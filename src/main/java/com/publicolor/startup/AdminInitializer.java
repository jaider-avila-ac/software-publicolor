package com.publicolor.startup;

import com.publicolor.user.model.Usuario;
import com.publicolor.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Si la tabla users está vacía (primer arranque), crea un administrador por
 * defecto con la contraseña configurada. Cámbiala luego si lo necesitás —
 * no hay pantalla de "olvidé mi contraseña" en v1.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-email}")
    private String defaultEmail;

    @Value("${admin.default-password}")
    private String defaultPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario admin = Usuario.builder()
                .name("Administrador Publicolor")
                .email(defaultEmail)
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .build();
        usuarioRepository.save(admin);

        log.warn("==========================================================");
        log.warn(" Usuario administrador creado (primer arranque):");
        log.warn(" correo:      {}", defaultEmail);
        log.warn(" Cambiala en el codigo/variables de entorno si es necesario.");
        log.warn("==========================================================");
    }
}
