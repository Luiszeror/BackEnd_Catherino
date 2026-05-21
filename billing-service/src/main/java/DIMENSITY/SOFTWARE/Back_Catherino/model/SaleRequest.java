package DIMENSITY.SOFTWARE.Back_Catherino.model;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {

    @Valid
    private Customer customer;

    @NotEmpty(message = "Items cannot be empty")
    private List<@Valid SaleItem> items;

    @NotNull(message = "Cashier is required")
    private String cashier;

    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @Builder.Default
    private Double taxRate = 0.0;  // Valor por defecto


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleItem {
        @NotNull(message = "Product ID is required")
        private String productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Sale price is required")
        private Double salePrice;  // Precio de venta final ingresado por el cajero
    }
}