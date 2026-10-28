package com.disasterdecoded.nivaranflow.model;

import java.util.ArrayList;
import java.util.List;

public class ReviewDecision {
    private String id;
    private String overlapAlertId;
    private String aictePermanentId;
    private String decisionType; // "LINKED" or "KEPT_SEPARATE"
    private String justification; // Reason why
    private String designatedOwner; // Designated Lead Bureau/Official
    private String designatedBureau;
    private String agreedNextStep;
    private String reviewedBy; // Authorized official name & designation
    private String reviewedAt;
    private List<String> caseIds = new ArrayList<>();

    public ReviewDecision() {}

    public ReviewDecision(String id, String overlapAlertId, String aictePermanentId, String decisionType,
                          String justification, String designatedOwner, String designatedBureau,
                          String agreedNextStep, String reviewedBy, String reviewedAt, List<String> caseIds) {
        this.id = id;
        this.overlapAlertId = overlapAlertId;
        this.aictePermanentId = aictePermanentId;
        this.decisionType = decisionType;
        this.justification = justification;
        this.designatedOwner = designatedOwner;
        this.designatedBureau = designatedBureau;
        this.agreedNextStep = agreedNextStep;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.caseIds = caseIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOverlapAlertId() { return overlapAlertId; }
    public void setOverlapAlertId(String overlapAlertId) { this.overlapAlertId = overlapAlertId; }

    public String getAictePermanentId() { return aictePermanentId; }
    public void setAictePermanentId(String aictePermanentId) { this.aictePermanentId = aictePermanentId; }

    public String getDecisionType() { return decisionType; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getDesignatedOwner() { return designatedOwner; }
    public void setDesignatedOwner(String designatedOwner) { this.designatedOwner = designatedOwner; }

    public String getDesignatedBureau() { return designatedBureau; }
    public void setDesignatedBureau(String designatedBureau) { this.designatedBureau = designatedBureau; }

    public String getAgreedNextStep() { return agreedNextStep; }
    public void setAgreedNextStep(String agreedNextStep) { this.agreedNextStep = agreedNextStep; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }

    public List<String> getCaseIds() { return caseIds; }
    public void setCaseIds(List<String> caseIds) { this.caseIds = caseIds; }
}
