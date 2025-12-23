package com.web.cinemaservice.entity;

import com.web.cinemaservice.constant.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "seat_row")
    private Integer seatRow;

    @Column(name = "seat_column")
    private Integer seatColumn;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "room_id")
    private Integer roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Room room;
}
