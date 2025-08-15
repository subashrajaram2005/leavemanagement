package com.example.leavemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        var admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN") // Spring Security adds "ROLE_" prefix automatically
                .build();

        var s1 = User.withUsername("student1").password(encoder.encode("student123")).roles("STUDENT").build();
        var s2 = User.withUsername("student2").password(encoder.encode("student123")).roles("STUDENT").build();
        var s3 = User.withUsername("student3").password(encoder.encode("student123")).roles("STUDENT").build();
        var s4 = User.withUsername("student4").password(encoder.encode("student123")).roles("STUDENT").build();
        var s5 = User.withUsername("student5").password(encoder.encode("student123")).roles("STUDENT").build();

        return new InMemoryUserDetailsManager(admin, s1, s2, s3, s4, s5);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    var cfg = new org.springframework.web.cors.CorsConfiguration();
                    cfg.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
                    cfg.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(java.util.List.of("*"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Allow CORS preflight requests without authentication
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Allow anonymous access to authentication endpoint
                        .requestMatchers("/api/auth/**").permitAll()

                        // Only STUDENT role can submit leave requests
                        .requestMatchers(HttpMethod.POST, "/api/leave/request").hasRole("STUDENT")

                        // ADMIN role can get, update, and delete leave requests
                        .requestMatchers(HttpMethod.GET, "/api/leave/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/leave/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/leave/**").hasRole("ADMIN")

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
