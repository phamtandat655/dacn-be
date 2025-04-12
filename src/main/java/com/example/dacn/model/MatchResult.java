package com.example.dacn.model;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "match_result")
@Data
public class MatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "matched_keywords")
    private String matchedKeywords;

    @Column(name = "match_percentage", nullable = false)
    private double matchPercentage;

    @ManyToOne
    @JoinColumn(name = "cv_id", nullable = false)
    private CV cv;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
