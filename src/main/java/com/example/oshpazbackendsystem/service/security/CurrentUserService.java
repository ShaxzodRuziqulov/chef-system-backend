package com.example.oshpazbackendsystem.service.security;

import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Anonymous yoki autentifikatsiya qilinmagan so'rov → 401
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BadCredentialsException("Tizimga kirish talab etiladi");
        }

        String username = authentication.getName();
        return userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Foydalanuvchi topilmadi: " + username));
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public UUID getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return userRepository.findWithRolesByUsername(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}
