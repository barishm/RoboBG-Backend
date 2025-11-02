package com.robobg.helpers;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.nio.file.*;
import java.util.Set;

public class TarUtils {
    private static final Set<String> IMG_EXT = Set.of("jpg","jpeg","png","gif","webp","bmp","tiff");

    /** Create a .tar.gz containing all images under imagesDir AND the given extra file (e.g., DB dump). */
    public static Path tarGzipImagesPlusFile(Path imagesDir, Path extraFile, Path outFile) throws IOException {
        if (!Files.isDirectory(imagesDir)) {
            throw new IllegalArgumentException("Not a directory: " + imagesDir);
        }
        if (!Files.isRegularFile(extraFile)) {
            throw new IllegalArgumentException("Not a file: " + extraFile);
        }

        Files.createDirectories(outFile.getParent());

        try (OutputStream fos = Files.newOutputStream(outFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos);
             TarArchiveOutputStream tar = new TarArchiveOutputStream(gzos)) {

            tar.setLongFileMode(org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU);

            int count = 0;

            // 1) Add images under imagesDir
            try (var walk = Files.walk(imagesDir)) {
                for (Path p : (Iterable<Path>) walk.filter(Files::isRegularFile).filter(TarUtils::isImage)::iterator) {
                    String rel = imagesDir.relativize(p).toString().replace('\\','/');
                    addEntry(tar, p, "images/" + rel); // put under a folder in the tar
                    count++;
                }
            }

            // 2) Add the DB dump at the root (or in "db/")
            addEntry(tar, extraFile, "db/" + extraFile.getFileName().toString());
            count++;

            if (count == 0) {
                try { Files.deleteIfExists(outFile); } catch (IOException ignored) {}
                throw new IOException("Nothing to archive.");
            }
        }

        return outFile;
    }

    private static void addEntry(TarArchiveOutputStream tar, Path path, String tarPath) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), tarPath);
        tar.putArchiveEntry(entry);
        Files.copy(path, tar);
        tar.closeArchiveEntry();
    }

    private static boolean isImage(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot < 0) return false;
        String ext = name.substring(dot + 1);
        return IMG_EXT.contains(ext);
    }
}

