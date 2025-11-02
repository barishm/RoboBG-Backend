package com.robobg.service.impl;

import com.robobg.helpers.PgDump;
import com.robobg.helpers.TarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ScheduledBackup {
    private static final Logger log = LoggerFactory.getLogger(ScheduledBackup.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final S3Service s3;

    @Value("${static.files.path}")
    private String filesDir;

    @Value("${database.backup.path}")
    private String databaseBackupPath;

    @Value("${backup.bucket:robobg-backups}")
    private String bucket;

    @Value("${backup.prefix:backups/}")
    private String prefix;

    // --- DB connection (usually from Spring properties) ---
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password:}")
    private String dbPassword;

    public ScheduledBackup(S3Service s3) { this.s3 = s3; }

    @Scheduled(fixedRate = 60000)
    public void archiveAndUpload() {
        String ts = LocalDateTime.now().format(TS);
        String filename = ts + ".tar.gz";

        Path imagesDir = Path.of(filesDir);
        Path dumpDir = Path.of(databaseBackupPath);
        Path dumpFile = dumpDir.resolve("db-" + ts + ".dump"); // plain SQL; outer .gz will compress it
        Path tmp = Path.of(System.getProperty("java.io.tmpdir")).resolve(filename);
        String key = prefix + filename;

        try {
            // 1) Ensure DB dump exists
            Files.createDirectories(dumpDir);
            PgDump.dump(jdbcUrl, dbUser, dbPassword, dumpFile);

            // 2) Create a single tar.gz with images + dump
            Path archive = TarUtils.tarGzipImagesPlusFile(imagesDir, dumpFile, tmp);
            log.info("Archive ready: {}", archive);

            // 3) Upload
            s3.putFile(bucket, key, archive);
            log.info("Uploaded to s3://{}/{}", bucket, key);

            // 4) Keep only the 3 newest backups
            int removed = s3.deleteAllButNewest(bucket, prefix, 3);
            if (removed > 0) {
                log.info("Pruned {} old backups under s3://{}/{}", removed, bucket, prefix);
            }

            int removedLocal = deleteAllButNewest(dumpDir, p -> p.getFileName().toString().endsWith(".sql"), 3);
            if (removedLocal > 0) {
                log.info("Pruned {} old local dumps under {}", removedLocal, dumpDir);
            }
        } catch (Exception e) {
            log.error("Backup failed", e);
        } finally {
            try { java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignore) {}
        }
    }

    private static int deleteAllButNewest(Path dir, java.util.function.Predicate<Path> filter, int keep) throws IOException {
        if (!Files.isDirectory(dir)) return 0;

        List<Path> files;
        try (Stream<Path> s = Files.list(dir)) {
            files = s.filter(Files::isRegularFile)
                    .filter(filter)
                    .sorted(Comparator
                            .comparing((Path p) -> {
                                try { return Files.getLastModifiedTime(p); } catch (IOException e) { return FileTime.fromMillis(0); }
                            })
                            .reversed()
                            .thenComparing((Path p) -> p.getFileName().toString(), Comparator.reverseOrder()))
                    .toList();
        }

        int removed = 0;
        for (int i = keep; i < files.size(); i++) {
            try { Files.deleteIfExists(files.get(i)); removed++; } catch (Exception ignore) {}
        }
        return removed;
    }

}
