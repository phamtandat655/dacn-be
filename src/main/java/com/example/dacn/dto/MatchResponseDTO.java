package com.example.dacn.dto;
import lombok.Data;

@Data
public class MatchResponseDTO {
    private String matchedKeywords;
    private double matchPercentage;
    private String cvContent;
    private String jdKeywords;
}