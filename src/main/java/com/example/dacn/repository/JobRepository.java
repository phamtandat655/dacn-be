package com.example.dacn.repository;
import com.example.dacn.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByUserId(Long user_id);
}