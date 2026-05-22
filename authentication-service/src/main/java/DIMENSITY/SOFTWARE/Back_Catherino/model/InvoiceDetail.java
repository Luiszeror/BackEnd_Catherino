package DIMENSITY.SOFTWARE.Back_Catherino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    private String productId;
    private String productName;
    private String productCode;
    private Integer quantity;
    private Double unitPrice;
    private Double suggestedPrice;
    private Double purchasePrice;
    private Double subtotal;
    private Double profit;

}