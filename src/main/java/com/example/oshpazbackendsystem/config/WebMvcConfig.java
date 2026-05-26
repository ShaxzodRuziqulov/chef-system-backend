package com.example.oshpazbackendsystem.config;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    @Value("${app.upload.video-dir:uploads/videos}")
    private String videoUploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // file: prefiks bilan absolute path — Windows va Unix da bir xil ishlaydi
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString()
                .replace("\\", "/");
        String location = "file:" + absolutePath + "/";

        log.info("Static upload resource location: {}", location);

        // /uploads/** → uploads/images/ papkasi
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCacheControl(
                    org.springframework.http.CacheControl.maxAge(7, java.util.concurrent.TimeUnit.DAYS)
                );

        // /uploads/videos/** → uploads/videos/ papkasi
        String videoAbsPath = Paths.get(videoUploadDir).toAbsolutePath().normalize().toString()
                .replace("\\", "/");
        String videoLocation = "file:" + videoAbsPath + "/";
        log.info("Static video resource location: {}", videoLocation);
        registry.addResourceHandler("/uploads/videos/**")
                .addResourceLocations(videoLocation)
                .setCacheControl(
                    org.springframework.http.CacheControl.maxAge(7, java.util.concurrent.TimeUnit.DAYS)
                );
    }
}
