package DIMENSITY.SOFTWARE.Back_Catherino.controller;

import  DIMENSITY.SOFTWARE.Back_Catherino.model.Invoice;
import  DIMENSITY.SOFTWARE.Back_Catherino.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving all invoices: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        try {
            Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
            return ResponseEntity.ok(invoice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving invoice by number: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(PointOfSaleController.ErrorResponse.of("Error retrieving invoice"));
        }
    }

    @GetMapping("/customer/{documentNumber}")
    public ResponseEntity<List<Invoice>> getInvoicesByCustomer(@PathVariable String documentNumber) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByCustomer(documentNumber);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving customer invoices: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Invoice>> getInvoicesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving invoices by date range: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByStatus(status);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving invoices by status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}