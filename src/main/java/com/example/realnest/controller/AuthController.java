package com.example.realnest.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.realnest.entity.Login;
import com.example.realnest.service.AuthService;
import com.example.realnest.service.UserService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("api/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthService authservice;
    @Autowired
    private UserService userService;

    // @PostMapping("/sign-in")
    // public ResponseEntity<?> signIn(@RequestBody Login l) {
    // return ResponseEntity.ok(Map.of("token", authservice.signIn(l.getUsername(),
    // l.getPassword())));
    // }
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody Login l) {
        try {
            String token = authservice.signIn(l.getUsername(), l.getPassword());
            // return ResponseEntity.ok(Map.of("token", token));
            Optional<Login> userOptional = userService.getUserByUsername(l.getUsername());

            String role = userOptional.map(Login::getRole).orElse("ROLE_USER");
            String name = userOptional.map(Login::getName).orElse("NAME_USER");
            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "role", role,
                            "name", name));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    // @PostMapping("/sign-up")
    // public String signUp(@RequestBody Login l) {
    // return authservice.signUp(l);
    // }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody Login l) {
        try {
            if (l.getUsername() == null || l.getPassword() == null || l.getEmail() == null) {
                return ResponseEntity.badRequest().body("Username, email, and password are required");
            }

            String result = authservice.signUp(l);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error signing up: " + e.getMessage());
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllLogins() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/updateUser")
    public Login updateProperty(@RequestParam("id") Long id, @RequestBody Login upd) {
        Login updateProperty = userService.updateLogin(id, upd);
        return updateProperty;
    }

    // @DeleteMapping("/deleteUser")
    // public RealNest deleteProperty(@RequestParam("id") Long id) {
    // RealNest delprops = userService.deleteLogin(id);
    // // return "User deleted successfully with id: " + id;
    // return delprops
    // }
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam("id") Long id) {
        try {
            userService.deleteLogin(id);
            return ResponseEntity.ok("User deleted successfully with id: " + id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/viewUser")
    public Optional<Login> viewProperty(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }
}
