package com.example.realnest.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name="login_users")
public class Login {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @Column(nullable=true)
    private String name;
    @Column(nullable=true)
    private String phoneno;
    @Column(nullable=false,unique=true)
    private String email;
    @Column(nullable = false,unique=true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @CreationTimestamp
    @Column(name="created_at",nullable = false,updatable=false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Instant updatedAt;
}
