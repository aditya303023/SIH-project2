package com.disasterdecoded.nivaranflow.model;

public class Institution {
    private String aictePermanentId;
    private String name;
    private String state;
    private String city;
    private String institutionType;
    private String approvalStatus;
    private String nodalEmail;
    private int activeCaseCount;

    public Institution() {}

    public Institution(String aictePermanentId, String name, String state, String city, String institutionType, String approvalStatus, String nodalEmail, int activeCaseCount) {
        this.aictePermanentId = aictePermanentId;
        this.name = name;
        this.state = state;
        this.city = city;
        this.institutionType = institutionType;
        this.approvalStatus = approvalStatus;
        this.nodalEmail = nodalEmail;
        this.activeCaseCount = activeCaseCount;
    }

    public String getAictePermanentId() { return aictePermanentId; }
    public void setAictePermanentId(String aictePermanentId) { this.aictePermanentId = aictePermanentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getInstitutionType() { return institutionType; }
    public void setInstitutionType(String institutionType) { this.institutionType = institutionType; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getNodalEmail() { return nodalEmail; }
    public void setNodalEmail(String nodalEmail) { this.nodalEmail = nodalEmail; }

    public int getActiveCaseCount() { return activeCaseCount; }
    public void setActiveCaseCount(int activeCaseCount) { this.activeCaseCount = activeCaseCount; }
}
