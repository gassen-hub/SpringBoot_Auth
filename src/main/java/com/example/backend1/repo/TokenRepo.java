package com.example.backend1.repo;

import com.example.backend1.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TokenRepo extends JpaRepository<Token,Integer> {

    Optional<Token> findByToken(String token);
}
