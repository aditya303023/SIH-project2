package com.disasterdecoded.nivaranflow.service;

import com.disasterdecoded.nivaranflow.model.ComplaintCase;
import com.disasterdecoded.nivaranflow.model.OverlapAlert;
import com.disasterdecoded.nivaranflow.model.ReviewDecision;
import com.disasterdecoded.nivaranflow.repository.DataStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OverlapDetectionService {
    private final DataStore dataStore = DataStore.getInstance();
    private final AuditService auditService = new AuditService();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Inspects active cases for a given institution and triggers an OverlapAlert
     * if active cases span across multiple AICTE Bureaus/Cells.
     */
    public OverlapAlert checkAndFlagOverlap(String aictePermanentId) {
        List<ComplaintCase> instCases = dataStore.getCases().values().stream()
                .filter(c -> aictePermanentId.equalsIgnoreCase(c.getAictePermanentId()))
                .filter(c -> !"Closed".equalsIgnoreCase(c.getCurrentStatus()) && !"Resolved".equalsIgnoreCase(c.getCurrentStatus()))
                .collect(Collectors.toList());

        Set<String> bureaus = instCases.stream().map(ComplaintCase::getBureau).collect(Collectors.toSet());

        if (bureaus.size() > 1) {
            // Find if there's an existing active alert for this institution
            Optional<OverlapAlert> existing = dataStore.getOverlapAlerts().values().stream()
                    .filter(a -> aictePermanentId.equalsIgnoreCase(a.getAictePermanentId()))
                    .filter(a -> "PENDING_REVIEW".equalsIgnoreCase(a.getStatus()))
                    .findFirst();

            List<String> caseIds = instCases.stream().map(ComplaintCase::getId).collect(Collectors.toList());
            String instName = instCases.get(0).getInstitutionName();

            if (existing.isPresent()) {
                OverlapAlert alert = existing.get();
                alert.setCaseIds(caseIds);
                alert.setBureaus(new ArrayList<>(bureaus));
                return alert;
            } else {
                String alertId = "OA-" + (100 + dataStore.getOverlapAlerts().size() + 1);
                String now = LocalDateTime.now().format(FORMATTER);
                String desc = "Multi-Bureau Conflict Alert: Active cases found across bureaus ["
                        + String.join(", ", bureaus) + "] for " + instName + ". Human review recommended before parallel action.";

                OverlapAlert newAlert = new OverlapAlert(
                        alertId,
                        aictePermanentId,
                        instName,
                        caseIds,
                        new ArrayList<>(bureaus),
                        "PENDING_REVIEW",
                        now,
                        desc
                );

                dataStore.getOverlapAlerts().put(alertId, newAlert);

                // Mark cases with alert flag
                for (ComplaintCase c : instCases) {
                    c.setHasOverlapAlert(true);
                    c.setOverlapAlertId(alertId);
                }

                auditService.log(
                        "OVERLAP_ALERT_GENERATED",
                        "NIVARANFLOW Engine",
                        "Smart Automation",
                        desc,
                        aictePermanentId,
                        String.join(", ", caseIds)
                );

                return newAlert;
            }
        }
        return null;
    }

    public List<OverlapAlert> getAllAlerts() {
        return new ArrayList<>(dataStore.getOverlapAlerts().values());
    }

    public OverlapAlert getAlertById(String alertId) {
        return dataStore.getOverlapAlerts().get(alertId);
    }

    /**
     * Slide 3: Step 5 - Record the decision
     * "Link related cases or keep them separate, record why, and show the owner and next step.
     * No automatic case linking: every match remains a human decision."
     */
    public synchronized ReviewDecision recordHumanDecision(String alertId, String decisionType, String justification,
                                             String designatedOwner, String designatedBureau,
                                             String agreedNextStep, String reviewedBy) {
        OverlapAlert alert = dataStore.getOverlapAlerts().get(alertId);
        if (alert == null) {
            throw new IllegalArgumentException("Overlap alert not found: " + alertId);
        }

        String now = LocalDateTime.now().format(FORMATTER);
        String decId = "DEC-" + (dataStore.getDecisions().size() + 101);

        ReviewDecision decision = new ReviewDecision(
                decId,
                alertId,
                alert.getAictePermanentId(),
                decisionType,
                justification,
                designatedOwner,
                designatedBureau,
                agreedNextStep,
                reviewedBy,
                now,
                alert.getCaseIds()
        );

        alert.setStatus(decisionType);
        alert.setReviewDecision(decision);
        dataStore.getDecisions().put(alertId, decision);

        // Update cases accordingly
        List<String> cIds = alert.getCaseIds();
        if ("LINKED".equalsIgnoreCase(decisionType)) {
            for (String cid : cIds) {
                ComplaintCase c = dataStore.getCases().get(cid);
                if (c != null) {
                    List<String> others = cIds.stream().filter(id -> !id.equals(cid)).collect(Collectors.toList());
                    for (String otherId : others) {
                        if (!c.getLinkedCaseIds().contains(otherId)) {
                            c.getLinkedCaseIds().add(otherId);
                        }
                    }
                    c.setCurrentStatus("Joint Bureau Review");
                    if (designatedOwner != null && !designatedOwner.isBlank()) {
                        c.setCaseOwner(designatedOwner + " (" + designatedBureau + " Coordinated)");
                    }
                    if (agreedNextStep != null && !agreedNextStep.isBlank()) {
                        c.setNextStep(agreedNextStep);
                    }
                }
            }
        }

        auditService.log(
                "DECISION_RECORDED",
                reviewedBy,
                designatedBureau,
                "Official review completed for alert " + alertId + " (" + alert.getInstitutionName() + "). Decision: "
                        + decisionType + ". Rationale: " + justification + ". Designated Owner: " + designatedOwner + ". Next: " + agreedNextStep,
                alert.getAictePermanentId(),
                String.join(", ", cIds)
        );

        return decision;
    }
}
