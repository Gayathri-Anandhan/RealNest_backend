package com.example.realnest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.realnest.config.JwtUtil;
import com.example.realnest.entity.Login;
import com.example.realnest.repository.LoginDetailsRepo;

@Service
public class AuthService {
    @Autowired
    private LoginDetailsRepo loginDetailsRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String signUp(Login login) {
        if (loginDetailsRepo.findByUsername(login.getUsername()).isPresent()) {
            return "Username already exists";
        }
        if (loginDetailsRepo.findByEmail(login.getEmail()).isPresent()) {
            return "Email already exists";
        }
        login.setPassword(passwordEncoder.encode(login.getPassword()));
        if (login.getRole() == null || login.getRole().isEmpty()) {
            login.setRole("USER");
        }
        loginDetailsRepo.save(login);
        return "User registered successfully";
    }

    // public String signIn(String username,String password){
    // UsernamePasswordAuthenticationToken token=new
    // UsernamePasswordAuthenticationToken(username, password);
    // Authentication aut=authManager.authenticate(token);
    // return jwtUtil.generateToken(aut.getName());
    // }
    public String signIn(String username, String password) {
    // Authenticate using Spring Security
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
    );

    // If no exception, authentication is successful
    return jwtUtil.generateToken(auth.getName());
}
}
