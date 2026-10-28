package com.disasterdecoded.nivaranflow.model;

import java.util.ArrayList;
import java.util.List;

public class ComplaintCase {
    private String id;
    private String aictePermanentId;
    private String institutionName;
    private String complaintType;
    private String bureau;
    private String currentStatus;
    private String caseOwner;
    private String nextStep;
    private String deadlineDate;
    private String filedDate;
    private String priority;
    private String complainantType;
    private String description;
    private boolean hasOverlapAlert;
    private String overlapAlertId;
    private List<String> linkedCaseIds = new ArrayList<>();
    private List<ActionHistoryItem> actionHistory = new ArrayList<>();

    public ComplaintCase() {}

    public ComplaintCase(String id, String aictePermanentId, String institutionName, String complaintType,
                         String bureau, String currentStatus, String caseOwner, String nextStep,
                         String deadlineDate, String filedDate, String priority, String complainantType,
                         String description) {
        this.id = id;
        this.aictePermanentId = aictePermanentId;
        this.institutionName = institutionName;
        this.complaintType = complaintType;
        this.bureau = bureau;
        this.currentStatus = currentStatus;
        this.caseOwner = caseOwner;
        this.nextStep = nextStep;
        this.deadlineDate = deadlineDate;
        this.filedDate = filedDate;
        this.priority = priority;
        this.complainantType = complainantType;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAictePermanentId() { return aictePermanentId; }
    public void setAictePermanentId(String aictePermanentId) { this.aictePermanentId = aictePermanentId; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }

    public String getBureau() { return bureau; }
    public void setBureau(String bureau) { this.bureau = bureau; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getCaseOwner() { return caseOwner; }
    public void setCaseOwner(String caseOwner) { this.caseOwner = caseOwner; }

    public String getNextStep() { return nextStep; }
    public void setNextStep(String nextStep) { this.nextStep = nextStep; }

    public String getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(String deadlineDate) { this.deadlineDate = deadlineDate; }

    public String getFiledDate() { return filedDate; }
    public void setFiledDate(String filedDate) { this.filedDate = filedDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getComplainantType() { return complainantType; }
    public void setComplainantType(String complainantType) { this.complainantType = complainantType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isHasOverlapAlert() { return hasOverlapAlert; }
    public void setHasOverlapAlert(boolean hasOverlapAlert) { this.hasOverlapAlert = hasOverlapAlert; }

    public String getOverlapAlertId() { return overlapAlertId; }
    public void setOverlapAlertId(String overlapAlertId) { this.overlapAlertId = overlapAlertId; }

    public List<String> getLinkedCaseIds() { return linkedCaseIds; }
    public void setLinkedCaseIds(List<String> linkedCaseIds) { this.linkedCaseIds = linkedCaseIds; }

    public List<ActionHistoryItem> getActionHistory() { return actionHistory; }
    public void setActionHistory(List<ActionHistoryItem> actionHistory) { this.actionHistory = actionHistory; }
}
