package com.publicolor.shared.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/** Carga el logo de Publicolor una sola vez para usarlo en los PDFs (reportes y recibos). */
public final class LogoUtil {

    private static volatile byte[] logoBytes;

    private LogoUtil() {
    }

    public static byte[] cargarLogo() {
        byte[] cargado = logoBytes;
        if (cargado != null) {
            return cargado;
        }
        try (InputStream in = new ClassPathResource("static/images/logo-publicolor.png").getInputStream()) {
            cargado = in.readAllBytes();
            logoBytes = cargado;
            return cargado;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el logo de Publicolor.", e);
        }
    }
}
