package com.web.userservice.entity;

import com.web.userservice.enums.Position;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "managers")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    @OneToOne(fetch     = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @ToString.Exclude
    private Account account;
}
