package DIMENSITY.SOFTWARE.Back_Catherino.dto;


import lombok.Data;
import lombok.Builder;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class TimeRangeRequest {
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private String label;
}