package com.web.recommendationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "genre_id")
    private Integer genreId;

    @Column(name = "genre_name")
    private String genreName;

    @Column(name = "actor_id")
    private Integer actorId;

    @Column(name = "actor_name")
    private String actorName;

    @Column(name = "preference_score")
    @Builder.Default
    private Double preferenceScore = 1.0;

    @Column(name = "last_updated")
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
