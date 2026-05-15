package com.example.oshpazbackendsystem.controller;

import com.example.oshpazbackendsystem.dto.LoginRequest;
import com.example.oshpazbackendsystem.dto.RegisterRequest;
import com.example.oshpazbackendsystem.dto.UpdateProfileRequest;
import com.example.oshpazbackendsystem.dto.response.*;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.exception.ApiResponse;
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
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(authService.buildAuthTokenResponse(user)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPairResponse>> refresh(@RequestBody RefreshTokenRequestDto dto) {
        User authUser = authService.refresh(dto);
        TokenPairResponse response = new TokenPairResponse();
        response.setAccessToken(jwtService.generateToken(authUser));
        response.setRefreshToken(jwtService.generateRefreshToken(authUser));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> authenticatedUser() {
        User currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok(authService.toAuthUserResponse(currentUser)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<AuthUserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        AuthUserResponse updated = authService.updateProfile(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
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
