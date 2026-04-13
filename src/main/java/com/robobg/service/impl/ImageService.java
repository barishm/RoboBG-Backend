package com.robobg.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ImageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final String OUTPUT_FORMAT = "jpg";
    private static final float COMPRESSION_QUALITY = 0.75f;

    private final Path storageDir = Paths.get("/files");

    public String storeRobotImage(Long robotId, MultipartFile file) throws IOException {

        validateImage(file);

        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            throw new IllegalArgumentException("File is not a valid image");
        }

        BufferedImage processed = cropAndResizeToSquare(original, 600);

        String fileName = generateFileName("Robot",robotId);
        Path outputPath = storageDir.resolve(fileName);

        Files.createDirectories(outputPath.getParent());

        writeJpeg(processed, outputPath, COMPRESSION_QUALITY);

        return fileName;
    }

    public List<String> storeConsumablesImages(Long consumableId, List<MultipartFile> files) throws IOException {

        List<String> savedFiles = new ArrayList<>();


        for (MultipartFile file : files) {
            validateImage(file);

            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            BufferedImage processed = cropAndResizeToSquare(original, 600);

            String fileName = generateFileName("Consumable",consumableId);

            Path outputPath = storageDir.resolve(fileName);
            Files.createDirectories(outputPath.getParent());

            writeJpeg(processed, outputPath, 0.75f);

            savedFiles.add(fileName);
        }

        return savedFiles;
    }

    // -------------------- VALIDATION --------------------

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new IllegalArgumentException("File too large (max 5MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }
    }

    // -------------------- RESIZING --------------------

    private BufferedImage cropAndResizeToSquare(BufferedImage original, int size) {

        int width = original.getWidth();
        int height = original.getHeight();

        // 1. Crop to square (center crop)
        int squareSize = Math.min(width, height);

        int x = (width - squareSize) / 2;
        int y = (height - squareSize) / 2;

        BufferedImage cropped = original.getSubimage(x, y, squareSize, squareSize);

        // 2. Convert to RGB (important for JPEG)
        BufferedImage rgb = toRGB(cropped);

        // 3. Resize to 600x600
        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g2d.drawImage(rgb, 0, 0, size, size, null);
        g2d.dispose();

        return resized;
    }

    // Convert any format (PNG/WebP) → RGB (needed for JPEG)
    private BufferedImage toRGB(BufferedImage image) {
        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return rgb;
    }

    // -------------------- COMPRESSION --------------------

    private void writeJpeg(BufferedImage image, Path outputPath, float quality) throws IOException {

        ImageWriter writer = ImageIO.getImageWritersByFormatName(OUTPUT_FORMAT).next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        try (OutputStream os = Files.newOutputStream(outputPath);
             var ios = ImageIO.createImageOutputStream(os)) {

            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    // -------------------- FILE NAMING --------------------

    private String generateFileName(String name,Long Id) {

        return "%s_%d_%d.%s"
                .formatted(name, Id, Instant.now().toEpochMilli(), OUTPUT_FORMAT);
    }
}