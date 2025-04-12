package com.example.dacn.controller;
import com.example.dacn.dto.MatchRequestDTO;
import com.example.dacn.dto.MatchResponseDTO;
import com.example.dacn.service.MatchServiceBK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchServiceBK matchService;

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/extract-pdf-info")
    public ResponseEntity<String> getPdfInfo(@RequestBody Map<String, String> map) {
        try {
            return ResponseEntity.ok(matchService.extractTextFromPDF(map.get("base64Pdf")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/matches")
    public ResponseEntity<?> matchCVWithJD(@RequestBody MatchRequestDTO request) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                System.out.println("Authenticated user: " + username); // For debugging

                // Pass userDetails or username to service if needed
                String email = userDetails.getUsername();
                MatchResponseDTO response = matchService.matchCVWithJD(request, email);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("User not authenticated properly");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
