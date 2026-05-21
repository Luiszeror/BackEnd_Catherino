package DIMENSITY.SOFTWARE.Back_Catherino.service;

import DIMENSITY.SOFTWARE.Back_Catherino.service.InvoiceService;
import DIMENSITY.SOFTWARE.Back_Catherino.model.*;
import DIMENSITY.SOFTWARE.Back_Catherino.repository.InvoiceRepository;
import DIMENSITY.SOFTWARE.Back_Catherino.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;


    private static final String ESTABLISHMENT = "001";
    private static final String POINT_OF_SALE = "001";

    @Override
    public Invoice processSale(SaleRequest saleRequest) {
        log.info("Processing sale for cashier: {}", saleRequest.getCashier());

        validateSaleRequest(saleRequest);
        List<InvoiceDetail> details = validateAndPrepareDetails(saleRequest.getItems());

        Invoice invoice = buildInvoice(saleRequest, details);
        calculateTotals(invoice,saleRequest.getTaxRate());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        updateInventory(details, false);

        log.info("Sale processed successfully. Invoice: {}. Total: {}, Profit: {}",
                savedInvoice.getInvoiceNumber(), savedInvoice.getTotal(), savedInvoice.getTotalProfit());
        return savedInvoice;
    }

    private void validateSaleRequest(SaleRequest saleRequest) {
        if (saleRequest.getItems() == null || saleRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Sale must contain at least one item");
        }

        if (saleRequest.getCashier() == null || saleRequest.getCashier().trim().isEmpty()) {
            throw new IllegalArgumentException("Cashier is required");
        }

        if (saleRequest.getTaxRate() == null || saleRequest.getTaxRate() < 0) {
            throw new IllegalArgumentException("Tax rate must be provided and cannot be negative");
        }

        // Validar que cada item tenga precio de venta
        for (SaleRequest.SaleItem item : saleRequest.getItems()) {
            if (item.getSalePrice() == null) {
                throw new IllegalArgumentException("Sale price is required for all items");
            }
            if (item.getSalePrice() <= 0) {
                throw new IllegalArgumentException("Sale price must be greater than zero");
            }
        }
    }

    private List<InvoiceDetail> validateAndPrepareDetails(List<SaleRequest.SaleItem> items) {
        List<InvoiceDetail> details = new ArrayList<>();

        for (SaleRequest.SaleItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

            validateProductForSale(product, item.getQuantity());

            // Validar que el precio de venta no sea menor al precio de compra
            if (product.getPurchasePrice() != null && item.getSalePrice() < product.getPurchasePrice()) {
                throw new IllegalArgumentException(
                        String.format("Sale price (%.2f) cannot be less than purchase price (%.2f) for product: %s",
                                item.getSalePrice(), product.getPurchasePrice(), product.getName())
                );
            }

            // Crear detalle con información financiera completa
            InvoiceDetail detail = new InvoiceDetail(
                    product.getId(),
                    product.getName(),
                    product.getCode(),
                    item.getQuantity(),
                    item.getSalePrice(),           // Precio final ingresado por cajero
                    product.getSuggestedSalePrice(), // Precio sugerido (referencia)
                    product.getPurchasePrice()     // Precio de compra (referencia)
            );

            details.add(detail);
        }

        return details;
    }

    private void validateProductForSale(Product product, Integer quantity) {
        if (!product.getActive()) {
            throw new IllegalArgumentException("Product is not active: " + product.getName());
        }

        if (!product.hasStock(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock for: %s. Available: %d, Requested: %d",
                            product.getName(), product.getStock(), quantity)
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private Invoice buildInvoice(SaleRequest saleRequest, List<InvoiceDetail> details) {
        Customer customer = saleRequest.getCustomer() != null ?
                saleRequest.getCustomer() : createGenericCustomer();

        return Invoice.builder()
                .issueDate(LocalDateTime.now())
                .invoiceNumber(generateInvoiceNumber())
                .customer(customer)
                .details(details)
                .cashier(saleRequest.getCashier())
                .taxRate(saleRequest.getTaxRate())
                .status("ISSUED")
                .build();
    }

    private Customer createGenericCustomer() {
        return Customer.builder()
                .documentNumber("NULL")
                .name("FINAL CONSUMER")
                .build();
    }

    private void calculateTotals(Invoice invoice , Double taxRate) {
        // Calcular subtotal
        Double subtotal = invoice.getDetails().stream()
                .mapToDouble(InvoiceDetail::getSubtotal)
                .sum();

        Double tax = subtotal * taxRate;
        Double total = subtotal + tax;

        invoice.setSubtotal(subtotal);
        invoice.setTax(tax);
        invoice.setTotal(total);

        // Calcular ganancia total
        invoice.calculateTotalProfit();
    }

    @Override
    public void updateInventory(List<InvoiceDetail> details, boolean isCancellation) {
        for (InvoiceDetail detail : details) {
            productRepository.findById(detail.getProductId()).ifPresent(product -> {
                int stockChange = isCancellation ? detail.getQuantity() : -detail.getQuantity();

                if (isCancellation) {
                    product.increaseStock(detail.getQuantity());
                } else {
                    product.decreaseStock(detail.getQuantity());
                }

                productRepository.save(product);
                log.debug("Updated stock for product: {}. Change: {}", product.getName(), stockChange);
            });
        }
    }

    private String generateInvoiceNumber() {
        Long sequential = invoiceRepository.count() + 1;
        return String.format("INV-%s-%s-%07d", ESTABLISHMENT, POINT_OF_SALE, sequential);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));
    }

    @Override
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceNumber));
    }

    @Override
    public List<Invoice> getInvoicesByCustomer(String documentNumber) {
        return invoiceRepository.findByCustomerDocumentNumber(documentNumber);
    }

    @Override
    public List<Invoice> getInvoicesByDateRange(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);

            return invoiceRepository.findByIssueDateBetween(start, end);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    @Override
    public List<Invoice> getInvoicesByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Valid values: ISSUED, CANCELLED");
        }
        return invoiceRepository.findByStatus(status);
    }

    @Override
    public List<Invoice> getRecentInvoices() {
        return invoiceRepository.findTop10ByOrderByIssueDateDesc();
    }

    @Override
    public Invoice cancelInvoice(String id) {
        Invoice invoice = getInvoiceById(id);

        if (invoice.isCancelled()) {
            throw new IllegalArgumentException("Invoice is already cancelled");
        }

        if (!invoice.canBeCancelled()) {
            throw new IllegalArgumentException("Invoice cannot be cancelled");
        }

        invoice.setStatus("CANCELLED");
        updateInventory(invoice.getDetails(), true);

        Invoice cancelledInvoice = invoiceRepository.save(invoice);
        log.info("Invoice cancelled: {}", cancelledInvoice.getInvoiceNumber());

        return cancelledInvoice;
    }

    @Override
    public List<Invoice> getInvoicesByCashier(String cashier) {
        if (cashier == null || cashier.trim().isEmpty()) {
            throw new IllegalArgumentException("Cashier name is required");
        }
        return invoiceRepository.findByCashier(cashier);
    }

    @Override
    public Double getDailySalesTotal(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(date + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(date + " 23:59:59", formatter);

            List<Invoice> dailyInvoices = invoiceRepository.findByIssueDateBetween(start, end);

            return dailyInvoices.stream()
                    .filter(invoice -> "ISSUED".equals(invoice.getStatus()))
                    .mapToDouble(Invoice::getTotal)
                    .sum();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    @Override
    public Double getTotalProfitByDateRange(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);

            List<Invoice> invoices = invoiceRepository.findByIssueDateBetween(start, end);

            return invoices.stream()
                    .filter(invoice -> "ISSUED".equals(invoice.getStatus()))
                    .mapToDouble(invoice -> invoice.getTotalProfit() != null ? invoice.getTotalProfit() : 0.0)
                    .sum();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    @Override
    public List<Product> getTopSellingProducts(int limit) {
        // Esta implementación es básica, en producción se debería usar aggregation pipeline
        List<Invoice> allInvoices = invoiceRepository.findAll();

        // Aquí se implementaría la lógica para calcular productos más vendidos
        // Por simplicidad, retornamos una lista vacía
        log.warn("getTopSellingProducts not fully implemented yet");
        return List.of();
    }

    private boolean isValidStatus(String status) {
        return "ISSUED".equals(status) || "CANCELLED".equals(status);
    }

    @Override
    public Invoice updateInvoiceStatus(String id, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Valid values: ISSUED, CANCELLED");
        }

        Invoice invoice = getInvoiceById(id);

        if (invoice.getStatus().equals(status)) {
            throw new IllegalArgumentException("Invoice already has status: " + status);
        }

        // Si se cancela una factura, revertir inventario
        if ("CANCELLED".equals(status)) {
            updateInventory(invoice.getDetails(), true);
        }
        // Si se reactiva una factura cancelada, actualizar inventario nuevamente
        else if ("ISSUED".equals(status) && "CANCELLED".equals(invoice.getStatus())) {
            updateInventory(invoice.getDetails(), false);
        }

        invoice.setStatus(status);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice {} status updated to: {}", updatedInvoice.getInvoiceNumber(), status);
        return updatedInvoice;
    }

    @Override
    public Optional<Invoice> findInvoiceByCriteria(String invoiceNumber, String customerDocument, String status) {
        if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
            return invoiceRepository.findByInvoiceNumber(invoiceNumber);
        }

        if (customerDocument != null && !customerDocument.trim().isEmpty()) {
            List<Invoice> customerInvoices = invoiceRepository.findByCustomerDocumentNumber(customerDocument);
            if (!customerInvoices.isEmpty()) {
                return Optional.of(customerInvoices.get(0));
            }
        }

        if (status != null && !status.trim().isEmpty() && isValidStatus(status)) {
            List<Invoice> statusInvoices = invoiceRepository.findByStatus(status);
            if (!statusInvoices.isEmpty()) {
                return Optional.of(statusInvoices.get(0));
            }
        }

        return Optional.empty();
    }
}