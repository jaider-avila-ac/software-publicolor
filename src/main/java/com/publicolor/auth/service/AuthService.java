package com.publicolor.auth.service;

import com.publicolor.auth.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(String email, String password);
}
