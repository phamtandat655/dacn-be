package com.example.dacn.repository;
import com.example.dacn.model.Token;
import com.example.dacn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserOrderByCreatedAtAsc(User user);
    void deleteByToken(String token);
}
