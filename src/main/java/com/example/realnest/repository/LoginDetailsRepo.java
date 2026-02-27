package com.example.realnest.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.realnest.entity.Login;

@Repository
public interface LoginDetailsRepo extends JpaRepository<Login, Long>{
    Optional<Login> findByUsername(String username);
    Optional<Login> findByEmail(String email);
    // Optional<Login> findById(String id);
}
