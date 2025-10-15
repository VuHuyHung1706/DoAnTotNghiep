package com.web.recommendationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_history")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @Column(name = "similarity_score")
    private Double similarityScore;

    @Column(name = "recommended_at")
    @Builder.Default
    private LocalDateTime recommendedAt = LocalDateTime.now();

    @Column(name = "was_clicked")
    @Builder.Default
    private Boolean wasClicked = false;
}
