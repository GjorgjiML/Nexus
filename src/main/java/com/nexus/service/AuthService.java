package com.nexus.service;

import com.nexus.dto.LoginRequest;
import com.nexus.dto.RegisterRequest;
import com.nexus.entity.User;
import com.nexus.exception.BadRequestException;
import com.nexus.repository.UserRepository;
import com.nexus.security.JwtService;
import com.nexus.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public User register(RegisterRequest request) {
        String username = request.getUsername().trim().toLowerCase();
        String email = request.getEmail().trim().toLowerCase();

        if (!username.matches("^[a-z0-9_]{3,50}$")) {
            throw new BadRequestException("Username may only contain letters, numbers, and underscores");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .bio("")
                .build();
        return userRepository.save(user);
    }

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername().trim().toLowerCase(),
                        request.getPassword()
                )
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return jwtService.generateToken(principal.getId(), principal.getUsername());
    }
}
