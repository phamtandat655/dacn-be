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
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private UserRepository userRepository;

    private Word2Vec word2Vec;

    public MatchService() {
        try {
            System.out.println("Đang load mô hình...");
            File modelFile = new File("src/main/resources/models/GoogleNews-vectors-negative300.bin.gz");
            System.out.println("Tồn tại file? " + modelFile.exists());

            this.word2Vec = WordVectorSerializer.readWord2VecModel(modelFile);

            System.out.println("Load thành công");
            System.out.println("Vector của 'java': " + this.word2Vec.getWordVectorMatrix("java"));
        } catch (Exception e) {
            System.err.println("Không tải được mô hình lớn, chuyển sang huấn luyện mô hình nhỏ...");
        }
    }

    public MatchResponseDTO matchCVWithJD(MatchRequestDTO request, String email) throws IOException {
        // 1. Trích xuất text từ file PDF CV
        String cvContent = preprocessText(extractTextFromPDF(request.getCvBase64()));
        String jdTitle = preprocessText(request.getTitle());
        String jdDescription = preprocessText(request.getDescription());
        List<String> jdKeywords = Arrays.stream(request.getKeywords().toLowerCase().split(","))
                .map(this::preprocessText)
                .collect(Collectors.toList());
        String jdCombinedText = jdTitle + " " + jdDescription + " " + String.join(" ", jdKeywords);

        // 2. Tính độ tương đồng bằng Word2Vec
        double keywordSimilarity = calculateSimilarity(jdKeywords.stream().collect(Collectors.joining(" ")), cvContent);
        double titleSimilarity = calculateSimilarity(jdTitle, cvContent);
        double descriptionSimilarity = calculateSimilarity(jdDescription, cvContent);
        double overallSimilarity = calculateSimilarity(jdCombinedText, cvContent);

        // 3. Tính điểm cuối cùng với trọng số
        double finalScore = (keywordSimilarity * 0.3) + (titleSimilarity * 0.2) +
                (descriptionSimilarity * 0.2) + (overallSimilarity * 0.3);
        double matchPercentage = Math.min(finalScore * 100, 100.0);

        // 4. Xác định từ khóa khớp
        List<String> matchedKeywords = jdKeywords.stream()
                .filter(keyword -> Arrays.stream(cvContent.split("\\s+"))
                        .anyMatch(word -> word.contains(keyword)))
                .collect(Collectors.toList());

        // 5. Lưu kết quả vào DB
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
        matchResult.setMatchedKeywords(String.join(",", matchedKeywords));
        matchResult.setMatchPercentage(matchPercentage);
        matchResult.setCv(cv);
        matchResult.setJob(job);
        matchResultRepository.save(matchResult);

        // 6. Trả về response
        MatchResponseDTO response = new MatchResponseDTO();
        response.setMatchedKeywords(matchResult.getMatchedKeywords());
        response.setMatchPercentage(matchPercentage);
        response.setCvContent(cvContent);
        response.setJdKeywords(request.getKeywords());

        return response;
    }

    private String preprocessText(String text) {
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

    // Tính độ tương đồng cosine giữa hai chuỗi văn bản
    private double calculateSimilarity(String text1, String text2) {
        INDArray vector1 = getSentenceVector(text1);
        INDArray vector2 = getSentenceVector(text2);

        if (vector1 == null || vector2 == null) {
            return 0.0; // Nếu không có vector, trả về 0
        }

        return Transforms.cosineSim(vector1, vector2);
    }

    // Chuyển một chuỗi văn bản thành vector trung bình của các từ
    private INDArray getSentenceVector(String text) {
        List<String> words = Arrays.asList(text.toLowerCase().split("\\s+"));
        INDArray sumVector = null;
        int wordCount = 0;

        for (String word : words) {
            if (word2Vec.hasWord(word)) {
                INDArray wordVector = Nd4j.create(word2Vec.getWordVector(word));
                if (sumVector == null) {
                    sumVector = wordVector.dup();
                } else {
                    sumVector.addi(wordVector);
                }
                wordCount++;
            }
        }

        if (sumVector != null && wordCount > 0) {
            return sumVector.div(wordCount); // Trung bình vector
        }
        return null;
    }
}