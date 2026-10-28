package com.disasterdecoded.nivaranflow;

import com.disasterdecoded.nivaranflow.controller.*;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // API endpoints
        CaseController caseController = new CaseController();
        OverlapController overlapController = new OverlapController();
        InstitutionController institutionController = new InstitutionController();
        StatsController statsController = new StatsController();

        server.createContext("/api/cases", caseController);
        server.createContext("/api/overlaps", overlapController);
        server.createContext("/api/institutions", institutionController);
        server.createContext("/api/stats", statsController);
        server.createContext("/api/audit", statsController);

        // Static files frontend
        String frontendDir = "frontend/public";
        File dir = new File(frontendDir);
        if (!dir.exists()) {
            // Check relative to project root
            frontendDir = "./frontend/public";
        }
        server.createContext("/", new StaticFileHandler(frontendDir));

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor()); // Java 25 virtual threads!
        server.start();

        System.out.println("===============================================================");
        System.out.println("  SMART INDIA HACKATHON 2026 - PROBLEM STATEMENT SIH26264     ");
        System.out.println("  TEAM: DISASTER DECODED (Team ID: 198035)                     ");
        System.out.println("  SOLUTION: NIVARANFLOW - Integrated AICTE Complaint Dashboard ");
        System.out.println("===============================================================");
        System.out.println("  🚀 Java 25 Backend Server running on: http://localhost:" + port);
        System.out.println("  🌐 Web Dashboard available at:       http://localhost:" + port + "/");
        System.out.println("  📡 REST API Base:                     http://localhost:" + port + "/api");
        System.out.println("  Press Ctrl+C to stop.");
        System.out.println("===============================================================");
    }
}
