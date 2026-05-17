package DIMENSITY.SOFTWARE.Back_Catherino.repository;


import DIMENSITY.SOFTWARE.Back_Catherino.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceFinanceRepository extends MongoRepository<Invoice, String> {

    /**
     * Encontrar facturas por rango de fecha y estado
     */
    List<Invoice> findByIssueDateBetweenAndStatus(
            LocalDateTime startDate, LocalDateTime endDate, String status);

    /**
     * Encontrar todas las facturas por estado
     */
    List<Invoice> findByStatus(String status);

    /**
     * Consulta agregada para productos más vendidos
     */
    @Query(value = "{ 'status': 'ISSUED' }", fields = "{ 'details': 1 }")
    List<Invoice> findIssuedInvoicesWithDetails();

    /**
     * Consulta para ganancias totales por rango de fecha
     */
    @Query(value = "{ 'issueDate': { $gte: ?0, $lte: ?1 }, 'status': 'ISSUED' }",
            fields = "{ 'totalProfit': 1, 'total': 1, 'details': 1 }")
    List<Invoice> findFinancialDataByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}