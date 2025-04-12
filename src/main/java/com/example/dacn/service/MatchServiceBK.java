package com.example.dacn.service;

import com.example.dacn.dto.MatchRequestDTO;
import com.example.dacn.dto.MatchResponseDTO;
import com.example.dacn.model.CV;
import com.example.dacn.model.Job;
import com.example.dacn.model.MatchResult;
import com.example.dacn.model.User;
import com.example.dacn.repository.CVRepository;
import com.example.dacn.repository.JobRepository;
import com.example.dacn.repository.MatchResultRepository;
import com.example.dacn.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class MatchServiceBK {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private UserRepository userRepository;

    public MatchResponseDTO matchCVWithJD(MatchRequestDTO request, String email) throws IOException {
        // 1. Trích xuất text từ file PDF CV
        String cvContent = preprocessText(extractTextFromPDF(request.getCvBase64()));

        // 2. Chuẩn bị dữ liệu JD
        String jdTitle = request.getTitle().toLowerCase();
        String jdDescription = request.getDescription().toLowerCase();
        List<String> jdKeywords = Arrays.stream(request.getKeywords().toLowerCase().split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        String jdCombinedText = jdTitle + " " + jdDescription + " " + String.join(" ", jdKeywords);

        // 3. Logic khớp
        // 3.1. Tách từ trong CV và JD
        Set<String> cvWords = new HashSet<>(Arrays.asList(cvContent.split("\\s+")));
        Set<String> jdWords = new HashSet<>(Arrays.asList(jdCombinedText.split("\\s+")));

        // 3.2. Tính điểm từ khóa
        double keywordScore = calculateKeywordScore(jdKeywords, cvWords);

        // 3.3. Tính điểm title và description
        double titleScore = calculateTextScore(jdTitle, cvWords);
        double descriptionScore = calculateTextScore(jdDescription, cvWords);

        List<String> jdWordsList = jdWords.stream().toList();
        double overallScore = calculateKeywordScore(jdWordsList, cvWords);

        // 3.4. Tính điểm cuối cùng với trọng số
        double finalScore = (keywordScore * 0.3) + (titleScore * 0.3) + (descriptionScore * 0.2) + (overallScore * 0.2);
        double matchPercentage = Math.min(finalScore * 100, 100.0);

        // 4. Lưu kết quả vào DB
        User user = userRepository.findByEmail(email).orElse(null);
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setKeywords(request.getKeywords());
        job.setUser(user);
        job = jobRepository.save(job);

        CV cv = new CV();
        cv.setContent(cvContent);
        cv.setUser(user);
        cv = cvRepository.save(cv);

        MatchResult matchResult = new MatchResult();
        matchResult.setMatchedKeywords(String.join(",", jdKeywords.stream()
                .filter(cvWords::contains)
                .collect(Collectors.toList())));
        matchResult.setMatchPercentage(matchPercentage);
        matchResult.setCv(cv);
        matchResult.setJob(job);
        matchResultRepository.save(matchResult);

        // 5. Trả về response
        MatchResponseDTO response = new MatchResponseDTO();
        response.setMatchedKeywords(matchResult.getMatchedKeywords());
        response.setMatchPercentage(matchPercentage);
        response.setCvContent(cvContent);
        response.setJdKeywords(request.getKeywords());

        return response;
    }

    public String preprocessText(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String extractTextFromPDF(String base64String) throws IOException {
        byte[] pdfBytes;
        if (base64String.contains(",")) {
            pdfBytes = Base64.getDecoder().decode(base64String.split(",")[1]);
        } else {
            pdfBytes = Base64.getDecoder().decode(base64String);
        }
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // Tính điểm từ khóa
    public double calculateKeywordScore(List<String> keywords, Set<String> cvWords) {
        int matchedCount = (int) keywords.stream()
                .filter(keyword -> cvWords.stream().anyMatch(word -> word.contains(keyword)))
                .count();
        return (double) matchedCount / keywords.size();
    }

    // Tính điểm cho title hoặc description
    public double calculateTextScore(String jdText, Set<String> cvWords) {
        List<String> jdWords = Arrays.asList(jdText.split("\\s+"));
        int matchedCount = (int) jdWords.stream()
                .filter(word -> cvWords.stream().anyMatch(cvWord -> cvWord.contains(word)))
                .count();
        return (double) matchedCount / jdWords.size();
    }
}