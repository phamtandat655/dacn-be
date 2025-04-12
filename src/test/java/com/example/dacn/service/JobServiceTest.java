package com.example.dacn.service;

import com.example.dacn.model.Job;
import com.example.dacn.model.User;
import com.example.dacn.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobServiceTest {

    private JobRepository jobRepository;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobRepository = mock(JobRepository.class);
        jobService = new JobService(jobRepository);
    }

    @Test
    void testGetJobsByUserId() {
        Long userId = 1L;
        List<Job> jobs = List.of(new Job(), new Job());
        when(jobRepository.findByUserId(userId)).thenReturn(jobs);

        List<Job> result = jobService.getJobsByUserId(userId);

        assertEquals(2, result.size());
        verify(jobRepository).findByUserId(userId);
    }

    @Test
    void testGetAllJobs() {
        List<Job> jobs = List.of(new Job(), new Job(), new Job());
        when(jobRepository.findAll()).thenReturn(jobs);

        List<Job> result = jobService.getAllJobs();

        assertEquals(3, result.size());
        verify(jobRepository).findAll();
    }

    @Test
    void testGetJobById() {
        Long jobId = 1L;
        Job job = new Job();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        Optional<Job> result = jobService.getJobById(jobId);

        assertTrue(result.isPresent());
        verify(jobRepository).findById(jobId);
    }

    @Test
    void testCreateJob() {
        Job job = new Job();
        when(jobRepository.save(job)).thenReturn(job);

        Job result = jobService.createJob(job);

        assertEquals(job, result);
        verify(jobRepository).save(job);
    }

    @Test
    void testUpdateJob() {
        Long jobId = 1L;
        Job existingJob = new Job();
        existingJob.setId(jobId);
        existingJob.setTitle("Old Title");

        Job updatedJob = new Job();
        updatedJob.setTitle("New Title");
        updatedJob.setDescription("New Desc");
        updatedJob.setKeywords("New Keywords");
        updatedJob.setUser(new User());

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        when(jobRepository.save(any(Job.class))).thenReturn(existingJob);

        Job result = jobService.updateJob(jobId, updatedJob);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        verify(jobRepository).save(existingJob);
    }

    @Test
    void testDeleteJob() {
        Long jobId = 1L;
        doNothing().when(jobRepository).deleteById(jobId);

        jobService.deleteJob(jobId);

        verify(jobRepository).deleteById(jobId);
    }

    @Test
    void testUpdateJob_JobNotFound() {
        Long jobId = 99L;
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                jobService.updateJob(jobId, new Job()));

        assertEquals("Job not found", exception.getMessage());
    }
}
