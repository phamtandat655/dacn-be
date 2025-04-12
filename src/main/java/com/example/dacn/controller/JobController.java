package com.example.dacn.controller;

import com.example.dacn.dto.ErrorResponse;
import com.example.dacn.dto.JobDTO;
import com.example.dacn.model.Job;
import com.example.dacn.model.User;
import com.example.dacn.repository.UserRepository;
import com.example.dacn.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    @Autowired
    public JobController(JobService jobService, UserRepository userRepository) {
        this.jobService = jobService;
        this.userRepository = userRepository;
    }

    // Lấy tất cả các job
    @GetMapping
    public ResponseEntity<?> getAllJobs() {
        try {
            List<Job> jobs = jobService.getAllJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve jobs"));
        }
    }

    // Lấy job theo UserId
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getJobsByUserId(@PathVariable Long userId) {
        try {
            List<Job> jobs = jobService.getJobsByUserId(userId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve jobs for user " + userId));
        }
    }

    // Lấy job theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            Optional<Job> job = jobService.getJobById(id);
            return job.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve job with id " + id));
        }
    }

    // Tạo job mới
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobDTO job) {
        try {
            User user = userRepository.findById(job.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User with id " + job.getUserId() + " not found"));
            }

            Job jobModel = Job.builder()
                    .description(job.getDescription())
                    .title(job.getTitle())
                    .keywords(job.getKeywords())
                    .user(user)
                    .build();
            Job createdJob = jobService.createJob(jobModel);
            return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create job"));
        }
    }

    // Cập nhật job theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody JobDTO job) {
        try {
            User user = userRepository.findById(job.getUserId()).orElse(null);
            Job jobDetails = Job.builder()
                    .user(user)
                    .description(job.getDescription())
                    .keywords(job.getKeywords())
                    .title(job.getTitle())
                    .build();
            Job updatedJob = jobService.updateJob(id, jobDetails);
            return ResponseEntity.ok(updatedJob);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update job with id " + id));
        }
    }

    // Xóa job theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete job with id " + id));
        }
    }
}
