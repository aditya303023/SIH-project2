package com.disasterdecoded.nivaranflow.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {
    private final String baseDir;

    public StaticFileHandler(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/") || path.isBlank()) {
            path = "/index.html";
        }

        Path filePath = Paths.get(baseDir, path).normalize();
        File file = filePath.toFile();

        // Security check: ensure path does not escape baseDir
        if (!file.getCanonicalPath().startsWith(new File(baseDir).getCanonicalPath())) {
            String resp = "403 Forbidden";
            exchange.sendResponseHeaders(403, resp.length());
            try (OutputStream os = exchange.getResponseBody()) { os.write(resp.getBytes()); }
            return;
        }

        if (!file.exists() || file.isDirectory()) {
            // Fallback to index.html for SPA if not found
            File fallback = new File(baseDir, "index.html");
            if (fallback.exists()) {
                file = fallback;
            } else {
                String resp = "404 Not Found";
                exchange.sendResponseHeaders(404, resp.length());
                try (OutputStream os = exchange.getResponseBody()) { os.write(resp.getBytes()); }
                return;
            }
        }

        String mimeType = getMimeType(file.getName());
        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");

        exchange.sendResponseHeaders(200, file.length());
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
    }

    private String getMimeType(String filename) {
        if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filename.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filename.endsWith(".svg")) return "image/svg+xml";
        if (filename.endsWith(".json")) return "application/json";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
