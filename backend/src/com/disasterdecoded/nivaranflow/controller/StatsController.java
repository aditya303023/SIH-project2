package com.disasterdecoded.nivaranflow.controller;

import com.disasterdecoded.nivaranflow.model.ComplaintCase;
import com.disasterdecoded.nivaranflow.model.OverlapAlert;
import com.disasterdecoded.nivaranflow.repository.DataStore;
import com.disasterdecoded.nivaranflow.service.AuditService;
import com.disasterdecoded.nivaranflow.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StatsController implements HttpHandler {
    private final DataStore dataStore = DataStore.getInstance();
    private final AuditService auditService = new AuditService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpUtil.sendOptionsResponse(exchange);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(method) && path.equals("/api/stats")) {
                Collection<ComplaintCase> cases = dataStore.getCases().values();
                Collection<OverlapAlert> alerts = dataStore.getOverlapAlerts().values();

                long totalCases = cases.size();
                long activeCases = cases.stream().filter(c -> !"Closed".equalsIgnoreCase(c.getCurrentStatus()) && !"Resolved".equalsIgnoreCase(c.getCurrentStatus())).count();

                long pendingOverlaps = alerts.stream().filter(a -> "PENDING_REVIEW".equalsIgnoreCase(a.getStatus())).count();
                long linkedOverlaps = alerts.stream().filter(a -> "LINKED".equalsIgnoreCase(a.getStatus())).count();
                long separateOverlaps = alerts.stream().filter(a -> "KEPT_SEPARATE".equalsIgnoreCase(a.getStatus())).count();

                Map<String, Long> bureauBreakdown = cases.stream()
                        .collect(Collectors.groupingBy(ComplaintCase::getBureau, Collectors.counting()));

                Map<String, Long> priorityBreakdown = cases.stream()
                        .collect(Collectors.groupingBy(ComplaintCase::getPriority, Collectors.counting()));

                Map<String, Object> stats = new LinkedHashMap<>();
                stats.put("totalCases", totalCases);
                stats.put("activeCases", activeCases);
                stats.put("totalInstitutions", dataStore.getInstitutions().size());
                stats.put("totalOverlapsDetected", alerts.size());
                stats.put("pendingOverlaps", pendingOverlaps);
                stats.put("linkedOverlaps", linkedOverlaps);
                stats.put("separateOverlaps", separateOverlaps);
                stats.put("bureauBreakdown", bureauBreakdown);
                stats.put("priorityBreakdown", priorityBreakdown);

                HttpUtil.sendJsonResponse(exchange, 200, stats);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && path.equals("/api/audit")) {
                HttpUtil.sendJsonResponse(exchange, 200, auditService.getAllLogs());
                return;
            }

            HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
        }
    }
}
