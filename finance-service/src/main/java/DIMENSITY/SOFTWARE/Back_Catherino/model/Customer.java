package DIMENSITY.SOFTWARE.Back_Catherino.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Customer {
    private String documentNumber;
    private String name;
    private String email;
    private String phone;
    private String address;

    public boolean isFinalConsumer() {
        return "NULL".equals(this.documentNumber);
    }
}