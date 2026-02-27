package com.example.realnest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.example.realnest.entity.Login;
import com.example.realnest.repository.LoginDetailsRepo;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private LoginDetailsRepo loginDetailsRepo;

    public Login createLogin(Login l) {
        return loginDetailsRepo.save(l);
    }

    public List<Login> getAllUsers() {
        return loginDetailsRepo.findAll();
    }

    public Optional<Login> getUserById(Long id) {
        return loginDetailsRepo.findById(id);
    }

    public Optional<Login> getUserByUsername(String username) {
        return loginDetailsRepo.findByUsername(username);
    }

    public Login findByUsername(String username) {
        return loginDetailsRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Login updateLogin(Long id, Login upd) {
        Login exisLogin = loginDetailsRepo.findById(id).orElse(null);
        if (exisLogin == null) {
            throw new EntityNotFoundException("Login user not found with id-" + id);
        }
        exisLogin.setName(upd.getName());
        exisLogin.setPhoneno(upd.getPhoneno());
        exisLogin.setEmail(upd.getEmail());
        exisLogin.setUsername(upd.getUsername());
        // if (upd.getPassword() != null && !upd.getPassword().isEmpty()) {
        // exisLogin.setPassword(upd.getPassword());
        // }
        if (upd.getPassword() != null && !upd.getPassword().isEmpty()) {
            exisLogin.setPassword(passwordEncoder.encode(upd.getPassword())); // ✅ encode before saving
        }
        if (upd.getRole() != null && !upd.getRole().isEmpty()) {
            exisLogin.setRole(upd.getRole());
        }

        return loginDetailsRepo.save(exisLogin);
    }

    // public RealNest deleteLogin(long id){
    // if(!loginDetailsRepo.existsById(id)){
    // throw new EntityNotFoundException("User not found with id "+id);
    // }
    // loginDetailsRepo.deleteById(id);
    // return null;
    // }

    @Transactional
    public void deleteLogin(long id) {
        if (!loginDetailsRepo.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }
        loginDetailsRepo.deleteById(id);
    }

    // @Override
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException{
    // System.out.println(username);
    // Login appLogin = loginDetailsRepo.findByUsername(username).orElseThrow(() ->
    // new UsernameNotFoundException("User not found"));
    // System.out.println("Loaded login: "+appLogin.getUsername());
    // return
    // org.springframework.security.core.userdetails.User.builder().username(appLogin.getUsername()).password(appLogin.getPassword()).roles(appLogin.getRole()!=null?
    // appLogin.getRole():"USER").build();
    // }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Login appLogin = loginDetailsRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(appLogin.getUsername())
                .password(appLogin.getPassword())
                .roles(appLogin.getRole() != null ? appLogin.getRole() : "USER")
                .build();
    }

}
