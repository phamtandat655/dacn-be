package com.example.dacn.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    // private String role; // "user" hoặc "admin" => mặc định là user
}