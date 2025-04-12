package com.example.dacn.model;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "cv")
@Data
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}