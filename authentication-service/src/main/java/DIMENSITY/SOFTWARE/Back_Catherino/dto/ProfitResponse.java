package DIMENSITY.SOFTWARE.Back_Catherino.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class ProfitResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalProfit;
    private Integer totalInvoices;
    private Long totalProductsSold;
}