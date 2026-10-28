package com.disasterdecoded.nivaranflow.controller;

import com.disasterdecoded.nivaranflow.service.InstitutionService;
import com.disasterdecoded.nivaranflow.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class InstitutionController implements HttpHandler {
    private final InstitutionService institutionService = new InstitutionService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpUtil.sendOptionsResponse(exchange);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(method) && path.equals("/api/institutions")) {
                HttpUtil.sendJsonResponse(exchange, 200, institutionService.getAllInstitutions());
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/institutions/")) {
                String id = path.substring("/api/institutions/".length());
                Map<String, Object> profile = institutionService.getInstitution360Profile(id);
                if (profile == null) {
                    HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Institution not found: " + id));
                } else {
                    HttpUtil.sendJsonResponse(exchange, 200, profile);
                }
                return;
            }

            HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
        }
    }
}
