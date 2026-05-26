package com.parul.hostel.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Publicly accessible static pages and scripts
                .requestMatchers("/", "/index.html", "/css/**", "/js/script.js", "/assets/**").permitAll()
                // Auth endpoints
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                // Admin pages & endpoints
                .requestMatchers("/admin.html", "/pending_bookings", "/approve_booking/**", "/cancel_booking/**").hasRole("ADMIN")
                // Protected pages & APIs
                .requestMatchers("/hostels.html", "/booking.html", "/payment.html", "/success.html").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers("/api/**").hasAnyRole("STUDENT", "ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                // Redirect unauthenticated requests back to the main login page
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/index.html"))
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/index.html")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
