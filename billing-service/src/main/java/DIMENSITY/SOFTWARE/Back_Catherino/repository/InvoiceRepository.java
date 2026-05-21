package DIMENSITY.SOFTWARE.Back_Catherino.repository;

import DIMENSITY.SOFTWARE.Back_Catherino.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerDocumentNumber(String documentNumber);

    List<Invoice> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Invoice> findByStatus(String status);

    List<Invoice> findByCashier(String cashier);

    @Query("{ 'issueDate' : { $gte: ?0, $lte: ?1 } }")
    List<Invoice> findInvoicesByDateRange(LocalDateTime start, LocalDateTime end);

    Long countByStatus(String status);

    @Query(value = "{}", sort = "{ 'issueDate' : -1 }")
    List<Invoice> findTop10ByOrderByIssueDateDesc();
}