package com.robobg.service.impl;

import com.robobg.helpers.PgDump;
import com.robobg.helpers.TarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String jdbcUrl; // e.g. jdbc:postgresql://localhost:5432/mydb
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password:}")
    private String dbPassword;

    public ScheduledBackup(S3Service s3) { this.s3 = s3; }

    // Run once per day at 02:30 (Windows or Linux). Adjust as needed.
    @Scheduled(fixedRate = 5000)
    public void archiveAndUpload() {
        String ts = LocalDateTime.now().format(TS);
        String filename = ts + ".tar.gz";

        Path imagesDir = Path.of(filesDir);
        Path dumpDir = Path.of(databaseBackupPath);
        Path dumpFile = dumpDir.resolve("db-" + ts + ".sql"); // plain SQL; outer .gz will compress it
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
        } catch (Exception e) {
            log.error("Backup failed", e);
        } finally {
            try { java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignore) {}
        }
    }





}
