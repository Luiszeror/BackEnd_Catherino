package DIMENSITY.SOFTWARE.Back_Catherino.controller;

import  DIMENSITY.SOFTWARE.Back_Catherino.model.Invoice;
import  DIMENSITY.SOFTWARE.Back_Catherino.model.Product;
import  DIMENSITY.SOFTWARE.Back_Catherino.model.SaleRequest;
import  DIMENSITY.SOFTWARE.Back_Catherino.service.InvoiceService;
import  DIMENSITY.SOFTWARE.Back_Catherino.repository.ProductRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/pos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PointOfSaleController {

    private final InvoiceService invoiceService;
    private final ProductRepository productRepository;

    // Nuevo endpoint para obtener información completa del producto con precios
    @GetMapping("/products/{code}/pricing")
    public ResponseEntity<?> getProductPricing(@PathVariable String code) {
        try {
            Optional<Product> product = productRepository.findByCode(code);
            if (product.isPresent()) {
                // Crear DTO con información de precios
                ProductPricingResponse pricing = ProductPricingResponse.builder()
                        .productId(product.get().getId())
                        .code(product.get().getCode())
                        .name(product.get().getName())
                        .purchasePrice(product.get().getPurchasePrice())
                        .suggestedSalePrice(product.get().getSuggestedSalePrice())
                        .currentStock(product.get().getStock())
                        .profitMargin(product.get().getProfitMargin())
                        .build();

                return ResponseEntity.ok(pricing);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving product pricing: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Error retrieving product pricing"));
        }
    }

        @PostMapping("/sale")
    public ResponseEntity<?> processSale(@Valid @RequestBody SaleRequest saleRequest) {
        try {
            log.debug("Processing sale request for cashier: {}", saleRequest.getCashier());
            Invoice invoice = invoiceService.processSale(saleRequest);
            return ResponseEntity.ok(invoice);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in sale request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponse.of(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error processing sale: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Internal server error"));
        }
    }

    @GetMapping("/products/{code}")
    public ResponseEntity<?> getProductByCode(@PathVariable String code) {
        try {
            Optional<Product> product = productRepository.findByCode(code);
            return product.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving product: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Error retrieving product"));
        }
    }

    @GetMapping("/products/search/{name}")
    public ResponseEntity<?> searchProductsByName(@PathVariable String name) {
        try {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Error searching products"));
        }
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable String id) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            return ResponseEntity.ok(invoice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving invoice: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Error retrieving invoice"));
        }
    }

    @PutMapping("/invoices/{id}/cancel")
    public ResponseEntity<?> cancelInvoice(@PathVariable String id) {
        try {
            Invoice invoice = invoiceService.cancelInvoice(id);
            return ResponseEntity.ok(invoice);
        } catch (IllegalArgumentException e) {
            log.warn("Cannot cancel invoice: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponse.of(e.getMessage()));
        } catch (Exception e) {
            log.error("Error cancelling invoice: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Error cancelling invoice"));
        }
    }

    @GetMapping("/invoices/recent")
    public ResponseEntity<List<Invoice>> getRecentInvoices() {
        try {
            List<Invoice> recentInvoices = invoiceService.getRecentInvoices();
            return ResponseEntity.ok(recentInvoices);
        } catch (Exception e) {
            log.error("Error retrieving recent invoices: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public record ErrorResponse(String error) {
        public static ErrorResponse of(String error) {
            return new ErrorResponse(error);
        }
    }

    @Data
    @Builder
    public static class ProductPricingResponse {
        private String productId;
        private String code;
        private String name;
        private Double purchasePrice;
        private Double suggestedSalePrice;
        private Integer currentStock;
        private Double profitMargin;
    }
}