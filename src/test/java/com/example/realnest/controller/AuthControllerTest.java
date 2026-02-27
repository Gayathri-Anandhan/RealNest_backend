package com.example.realnest.controller;

import com.example.realnest.entity.Login;
import com.example.realnest.service.AuthService;
import com.example.realnest.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // disables JWT/security filters
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authservice;

    @MockBean
    private UserService userService;

    // ✅ SIGN-IN SUCCESS
    @Test
    void shouldSignInSuccessfully() throws Exception {

        String request = """
                {
                  "username": "john",
                  "password": "1234"
                }
                """;

        when(authservice.signIn("john", "1234"))
                .thenReturn("dummy-token");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    // ✅ SIGN-IN FAILURE (401)
    @Test
    void shouldReturn401WhenUserNotFound() throws Exception {

        String request = """
                {
                  "username": "wrong",
                  "password": "1234"
                }
                """;

        when(authservice.signIn("wrong", "1234"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    // ✅ SIGN-UP SUCCESS
    @Test
    void shouldSignUpSuccessfully() throws Exception {

        String request = """
                {
                  "username": "newuser",
                  "password": "1234",
                  "email": "test@gmail.com"
                }
                """;

        when(authservice.signUp(any(Login.class)))
                .thenReturn("User registered successfully");

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    // ✅ GET ALL USERS
    @Test
    void shouldReturnAllUsers() throws Exception {

        Login user = new Login();
        user.setId(1L);
        user.setUsername("john");

        when(userService.getAllUsers())
                .thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/auth/all-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    // ✅ UPDATE USER
    @Test
    void shouldUpdateUser() throws Exception {

        Login updated = new Login();
        updated.setId(1L);
        updated.setUsername("updatedUser");

        when(userService.updateLogin(anyLong(), any(Login.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/auth/updateUser")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    // ✅ DELETE USER
    @Test
    void shouldDeleteUser() throws Exception {

        mockMvc.perform(delete("/api/auth/deleteUser")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully with id: 1"));
    }

    // ✅ VIEW USER
    @Test
    void shouldReturnUserById() throws Exception {

        Login user = new Login();
        user.setId(1L);
        user.setUsername("john");

        when(userService.getUserById(1L))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/viewUser")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }
}