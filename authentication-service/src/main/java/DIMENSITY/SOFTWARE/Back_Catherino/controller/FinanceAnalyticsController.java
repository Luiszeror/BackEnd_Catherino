package DIMENSITY.SOFTWARE.Back_Catherino.controller;


import DIMENSITY.SOFTWARE.Back_Catherino.dto.*;
import DIMENSITY.SOFTWARE.Back_Catherino.service.FinanceAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceAnalyticsController {

    private final FinanceAnalyticsService financeService;

    /**
     * Devuelve todos los productos vendidos (JSON completo)
     */
    @GetMapping("/sold-products")
    public ResponseEntity<List<SoldProductResponse>> getAllSoldProducts() {
        try {
            List<SoldProductResponse> soldProducts = financeService.getAllSoldProducts();
            return ResponseEntity.ok(soldProducts);
        } catch (Exception e) {
            log.error("Error obteniendo productos vendidos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Devuelve cantidad de vendidos por producto
     */
    @GetMapping("/sold-products/{productId}")
    public ResponseEntity<SoldProductResponse> getSoldProductsByProductId(@PathVariable String productId) {
        try {
            SoldProductResponse response = financeService.getSoldProductsByProductId(productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo productos vendidos por ID: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Devuelve total de productos vendidos
     */
    @GetMapping("/total-products-sold")
    public ResponseEntity<Map<String, Long>> getTotalProductsSold() {
        try {
            Long total = financeService.getTotalProductsSold();
            return ResponseEntity.ok(Map.of("totalProductsSold", total));
        } catch (Exception e) {
            log.error("Error obteniendo total de productos vendidos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Devuelve ganancias en rango de tiempo
     */
    @PostMapping("/profits")
    public ResponseEntity<ProfitResponse> getProfitsByTimeRange(@Valid @RequestBody TimeRangeRequest request) {
        try {
            ProfitResponse response = financeService.getProfitsByTimeRange(
                    request.getStartDate(), request.getEndDate());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo ganancias por rango: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Comparación por número de ventas en rangos de tiempo
     */
    @PostMapping("/compare/sales")
    public ResponseEntity<List<TimeRangeComparison>> compareSales(@Valid @RequestBody List<TimeRangeRequest> timeRanges) {
        try {
            List<TimeRangeComparison> comparison = financeService.compareSalesByTimeRanges(timeRanges);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            log.error("Error comparando ventas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}