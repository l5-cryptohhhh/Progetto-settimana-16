package org.example.progettosettimana16.service;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.dto.AuthResponse;
import org.example.progettosettimana16.dto.LoginRequest;
import org.example.progettosettimana16.dto.RegisterRequest;
import org.example.progettosettimana16.dto.UserResponse;
import org.example.progettosettimana16.entity.User;
import org.example.progettosettimana16.exception.ApiException;
import org.example.progettosettimana16.repository.UserRepository;
import org.example.progettosettimana16.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().strip();
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw ApiException.conflict("Username gia' in uso");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("Email gia' registrata");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user), UserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Stesso messaggio per email inesistente e password errata: non riveliamo quali email sono registrate
        User user = userRepository.findByEmailIgnoreCase(request.email().strip())
                .filter(found -> passwordEncoder.matches(request.password(), found.getPassword()))
                .orElseThrow(() -> ApiException.unauthorized("Email o password non validi"));
        return new AuthResponse(jwtService.generateToken(user), UserResponse.from(user));
    }
}
