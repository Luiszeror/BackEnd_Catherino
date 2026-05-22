package DIMENSITY.SOFTWARE.Back_Catherino.dto;


import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class SoldProductResponse {
    private String productId;
    private String productName;
    private String productCode;
    private Integer totalSold;
    private Double totalRevenue;
    private Double totalProfit;
}