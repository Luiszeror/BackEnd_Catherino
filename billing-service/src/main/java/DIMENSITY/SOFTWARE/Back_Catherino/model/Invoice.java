package DIMENSITY.SOFTWARE.Back_Catherino.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;

    private String invoiceNumber;
    private LocalDateTime issueDate;
    private Customer customer;
    private List<InvoiceDetail> details;
    private Double subtotal;
    private Double taxRate;
    private Double tax;
    private Double total;
    private Double totalProfit;  // Ganancia total de la venta

    @Builder.Default
    private String status = "ISSUED"; // ISSUED, CANCELLED

    private String cashier;

    // Métodos de negocio
    public boolean isCancelled() {
        return "CANCELLED".equals(this.status);
    }

    public boolean canBeCancelled() {
        return !isCancelled();
    }

    // Calcular ganancia total
    public void calculateTotalProfit() {
        if (this.details != null) {
            this.totalProfit = this.details.stream()
                    .mapToDouble(detail -> detail.getProfit() != null ? detail.getProfit() : 0.0)
                    .sum();
        } else {
            this.totalProfit = 0.0;
        }
    }
}