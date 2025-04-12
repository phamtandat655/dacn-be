package com.example.dacn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
    private String title;
    private String description;
    private String keywords;
    private Long userId;
}
