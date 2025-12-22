package com.web.bookingservice.repository;

import com.web.bookingservice.constant.PaymentStatus;
import com.web.bookingservice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByUsername(String username);
    Invoice findByVnpayTransactionId(String vnpayTransactionId);
    List<Invoice> findByUsernameAndPaymentStatus(String username, PaymentStatus paymentStatus);
    List<Invoice> findByPaymentStatusAndPaidAtBetween(PaymentStatus paymentStatus, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.username, 8) AS int)), 0) FROM Invoice i WHERE i.username LIKE 'vanglai%'")
    Integer findMaxGuestId();
}
