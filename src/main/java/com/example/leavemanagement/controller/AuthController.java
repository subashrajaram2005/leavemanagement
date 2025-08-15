package com.example.leavemanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @GetMapping("/role")
    public Object getRole(Authentication authentication) {
        if (authentication == null) {
            return java.util.Map.of("authenticated", false);
        }
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return java.util.Map.of(
                "authenticated", true,
                "username", authentication.getName(),
                "roles", roles
        );
    }
}
