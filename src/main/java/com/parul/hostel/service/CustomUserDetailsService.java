package com.parul.hostel.service;

import com.parul.hostel.entity.Student;
import com.parul.hostel.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading UserDetails for email: {}", email);
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found in database for email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        String roleAuthority = "ROLE_" + student.getRole().toUpperCase();
        log.info("Loaded UserDetails successfully for email: {} with role: {}", email, roleAuthority);

        return new User(
                student.getEmail(),
                student.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleAuthority))
        );
    }
}
