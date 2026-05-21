package DIMENSITY.SOFTWARE.Back_Catherino.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    private String productId;
    private String productName;
    private String productCode;
    private Integer quantity;
    private Double unitPrice;  // Precio de venta final
    private Double suggestedPrice;  // Precio sugerido (solo para referencia)
    private Double purchasePrice;  // Precio de compra (solo para referencia)
    private Double subtotal;
    private Double profit;  // Ganancia por este item

    // Constructor de negocio actualizado
    public InvoiceDetail(String productId, String productName, String productCode,
                         Integer quantity, Double unitPrice, Double suggestedPrice,
                         Double purchasePrice) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.suggestedPrice = suggestedPrice;
        this.purchasePrice = purchasePrice;
        calculateFinancials();
    }

    // Método para calcular subtotal y ganancia
    public void calculateFinancials() {
        if (this.quantity != null && this.unitPrice != null) {
            this.subtotal = this.quantity * this.unitPrice;

            // Calcular ganancia si tenemos precio de compra
            if (this.purchasePrice != null && this.purchasePrice > 0) {
                this.profit = (this.unitPrice - this.purchasePrice) * this.quantity;
            } else {
                this.profit = 0.0;
            }
        }
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateFinancials();
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        calculateFinancials();
    }
}