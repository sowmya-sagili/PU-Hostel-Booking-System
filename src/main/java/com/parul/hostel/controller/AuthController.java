package com.parul.hostel.controller;

import com.parul.hostel.dto.AuthDto.*;
import com.parul.hostel.entity.Student;
import com.parul.hostel.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Processing registration request for email: {}", request.getEmail());

        if (studentRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration rejected: Email {} is already registered.", request.getEmail());
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .success(false)
                    .error("User already exists. Please login.")
                    .build());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.info("Password encoded successfully for registration of email: {}", request.getEmail());

        Student student = Student.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phone(request.getPhone())
                .role("STUDENT")
                .build();

        studentRepository.save(student);
        log.info("Student saved to DB successfully: {}", student.getEmail());
        log.info("Student registered successfully: {}", request.getEmail());

        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .email(student.getEmail())
                .name(student.getName())
                .role(student.getRole())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            Student student = studentRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User details not found post-authentication"));

            log.info("Authentication success for email: {}", student.getEmail());
            log.info("User {} authenticated successfully with role {}", student.getEmail(), student.getRole());

            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .role(student.getRole())
                    .name(student.getName())
                    .email(student.getEmail())
                    .build());

        } catch (Exception e) {
            log.warn("Authentication failed for email: {}. Reason: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .success(false)
                    .error("Invalid email or password!")
                    .build());
        }
    }
}
