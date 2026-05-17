package DIMENSITY.SOFTWARE.Back_Catherino.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String code;
    private String name;
    private String category;
    private String size;
    private Integer stock;

    @Builder.Default
    private Double purchasePrice = 0.0;  // Precio de compra

    @Builder.Default
    private Double suggestedSalePrice = 0.0;  // Precio de venta sugerido

    private String color;
    private String brand;
    private String description;

    @Builder.Default
    private Boolean active = true;

    // Métodos de negocio
    public boolean hasStock(int quantity) {
        return this.stock != null && this.stock >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (hasStock(quantity)) {
            this.stock -= quantity;
        } else {
            throw new IllegalStateException("Insufficient stock");
        }
    }

    public void increaseStock(int quantity) {
        if (this.stock == null) {
            this.stock = quantity;
        } else {
            this.stock += quantity;
        }
    }

    // Calcular margen de ganancia
    public Double getProfitMargin() {
        if (purchasePrice == null || purchasePrice == 0 || suggestedSalePrice == null) {
            return 0.0;
        }
        return ((suggestedSalePrice - purchasePrice) / purchasePrice) * 100;
    }
}