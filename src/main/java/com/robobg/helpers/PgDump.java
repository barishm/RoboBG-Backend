package com.robobg.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PgDump {
    private static final Logger log = LoggerFactory.getLogger(PgDump.class);

    /** Runs pg_dump to the given file (plain SQL). Requires pg_dump on PATH. */
    public static void dump(String jdbcUrl, String user, String password, Path outFile) throws IOException, InterruptedException {
        PostgresConn c = PostgresConn.parse(jdbcUrl);

        Files.createDirectories(outFile.getParent());

        // Build command
        List<String> cmd = new ArrayList<>();
        cmd.add("pg_dump");
        cmd.add("-h"); cmd.add(c.host);
        cmd.add("-p"); cmd.add(Integer.toString(c.port));
        cmd.add("-U"); cmd.add(user);
        cmd.add("-d"); cmd.add(c.db);
        cmd.add("-F"); cmd.add("p");    // plain
        cmd.add("-f"); cmd.add(outFile.toAbsolutePath().toString());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        // Prevent pg_dump from prompting for a password
        Map<String, String> env = pb.environment();
        if (password != null) env.put("PGPASSWORD", password);

        // On Windows, prefer redirecting error stream to make logs easier
        pb.redirectErrorStream(true);

        log.info("Running pg_dump to {}", outFile);
        Process p = pb.start();
        try (var r = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
            String line; while ((line = r.readLine()) != null) log.debug("[pg_dump] {}", line);
        }
        int exit = p.waitFor();
        if (exit != 0) {
            try { Files.deleteIfExists(outFile); } catch (Exception ignored) {}
            throw new IOException("pg_dump exited with code " + exit);
        }
        log.info("pg_dump completed");
    }

    /** Minimal JDBC URL parser for postgres: jdbc:postgresql://host:port/db?... */
    static final class PostgresConn {
        final String host; final int port; final String db;
        PostgresConn(String host, int port, String db) { this.host = host; this.port = port; this.db = db; }
        static PostgresConn parse(String jdbc) {
            // Strip prefix
            String s = jdbc.replaceFirst("^jdbc:postgresql://", "");
            // Remove params
            int q = s.indexOf('?'); if (q >= 0) s = s.substring(0, q);
            // host[:port]/db
            int slash = s.indexOf('/');
            if (slash < 0) throw new IllegalArgumentException("Bad JDBC URL (no /db): " + jdbc);
            String hostPort = s.substring(0, slash);
            String db = s.substring(slash + 1);
            String host; int port;
            int colon = hostPort.lastIndexOf(':');
            if (colon >= 0) {
                host = hostPort.substring(0, colon);
                port = Integer.parseInt(hostPort.substring(colon + 1));
            } else {
                host = hostPort;
                port = 5432;
            }
            return new PostgresConn(host, port, db);
        }
    }
}

