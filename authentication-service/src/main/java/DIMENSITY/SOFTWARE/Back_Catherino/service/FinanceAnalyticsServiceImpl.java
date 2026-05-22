package DIMENSITY.SOFTWARE.Back_Catherino.service;


import DIMENSITY.SOFTWARE.Back_Catherino.dto.*;
import DIMENSITY.SOFTWARE.Back_Catherino.model.*;
import DIMENSITY.SOFTWARE.Back_Catherino.repository.InvoiceFinanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceAnalyticsServiceImpl implements FinanceAnalyticsService {

    private final InvoiceFinanceRepository invoiceRepository;

    @Override
    public List<SoldProductResponse> getAllSoldProducts() {
        log.info("Obteniendo todos los productos vendidos");

        List<Invoice> allInvoices = invoiceRepository.findByStatus("ISSUED");

        Map<String, SoldProductResponse> productSales = new HashMap<>();

        for (Invoice invoice : allInvoices) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                String productId = detail.getProductId();

                SoldProductResponse current = productSales.getOrDefault(productId,
                        SoldProductResponse.builder()
                                .productId(productId)
                                .productName(detail.getProductName())
                                .productCode(detail.getProductCode())
                                .totalSold(0)
                                .totalRevenue(0.0)
                                .totalProfit(0.0)
                                .build());

                current.setTotalSold(current.getTotalSold() + detail.getQuantity());
                current.setTotalRevenue(current.getTotalRevenue() + detail.getSubtotal());
                current.setTotalProfit(current.getTotalProfit() +
                        (detail.getProfit() != null ? detail.getProfit() : 0.0));

                productSales.put(productId, current);
            }
        }

        return new ArrayList<>(productSales.values());
    }

    @Override
    public SoldProductResponse getSoldProductsByProductId(String productId) {
        log.info("Obteniendo productos vendidos para: {}", productId);

        List<Invoice> invoices = invoiceRepository.findByStatus("ISSUED");

        SoldProductResponse response = SoldProductResponse.builder()
                .productId(productId)
                .totalSold(0)
                .totalRevenue(0.0)
                .totalProfit(0.0)
                .build();

        for (Invoice invoice : invoices) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                if (productId.equals(detail.getProductId())) {
                    response.setTotalSold(response.getTotalSold() + detail.getQuantity());
                    response.setTotalRevenue(response.getTotalRevenue() + detail.getSubtotal());
                    response.setTotalProfit(response.getTotalProfit() +
                            (detail.getProfit() != null ? detail.getProfit() : 0.0));

                    // Solo necesitamos obtener nombre y código una vez
                    if (response.getProductName() == null) {
                        response.setProductName(detail.getProductName());
                        response.setProductCode(detail.getProductCode());
                    }
                }
            }
        }

        return response;
    }

    @Override
    public Long getTotalProductsSold() {
        log.info("Calculando total de productos vendidos");

        List<Invoice> invoices = invoiceRepository.findByStatus("ISSUED");

        return invoices.stream()
                .flatMap(invoice -> invoice.getDetails().stream())
                .mapToLong(InvoiceDetail::getQuantity)
                .sum();
    }

    @Override
    public ProfitResponse getProfitsByTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculando ganancias desde {} hasta {}", startDate, endDate);

        List<Invoice> invoices = invoiceRepository.findByIssueDateBetweenAndStatus(
                startDate, endDate, "ISSUED");

        Double totalProfit = invoices.stream()
                .mapToDouble(invoice -> invoice.getTotalProfit() != null ? invoice.getTotalProfit() : 0.0)
                .sum();

        Long totalProductsSold = invoices.stream()
                .flatMap(invoice -> invoice.getDetails().stream())
                .mapToLong(InvoiceDetail::getQuantity)
                .sum();

        return ProfitResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalProfit(totalProfit)
                .totalInvoices(invoices.size())
                .totalProductsSold(totalProductsSold)
                .build();
    }

    @Override
    public List<TimeRangeComparison> compareSalesByTimeRanges(List<TimeRangeRequest> timeRanges) {
        log.info("Comparando número de ventas por {} rangos de tiempo", timeRanges.size());

        return timeRanges.stream()
                .map(range -> buildTimeRangeComparison(range))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeRangeComparison> compareProfitsByTimeRanges(List<TimeRangeRequest> timeRanges) {
        log.info("Comparando ganancias por {} rangos de tiempo", timeRanges.size());

        return timeRanges.stream()
                .map(range -> buildTimeRangeComparison(range))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeRangeComparison> compareRevenueByTimeRanges(List<TimeRangeRequest> timeRanges) {
        log.info("Comparando ingresos por {} rangos de tiempo", timeRanges.size());

        return timeRanges.stream()
                .map(range -> buildTimeRangeComparison(range))
                .collect(Collectors.toList());
    }

    @Override
    public FinancialSummary getFinancialSummary() {
        log.info("Generando resumen financiero general");

        List<Invoice> allInvoices = invoiceRepository.findByStatus("ISSUED");

        Long totalProductsSold = allInvoices.stream()
                .flatMap(invoice -> invoice.getDetails().stream())
                .mapToLong(InvoiceDetail::getQuantity)
                .sum();

        Double totalRevenue = allInvoices.stream()
                .mapToDouble(Invoice::getTotal)
                .sum();

        Double totalProfit = allInvoices.stream()
                .mapToDouble(invoice -> invoice.getTotalProfit() != null ? invoice.getTotalProfit() : 0.0)
                .sum();

        Double averageSaleValue = allInvoices.isEmpty() ? 0.0 : totalRevenue / allInvoices.size();

        return FinancialSummary.builder()
                .totalInvoices((long) allInvoices.size())
                .totalProductsSold(totalProductsSold)
                .totalRevenue(totalRevenue)
                .totalProfit(totalProfit)
                .averageSaleValue(averageSaleValue)
                .build();
    }

    private TimeRangeComparison buildTimeRangeComparison(TimeRangeRequest range) {
        List<Invoice> invoices = invoiceRepository.findByIssueDateBetweenAndStatus(
                range.getStartDate(), range.getEndDate(), "ISSUED");

        Double totalProfit = invoices.stream()
                .mapToDouble(invoice -> invoice.getTotalProfit() != null ? invoice.getTotalProfit() : 0.0)
                .sum();

        Long totalProducts = invoices.stream()
                .flatMap(invoice -> invoice.getDetails().stream())
                .mapToLong(InvoiceDetail::getQuantity)
                .sum();

        return TimeRangeComparison.builder()
                .startDate(range.getStartDate())
                .endDate(range.getEndDate())
                .label(range.getLabel())
                .totalSales(invoices.size())
                .totalRevenue(invoices.stream().mapToDouble(Invoice::getTotal).sum())
                .totalProfit(totalProfit)
                .totalProductsSold(totalProducts)
                .build();
    }
}