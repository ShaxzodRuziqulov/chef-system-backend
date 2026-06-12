package com.example.oshpazbackendsystem.config;

import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreInject {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @PostConstruct
//    @Transactional
    public void setDefaultUsers() {

        if (!userRepository.existsByUsername("admin")) {
            User user = User.builder()
                    .username("admin")
                    .password(encodePassword("1234"))
                    .role(Role.ADMIN)
                    .email("admin@email.com")
                    .fullName("Admin")
                    .active(true)
                    .build();
            userRepository.save(user);
        } else {
            // Eski buggy kod bilan yaratilgan admin active=false bo'lishi mumkin — tuzatamiz
            userRepository.findByUsername("admin").ifPresent(admin -> {
                if (!admin.isActive()) {
                    admin.setActive(true);
                    userRepository.save(admin);
                }
            });
        }
    }
}
