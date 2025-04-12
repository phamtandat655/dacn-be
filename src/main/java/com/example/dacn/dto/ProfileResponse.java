package com.example.dacn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
}