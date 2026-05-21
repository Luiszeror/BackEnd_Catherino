package DIMENSITY.SOFTWARE.Back_Catherino.service;

import  DIMENSITY.SOFTWARE.Back_Catherino.model.Invoice;
import DIMENSITY.SOFTWARE.Back_Catherino.model.InvoiceDetail;
import  DIMENSITY.SOFTWARE.Back_Catherino.model.Product;
import  DIMENSITY.SOFTWARE.Back_Catherino.model.SaleRequest;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {

    Invoice processSale(SaleRequest saleRequest);

    List<Invoice> getAllInvoices();

    Invoice getInvoiceById(String id);

    Invoice getInvoiceByNumber(String invoiceNumber);

    List<Invoice> getInvoicesByCustomer(String documentNumber);

    List<Invoice> getInvoicesByDateRange(String startDate, String endDate);

    List<Invoice> getInvoicesByStatus(String status);

    List<Invoice> getInvoicesByCashier(String cashier);

    List<Invoice> getRecentInvoices();

    Invoice cancelInvoice(String id);

    Invoice updateInvoiceStatus(String id, String status);

    void updateInventory(List<InvoiceDetail> details, boolean isCancellation);

    Double getDailySalesTotal(String date);

    Double getTotalProfitByDateRange(String startDate, String endDate);

    List<Product> getTopSellingProducts(int limit);

    Optional<Invoice> findInvoiceByCriteria(String invoiceNumber, String customerDocument, String status);
}