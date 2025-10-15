package com.web.bookingservice.entity;

import com.web.bookingservice.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "vnpay_transaction_id")
    private String vnpayTransactionId;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
//    @ToString.Exclude
//    private Account account;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Ticket> tickets;
}
