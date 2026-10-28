package com.disasterdecoded.nivaranflow.controller;

import com.disasterdecoded.nivaranflow.model.ComplaintCase;
import com.disasterdecoded.nivaranflow.model.OverlapAlert;
import com.disasterdecoded.nivaranflow.model.ReviewDecision;
import com.disasterdecoded.nivaranflow.repository.DataStore;
import com.disasterdecoded.nivaranflow.service.OverlapDetectionService;
import com.disasterdecoded.nivaranflow.util.HttpUtil;
import com.disasterdecoded.nivaranflow.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.*;

public class OverlapController implements HttpHandler {
    private final OverlapDetectionService overlapService = new OverlapDetectionService();
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpUtil.sendOptionsResponse(exchange);
            return;
        }

        try {
            // GET /api/overlaps
            if ("GET".equalsIgnoreCase(method) && path.equals("/api/overlaps")) {
                List<OverlapAlert> alerts = overlapService.getAllAlerts();
                HttpUtil.sendJsonResponse(exchange, 200, alerts);
                return;
            }

            // GET /api/overlaps/{id} (includes side-by-side case details for review)
            if ("GET".equalsIgnoreCase(method) && path.matches("/api/overlaps/[^/]+")) {
                String id = path.substring("/api/overlaps/".length());
                OverlapAlert alert = overlapService.getAlertById(id);
                if (alert == null) {
                    HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Overlap alert not found: " + id));
                    return;
                }

                List<ComplaintCase> casesList = new ArrayList<>();
                for (String cId : alert.getCaseIds()) {
                    ComplaintCase c = dataStore.getCases().get(cId);
                    if (c != null) casesList.add(c);
                }

                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("alert", alert);
                resp.put("cases", casesList);

                HttpUtil.sendJsonResponse(exchange, 200, resp);
                return;
            }

            // POST /api/overlaps/{id}/decision
            if ("POST".equalsIgnoreCase(method) && path.matches("/api/overlaps/[^/]+/decision")) {
                String[] parts = path.split("/");
                String alertId = parts[3];

                String body = HttpUtil.readBody(exchange);
                Map<String, Object> map = JsonUtil.parseObject(body);

                String decisionType = (String) map.get("decisionType");
                String justification = (String) map.get("justification");
                String designatedOwner = (String) map.get("designatedOwner");
                String designatedBureau = (String) map.get("designatedBureau");
                String agreedNextStep = (String) map.get("agreedNextStep");
                String reviewedBy = (String) map.get("reviewedBy");

                ReviewDecision decision = overlapService.recordHumanDecision(
                        alertId, decisionType, justification, designatedOwner, designatedBureau, agreedNextStep, reviewedBy
                );

                HttpUtil.sendJsonResponse(exchange, 200, decision);
                return;
            }

            HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Not Found"));
        } catch (IllegalArgumentException e) {
            HttpUtil.sendJsonResponse(exchange, 400, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendJsonResponse(exchange, 500, Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}
