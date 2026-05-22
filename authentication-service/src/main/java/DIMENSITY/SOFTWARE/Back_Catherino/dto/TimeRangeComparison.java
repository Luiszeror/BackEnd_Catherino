package DIMENSITY.SOFTWARE.Back_Catherino.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class TimeRangeComparison {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String label;
    private Integer totalSales;
    private Double totalRevenue;
    private Double totalProfit;
    private Long totalProductsSold;
}