package com.disasterdecoded.nivaranflow.model;

import java.util.ArrayList;
import java.util.List;

public class OverlapAlert {
    private String id;
    private String aictePermanentId;
    private String institutionName;
    private List<String> caseIds = new ArrayList<>();
    private List<String> bureaus = new ArrayList<>();
    private String status; // PENDING_REVIEW, LINKED, KEPT_SEPARATE
    private String detectedDate;
    private String alertDescription;
    private ReviewDecision reviewDecision;

    public OverlapAlert() {}

    public OverlapAlert(String id, String aictePermanentId, String institutionName, List<String> caseIds,
                        List<String> bureaus, String status, String detectedDate, String alertDescription) {
        this.id = id;
        this.aictePermanentId = aictePermanentId;
        this.institutionName = institutionName;
        this.caseIds = caseIds;
        this.bureaus = bureaus;
        this.status = status;
        this.detectedDate = detectedDate;
        this.alertDescription = alertDescription;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAictePermanentId() { return aictePermanentId; }
    public void setAictePermanentId(String aictePermanentId) { this.aictePermanentId = aictePermanentId; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public List<String> getCaseIds() { return caseIds; }
    public void setCaseIds(List<String> caseIds) { this.caseIds = caseIds; }

    public List<String> getBureaus() { return bureaus; }
    public void setBureaus(List<String> bureaus) { this.bureaus = bureaus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDetectedDate() { return detectedDate; }
    public void setDetectedDate(String detectedDate) { this.detectedDate = detectedDate; }

    public String getAlertDescription() { return alertDescription; }
    public void setAlertDescription(String alertDescription) { this.alertDescription = alertDescription; }

    public ReviewDecision getReviewDecision() { return reviewDecision; }
    public void setReviewDecision(ReviewDecision reviewDecision) { this.reviewDecision = reviewDecision; }
}
