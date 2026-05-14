package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.LoginRequest;
import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.response.*;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.service.AuthService;
import com.example.oshpazbackendsystem.service.JwtService;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return ResponseEntity.ok(authService.buildAuthTokenResponse(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@RequestBody RefreshTokenRequestDto dto) {
        User authUser = authService.refresh(dto);
        TokenPairResponse response = new TokenPairResponse();
        response.setAccessToken(jwtService.generateToken(authUser));
        response.setRefreshToken(jwtService.generateRefreshToken(authUser));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication required");
        }
        User currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(authService.toAuthUserResponse(currentUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response) {
        authService.validateRefreshToken(dto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
