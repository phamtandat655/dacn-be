package com.example.dacn.controller;

import com.example.dacn.dto.*;
import com.example.dacn.model.User;
import com.example.dacn.repository.UserRepository;
import com.example.dacn.service.CustomUserDetailsService;
import com.example.dacn.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByEmail(request.getEmail()).get();
            return ResponseEntity.ok(new LoginResponse(token, user));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Login failed: " + e.getMessage()));
        }
    }

    // API Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Email is already in use"));
            }

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole("user");
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userRepository.save(user);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    // API Get Profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User not found"));

            return ResponseEntity.ok(new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to get profile: " + e.getMessage()));
        }
    }

    // API Update Profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User not found"));

            if (request.getName() != null && !request.getName().isEmpty()) {
                user.setName(request.getName());
            }
            if (request.getPhone() != null && !request.getPhone().isEmpty()) {
                user.setPhone(request.getPhone());
            }

            userRepository.save(user);

            return ResponseEntity.ok(new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }
}