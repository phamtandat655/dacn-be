package com.example.dacn.dto;
import lombok.Data;

@Data
public class MatchRequestDTO {
    private String title;
    private String description;
    private String keywords;
    private String cvBase64; // File PDF được gửi dưới dạng base64
}
