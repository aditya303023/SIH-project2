package com.disasterdecoded.nivaranflow.model;

public class ActionHistoryItem {
    private String id;
    private String timestamp;
    private String actionTaken;
    private String performedBy;
    private String bureau;
    private String remarks;

    public ActionHistoryItem() {}

    public ActionHistoryItem(String id, String timestamp, String actionTaken, String performedBy, String bureau, String remarks) {
        this.id = id;
        this.timestamp = timestamp;
        this.actionTaken = actionTaken;
        this.performedBy = performedBy;
        this.bureau = bureau;
        this.remarks = remarks;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getBureau() { return bureau; }
    public void setBureau(String bureau) { this.bureau = bureau; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
