package com.robobg.service.impl;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    public static String getExtensionOfFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String determineContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> "image/png";
            case "jpg" -> "image/jpg";
            case "jpeg" -> "image/jpeg";
            case "avif" -> "image/avif";
            case "webp" -> "image/webp";
            default -> ""; // Unsupported type
        };
    }
}
