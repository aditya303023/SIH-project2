package com.disasterdecoded.nivaranflow.service;

import com.disasterdecoded.nivaranflow.model.ActionHistoryItem;
import com.disasterdecoded.nivaranflow.model.ComplaintCase;
import com.disasterdecoded.nivaranflow.model.Institution;
import com.disasterdecoded.nivaranflow.model.OverlapAlert;
import com.disasterdecoded.nivaranflow.repository.DataStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CaseService {
    private final DataStore dataStore = DataStore.getInstance();
    private final OverlapDetectionService overlapService = new OverlapDetectionService();
    private final AuditService auditService = new AuditService();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public List<ComplaintCase> getCases(String bureau, String status, String priority, String query) {
        return dataStore.getCases().values().stream()
                .filter(c -> bureau == null || bureau.isBlank() || "ALL".equalsIgnoreCase(bureau) || c.getBureau().equalsIgnoreCase(bureau))
                .filter(c -> status == null || status.isBlank() || "ALL".equalsIgnoreCase(status) || c.getCurrentStatus().equalsIgnoreCase(status))
                .filter(c -> priority == null || priority.isBlank() || "ALL".equalsIgnoreCase(priority) || c.getPriority().equalsIgnoreCase(priority))
                .filter(c -> {
                    if (query == null || query.isBlank()) return true;
                    String q = query.toLowerCase().trim();
                    return c.getId().toLowerCase().contains(q)
                            || c.getAictePermanentId().toLowerCase().contains(q)
                            || c.getInstitutionName().toLowerCase().contains(q)
                            || c.getComplaintType().toLowerCase().contains(q)
                            || (c.getCaseOwner() != null && c.getCaseOwner().toLowerCase().contains(q))
                            || (c.getDescription() != null && c.getDescription().toLowerCase().contains(q));
                })
                .sorted(Comparator.comparing(ComplaintCase::getId))
                .collect(Collectors.toList());
    }

    public ComplaintCase getCaseById(String id) {
        return dataStore.getCases().get(id);
    }

    public synchronized ComplaintCase createCase(ComplaintCase c) {
        if (c.getAictePermanentId() == null || c.getAictePermanentId().isBlank()) {
            throw new IllegalArgumentException("AICTE Permanent ID is required.");
        }

        // Auto-generate Case ID if missing
        if (c.getId() == null || c.getId().isBlank()) {
            String prefix = switch (c.getBureau() != null ? c.getBureau().toUpperCase() : "GEN") {
                case "PGRC" -> "P-";
                case "REGULATION BUREAU", "REGULATION" -> "R-";
                case "APPROVAL BUREAU", "APPROVAL" -> "A-";
                case "SCSC" -> "S-";
                case "LEGAL CELL", "LEGAL" -> "L-";
                default -> "C-";
            };
            int count = (int) dataStore.getCases().values().stream().filter(caseItem -> caseItem.getId().startsWith(prefix)).count() + 101;
            c.setId(prefix + count);
        }

        String now = LocalDateTime.now().format(FORMATTER);
        if (c.getFiledDate() == null || c.getFiledDate().isBlank()) {
            c.setFiledDate(now.substring(0, 10));
        }
        if (c.getCurrentStatus() == null || c.getCurrentStatus().isBlank()) {
            c.setCurrentStatus("Active");
        }

        // Initial Action History
        ActionHistoryItem initialAction = new ActionHistoryItem(
                "AH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                now,
                "Case Logged in System",
                c.getCaseOwner() != null ? c.getCaseOwner() : "Authorized Official",
                c.getBureau(),
                "Initial registration of complaint under AICTE " + c.getBureau() + " dossier."
        );
        c.getActionHistory().add(initialAction);

        // Ensure Institution is registered
        Institution inst = dataStore.getInstitutions().get(c.getAictePermanentId());
        if (inst == null) {
            inst = new Institution(
                    c.getAictePermanentId(),
                    c.getInstitutionName() != null ? c.getInstitutionName() : "Institution (" + c.getAictePermanentId() + ")",
                    "State Office",
                    "Regional Jurisdiction",
                    "Technical Institution",
                    "Under Scrutiny",
                    "admin@" + c.getAictePermanentId().replace("-", "") + ".gov.in",
                    1
            );
            dataStore.getInstitutions().put(inst.getAictePermanentId(), inst);
        } else {
            inst.setActiveCaseCount(inst.getActiveCaseCount() + 1);
        }

        dataStore.getCases().put(c.getId(), c);

        auditService.log(
                "CASE_CREATED",
                c.getCaseOwner() != null ? c.getCaseOwner() : "AICTE Official",
                c.getBureau(),
                "New case " + c.getId() + " filed for " + c.getInstitutionName() + " (" + c.getAictePermanentId() + ")",
                c.getAictePermanentId(),
                c.getId()
        );

        // Step 3 in Technical Approach: Flag a possible overlap
        OverlapAlert alert = overlapService.checkAndFlagOverlap(c.getAictePermanentId());
        if (alert != null) {
            c.setHasOverlapAlert(true);
            c.setOverlapAlertId(alert.getId());
        }

        return c;
    }

    public synchronized ActionHistoryItem addAction(String caseId, String actionTaken, String performedBy, String bureau, String remarks) {
        ComplaintCase c = dataStore.getCases().get(caseId);
        if (c == null) throw new IllegalArgumentException("Case not found: " + caseId);

        String now = LocalDateTime.now().format(FORMATTER);
        ActionHistoryItem item = new ActionHistoryItem(
                "AH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                now,
                actionTaken,
                performedBy,
                bureau,
                remarks
        );
        c.getActionHistory().add(item);

        auditService.log(
                "ACTION_ADDED",
                performedBy,
                bureau,
                "Action added to " + caseId + ": " + actionTaken + " - " + remarks,
                c.getAictePermanentId(),
                caseId
        );

        return item;
    }
}
