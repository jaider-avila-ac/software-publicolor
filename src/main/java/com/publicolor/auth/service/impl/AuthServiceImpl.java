package com.publicolor.auth.service.impl;

import com.publicolor.auth.dto.AuthResponse;
import com.publicolor.auth.service.AuthService;
import com.publicolor.shared.exception.CredencialesInvalidasException;
import com.publicolor.shared.security.JwtService;
import com.publicolor.user.model.Usuario;
import com.publicolor.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndActiveTrue(email.trim())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }

        String token = jwtService.generate(usuario.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(usuario.getId())
                .name(usuario.getName())
                .email(usuario.getEmail())
                .role(usuario.getRole().name())
                .build();
    }
}
