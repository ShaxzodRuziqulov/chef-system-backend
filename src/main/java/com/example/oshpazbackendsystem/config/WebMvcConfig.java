package com.example.oshpazbackendsystem.config;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Yuklangan rasmlarni statik fayl sifatida serve qilish.
 * /uploads/images/** → uploads/images/ papkasidagi fayllar
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
        // OS ga bog'liq bo'lgan path separator ni to'g'rilash
        if (!absolutePath.endsWith("/") && !absolutePath.endsWith("\\")) {
            absolutePath += "/";
        }

        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
