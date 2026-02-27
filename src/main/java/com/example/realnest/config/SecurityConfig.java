package com.example.realnest.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.realnest.service.UserService;

@Configuration
// @EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AuthFilter jwtAuthFilter;
    @Autowired
    @Lazy
    private UserService u;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
    .sessionManagement(sm ->
    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/properties/**").permitAll()
    .requestMatchers("/web/**").permitAll()
    .requestMatchers("/vite.svg").permitAll()
    .requestMatchers("/assets/**").permitAll()
    .requestMatchers("/v3/api-docs/**").permitAll()
    .requestMatchers("/swagger-ui/**").permitAll()
    .requestMatchers("/swagger-ui.html").permitAll()
    .anyRequest().permitAll());
    // .anyRequest().authenticated());

    http.addFilterBefore(jwtAuthFilter,
    UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
