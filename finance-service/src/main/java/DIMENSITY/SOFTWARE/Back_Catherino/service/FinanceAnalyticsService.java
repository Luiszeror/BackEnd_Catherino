package DIMENSITY.SOFTWARE.Back_Catherino.service;


import  DIMENSITY.SOFTWARE.Back_Catherino.dto.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

public interface FinanceAnalyticsService {

    /**
     * Devuelve todos los productos vendidos (JSON completo)
     */
    List<SoldProductResponse> getAllSoldProducts();

    /**
     * Devuelve cantidad de productos vendidos por ID de producto
     */
    SoldProductResponse getSoldProductsByProductId(String productId);

    /**
     * Devuelve el total de productos vendidos
     */
    Long getTotalProductsSold();

    /**
     * Devuelve ganancias en un rango de tiempo
     */
    ProfitResponse getProfitsByTimeRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Comparación por número de ventas en rangos de tiempo
     */
    List<TimeRangeComparison> compareSalesByTimeRanges(List<TimeRangeRequest> timeRanges);

    /**
     * Comparación por ganancias en rangos de tiempo
     */
    List<TimeRangeComparison> compareProfitsByTimeRanges(List<TimeRangeRequest> timeRanges);

    /**
     * Comparación por total dinero en ventas en rangos de tiempo
     */
    List<TimeRangeComparison> compareRevenueByTimeRanges(List<TimeRangeRequest> timeRanges);

    /**
     * Obtener resumen financiero general
     */
    FinancialSummary getFinancialSummary();
}

// DTO adicional para resumen financiero
@Data
@Builder
class FinancialSummary {
    private Long totalInvoices;
    private Long totalProductsSold;
    private Double totalRevenue;
    private Double totalProfit;
    private Double averageSaleValue;
}