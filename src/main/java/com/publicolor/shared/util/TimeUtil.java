package com.publicolor.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Punto único para obtener la fecha/hora actual — facilita fijarla en pruebas. */
public final class TimeUtil {

    private TimeUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDate today() {
        return LocalDate.now();
    }
}
