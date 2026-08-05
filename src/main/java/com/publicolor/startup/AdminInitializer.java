package com.publicolor.startup;

import com.publicolor.user.model.Usuario;
import com.publicolor.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Si la tabla users está vacía (primer arranque), crea un administrador por
 * defecto y muestra la contraseña generada UNA sola vez en el log. Cámbiala
 * apenas inicies sesión — no hay pantalla de "olvidé mi contraseña" en v1.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-email}")
    private String defaultEmail;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        String rawPassword = randomPassword(12);
        Usuario admin = Usuario.builder()
                .name("Administrador Publicolor")
                .email(defaultEmail)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        usuarioRepository.save(admin);

        log.warn("==========================================================");
        log.warn(" Usuario administrador creado (primer arranque):");
        log.warn(" correo:      {}", defaultEmail);
        log.warn(" contraseña:  {}", rawPassword);
        log.warn(" Cambiala apenas inicies sesión — este mensaje no se repetirá.");
        log.warn("==========================================================");
    }

    private String randomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
