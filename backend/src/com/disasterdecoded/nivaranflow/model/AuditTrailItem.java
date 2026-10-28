package com.disasterdecoded.nivaranflow.model;

public class AuditTrailItem {
    private String id;
    private String timestamp;
    private String action;
    private String actor;
    private String bureau;
    private String details;
    private String aictePermanentId;
    private String caseId;

    public AuditTrailItem() {}

    public AuditTrailItem(String id, String timestamp, String action, String actor, String bureau, String details, String aictePermanentId, String caseId) {
        this.id = id;
        this.timestamp = timestamp;
        this.action = action;
        this.actor = actor;
        this.bureau = bureau;
        this.details = details;
        this.aictePermanentId = aictePermanentId;
        this.caseId = caseId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getBureau() { return bureau; }
    public void setBureau(String bureau) { this.bureau = bureau; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getAictePermanentId() { return aictePermanentId; }
    public void setAictePermanentId(String aictePermanentId) { this.aictePermanentId = aictePermanentId; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }
}
