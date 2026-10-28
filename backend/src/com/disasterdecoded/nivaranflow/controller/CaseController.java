package com.disasterdecoded.nivaranflow.controller;

import com.disasterdecoded.nivaranflow.model.ComplaintCase;
import com.disasterdecoded.nivaranflow.service.CaseService;
import com.disasterdecoded.nivaranflow.util.HttpUtil;
import com.disasterdecoded.nivaranflow.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CaseController implements HttpHandler {
    private final CaseService caseService = new CaseService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpUtil.sendOptionsResponse(exchange);
            return;
        }

        try {
            // GET /api/cases
            if ("GET".equalsIgnoreCase(method) && path.equals("/api/cases")) {
                Map<String, String> params = HttpUtil.parseQueryParams(exchange.getRequestURI().getQuery());
                String bureau = params.get("bureau");
                String status = params.get("status");
                String priority = params.get("priority");
                String query = params.get("query");

                List<ComplaintCase> list = caseService.getCases(bureau, status, priority, query);
                HttpUtil.sendJsonResponse(exchange, 200, list);
                return;
            }

            // GET /api/cases/{id}
            if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/cases/")) {
                String id = path.substring("/api/cases/".length());
                ComplaintCase c = caseService.getCaseById(id);
                if (c == null) {
                    HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Case not found with ID: " + id));
                } else {
                    HttpUtil.sendJsonResponse(exchange, 200, c);
                }
                return;
            }

            // POST /api/cases
            if ("POST".equalsIgnoreCase(method) && path.equals("/api/cases")) {
                String body = HttpUtil.readBody(exchange);
                Map<String, Object> map = JsonUtil.parseObject(body);

                ComplaintCase newCase = new ComplaintCase();
                newCase.setId((String) map.get("id"));
                newCase.setAictePermanentId((String) map.get("aictePermanentId"));
                newCase.setInstitutionName((String) map.get("institutionName"));
                newCase.setComplaintType((String) map.get("complaintType"));
                newCase.setBureau((String) map.get("bureau"));
                newCase.setCurrentStatus((String) map.get("currentStatus"));
                newCase.setCaseOwner((String) map.get("caseOwner"));
                newCase.setNextStep((String) map.get("nextStep"));
                newCase.setDeadlineDate((String) map.get("deadlineDate"));
                newCase.setPriority((String) map.get("priority"));
                newCase.setComplainantType((String) map.get("complainantType"));
                newCase.setDescription((String) map.get("description"));

                ComplaintCase created = caseService.createCase(newCase);
                HttpUtil.sendJsonResponse(exchange, 201, created);
                return;
            }

            // POST /api/cases/{id}/actions
            if ("POST".equalsIgnoreCase(method) && path.matches("/api/cases/[^/]+/actions")) {
                String id = path.split("/")[3];
                String body = HttpUtil.readBody(exchange);
                Map<String, Object> map = JsonUtil.parseObject(body);

                String actionTaken = (String) map.get("actionTaken");
                String performedBy = (String) map.get("performedBy");
                String bureau = (String) map.get("bureau");
                String remarks = (String) map.get("remarks");

                var item = caseService.addAction(id, actionTaken, performedBy, bureau, remarks);
                HttpUtil.sendJsonResponse(exchange, 201, item);
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
