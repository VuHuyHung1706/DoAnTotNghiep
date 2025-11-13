package com.web.movieservice.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file);
    void deleteFile(String filePath);
}
