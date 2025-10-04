package com.web.bookingservice.repository;


import com.web.bookingservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    boolean existsByShowtimeIdAndSeatIdAndStatus(Integer showtimeId, Integer seatId, Boolean status);
    List<Ticket> findByInvoiceId(Integer invoiceId);
    List<Ticket> findByShowtimeIdAndStatus(Integer showtimeId, Boolean status);
    // 1. Doanh thu theo RẠP
    @Query(value = """
        SELECT 
            r.cinema_id AS cinemaId,
            c.name AS cinemaName,
            SUM(t.price) AS totalRevenue,
            COUNT(t.id) AS totalTicketsSold
        FROM tickets t
        JOIN invoices i ON i.invoice_id = t.invoice_id
        JOIN showtimes s ON s.id = t.showtime_id
        JOIN rooms r ON r.id = s.room_id
        JOIN cinemas c ON c.id = r.cinema_id
        WHERE i.payment_status = 'PAID'
          AND i.paid_at BETWEEN :fromDate AND :toDate
        GROUP BY r.cinema_id, c.name
    """, nativeQuery = true)
    List<Object[]> getRevenueByCinema(@Param("fromDate") LocalDateTime fromDate,
                                      @Param("toDate") LocalDateTime toDate);

    // 2. Doanh thu theo PHIM
    @Query(value = """
        SELECT 
            s.movie_id AS movieId,
            m.title AS movieTitle,
            SUM(t.price) AS totalRevenue,
            COUNT(t.id) AS totalTicketsSold
        FROM tickets t
        JOIN invoices i ON i.invoice_id = t.invoice_id
        JOIN showtimes s ON s.id = t.showtime_id
        JOIN movies m ON m.id = s.movie_id
        WHERE i.payment_status = 'PAID'
          AND i.paid_at BETWEEN :fromDate AND :toDate
        GROUP BY s.movie_id, m.title
    """, nativeQuery = true)
    List<Object[]> getRevenueByMovie(@Param("fromDate") LocalDateTime fromDate,
                                     @Param("toDate") LocalDateTime toDate);

    // 3. Doanh thu theo NGÀY
    @Query(value = """
        SELECT 
            i.paid_at AS paidDate,
            SUM(t.price) AS totalRevenue,
            COUNT(t.id) AS totalTicketsSold
        FROM tickets t
        JOIN invoices i ON i.invoice_id = t.invoice_id
        WHERE i.payment_status = 'PAID'
          AND i.paid_at BETWEEN :fromDate AND :toDate
        GROUP BY paidDate
        ORDER BY paidDate
    """, nativeQuery = true)
    List<Object[]> getRevenueByDay(@Param("fromDate") LocalDateTime fromDate,
                                   @Param("toDate") LocalDateTime toDate);

    // 4. Doanh thu theo THÁNG
    @Query(value = """
        SELECT 
            YEAR(i.paid_at) AS year,
            MONTH(i.paid_at) AS month,
            SUM(t.price) AS totalRevenue,
            COUNT(t.id) AS totalTicketsSold
        FROM tickets t
        JOIN invoices i ON i.invoice_id = t.invoice_id
        WHERE i.payment_status = 'PAID'
          AND i.paid_at BETWEEN :fromDate AND :toDate
        GROUP BY year, month
        ORDER BY year, month
    """, nativeQuery = true)
    List<Object[]> getRevenueByMonth(@Param("fromDate") LocalDateTime fromDate,
                                     @Param("toDate") LocalDateTime toDate);

    // 5. Doanh thu theo QUÝ
    @Query(value = """
        SELECT 
            YEAR(i.paid_at) AS year,
            QUARTER(i.paid_at) AS quarter,
            SUM(t.price) AS totalRevenue,
            COUNT(t.id) AS totalTicketsSold
        FROM tickets t
        JOIN invoices i ON i.invoice_id = t.invoice_id
        WHERE i.payment_status = 'PAID'
          AND i.paid_at BETWEEN :fromDate AND :toDate
        GROUP BY year, quarter
        ORDER BY year, quarter
    """, nativeQuery = true)
    List<Object[]> getRevenueByQuarter(@Param("fromDate") LocalDateTime fromDate,
                                       @Param("toDate") LocalDateTime toDate);

    // 6. Doanh thu theo NĂM
    @Query(value = """
        SELECT 
            YEAR(i.paid_at) AS year,
            SUM(t.price) AS totalRevenue,
            COUNT(t.id) AS totalTicketsSold
        FROM tickets t
        JOIN invoices i ON i.invoice_id = t.invoice_id
        WHERE i.payment_status = 'PAID'
          AND i.paid_at BETWEEN :fromDate AND :toDate
        GROUP BY year
        ORDER BY year
    """, nativeQuery = true)
    List<Object[]> getRevenueByYear(@Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate);
}
