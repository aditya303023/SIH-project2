package com.disasterdecoded.nivaranflow.controller;

import com.disasterdecoded.nivaranflow.util.HttpUtil;
import com.disasterdecoded.nivaranflow.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.*;

public class AuthController implements HttpHandler {

    public static class UserProfile {
        public String id;
        public String name;
        public String email;
        public String role;
        public String designation;
        public String bureau;
        public String clearanceLevel;
        public String institutionId;
        public String avatar;
        public String token;

        public UserProfile(String id, String name, String email, String role, String designation, String bureau, String clearanceLevel, String institutionId, String avatar) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.designation = designation;
            this.bureau = bureau;
            this.clearanceLevel = clearanceLevel;
            this.institutionId = institutionId;
            this.avatar = avatar;
            this.token = "NF-JWT-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }

    private static final Map<String, UserProfile> USERS = new HashMap<>();

    static {
        USERS.put("admin@aicte-india.org", new UserProfile(
                "USR-01", "Dr. Alok Kumar", "admin@aicte-india.org", "AICTE_HQ_EXECUTIVE",
                "Advisor-I & Chief Grievance Appellate", "AICTE Headquarters", "Level 1 (All Bureaus)", null, "👨‍💼"
        ));
        USERS.put("pgrc@aicte-india.org", new UserProfile(
                "USR-02", "Dr. Sunita Rao", "pgrc@aicte-india.org", "PGRC_OFFICER",
                "Grievance Redressal Officer", "PGRC", "Level 2 (Grievance Cell)", null, "👩‍⚖️"
        ));
        USERS.put("regulation@aicte-india.org", new UserProfile(
                "USR-03", "Er. Amit Verma", "regulation@aicte-india.org", "REGULATION_OFFICER",
                "Deputy Director (Norms & Compliance)", "Regulation Bureau", "Level 2 (Regulation)", null, "👨‍💻"
        ));
        USERS.put("approval@aicte-india.org", new UserProfile(
                "USR-04", "Smt. K. Meenakshi", "approval@aicte-india.org", "APPROVAL_OFFICER",
                "Assistant Director (Extension of Approval)", "Approval Bureau", "Level 2 (Approval)", null, "👩‍💼"
        ));
        USERS.put("principal@apextech.edu.in", new UserProfile(
                "USR-05", "Dr. S. K. Kulkarni", "principal@apextech.edu.in", "INSTITUTION_NODAL",
                "Principal & AICTE Nodal Liaison", "Institution A (Apex Tech)", "Level 3 (Institution)", "1-1092837461", "🏛️"
        ));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpUtil.sendOptionsResponse(exchange);
            return;
        }

        try {
            // POST /api/auth/login
            if ("POST".equalsIgnoreCase(method) && path.equals("/api/auth/login")) {
                String body = HttpUtil.readBody(exchange);
                Map<String, Object> map = JsonUtil.parseObject(body);
                String email = (String) map.get("email");
                String password = (String) map.get("password");

                if (email == null || email.isBlank()) {
                    HttpUtil.sendJsonResponse(exchange, 400, Map.of("error", "Email is required."));
                    return;
                }

                UserProfile user = USERS.get(email.trim().toLowerCase());
                if (user == null) {
                    // Create guest profile if email unrecognized
                    user = new UserProfile(
                            "USR-GUEST",
                            email.split("@")[0].toUpperCase() + " (Official)",
                            email,
                            "AICTE_STAFF",
                            "Authorized Evaluation Official",
                            "AICTE General",
                            "Level 2",
                            null,
                            "👤"
                    );
                }

                HttpUtil.sendJsonResponse(exchange, 200, Map.of(
                        "success", true,
                        "token", user.token,
                        "user", user
                ));
                return;
            }

            // GET /api/auth/roles (demo accounts for easy 1-click evaluation)
            if ("GET".equalsIgnoreCase(method) && path.equals("/api/auth/roles")) {
                HttpUtil.sendJsonResponse(exchange, 200, USERS.values());
                return;
            }

            HttpUtil.sendJsonResponse(exchange, 404, Map.of("error", "Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
        }
    }
}
