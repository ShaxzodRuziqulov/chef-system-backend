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

        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(encodePassword("1234"));
            user.setRole(Role.ADMIN);
            user.setEmail("admin@email.com");
            user.setFullName("Admin");
            userRepository.save(user);
        }
    }
}
