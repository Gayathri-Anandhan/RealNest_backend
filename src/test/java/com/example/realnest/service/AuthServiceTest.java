package com.example.realnest.service;

import com.example.realnest.config.JwtUtil;
import com.example.realnest.entity.Login;
import com.example.realnest.repository.LoginDetailsRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private LoginDetailsRepo loginDetailsRepo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
        login.setUsername("testuser");
        login.setPassword("password123");
        login.setEmail("test@example.com");
    }

    // --------- SIGN UP TESTS ---------
    @Test
    void testSignUp_Success() {
        when(loginDetailsRepo.findByUsername(login.getUsername())).thenReturn(Optional.empty());
        when(loginDetailsRepo.findByEmail(login.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(login.getPassword())).thenReturn("encodedPassword");
        when(loginDetailsRepo.save(login)).thenReturn(login);

        String result = authService.signUp(login);

        assertEquals("User registered successfully", result);
        assertEquals("encodedPassword", login.getPassword());
        assertEquals("USER", login.getRole());
        verify(loginDetailsRepo, times(1)).save(login);
    }

    @Test
    void testSignUp_UsernameExists() {
        when(loginDetailsRepo.findByUsername(login.getUsername())).thenReturn(Optional.of(login));

        String result = authService.signUp(login);

        assertEquals("Username already exists", result);
        verify(loginDetailsRepo, never()).save(any());
    }

    @Test
    void testSignUp_EmailExists() {
        when(loginDetailsRepo.findByUsername(login.getUsername())).thenReturn(Optional.empty());
        when(loginDetailsRepo.findByEmail(login.getEmail())).thenReturn(Optional.of(login));

        String result = authService.signUp(login);

        assertEquals("Email already exists", result);
        verify(loginDetailsRepo, never()).save(any());
    }

    // --------- SIGN IN TESTS ---------
    @Test
    void testSignIn_Success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        String token = authService.signIn("testuser", "password123");

        assertEquals("jwt-token", token);
        verify(authManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testSignIn_Failure_ThrowsException() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.signIn("testuser", "wrongpassword"));

        assertEquals("Bad credentials", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any());
    }
}