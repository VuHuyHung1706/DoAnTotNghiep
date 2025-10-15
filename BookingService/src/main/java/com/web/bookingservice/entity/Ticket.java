package com.web.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "showtime_id")
    private Integer showtimeId;

    @Column(name = "seat_id")
    private Integer seatId;

    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    @Builder.Default
    private Boolean status = false;

    @Column(name = "qr_code", unique = true)
    private String qrCode;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @Column(name = "is_scanned")
    @Builder.Default
    private Boolean isScanned = false;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "showtime_id", referencedColumnName = "id", insertable = false, updatable = false)
//    @ToString.Exclude
//    private Showtime showtime;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "seat_id", referencedColumnName = "id", insertable = false, updatable = false)
//    @ToString.Exclude
//    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Invoice invoice;
}
