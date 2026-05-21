package com.example.oshpazbackendsystem.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Google Cloud Storage ga rasm yuklash servisi.
 * Cloud Run da Application Default Credentials (ADC) orqali avtomatik autentifikatsiya.
 */
@Service
@Slf4j
public class GcsStorageService {

    @Value("${app.gcs.bucket-name:oshpaz-uploads}")
    private String bucketName;

    @Value("${app.gcs.enabled:false}")
    private boolean gcsEnabled;

    @Value("${app.upload.dir:uploads/images}")
    private String localUploadDir;

    private final Storage storage;

    public GcsStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    /**
     * Faylni yuklaydi.
     * - prod (GCS enabled): GCS ga yuklaydi → public URL qaytaradi
     * - local (GCS disabled): lokal papkaga saqlaydi → /uploads/filename qaytaradi
     */
    public String upload(MultipartFile file) throws IOException {
        String ext      = getExtension(file);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        if (gcsEnabled) {
            return uploadToGcs(file, fileName);
        } else {
            return uploadToLocal(file, fileName);
        }
    }

    private String uploadToGcs(MultipartFile file, String fileName) throws IOException {
        BlobId   blobId   = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        String url = "https://storage.googleapis.com/" + bucketName + "/" + fileName;
        log.info("GCS ga yuklandi: {}", url);
        return url;
    }

    private String uploadToLocal(MultipartFile file, String fileName) throws IOException {
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(localUploadDir).toAbsolutePath();
        java.nio.file.Files.createDirectories(uploadPath);
        java.nio.file.Files.copy(
                file.getInputStream(),
                uploadPath.resolve(fileName),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
        log.info("Lokal saqlandi: {}/{}", uploadPath, fileName);
        return "/uploads/" + fileName;
    }

    private String getExtension(MultipartFile file) {
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "img";
        return original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
    }
}
