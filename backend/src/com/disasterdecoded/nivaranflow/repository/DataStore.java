package com.disasterdecoded.nivaranflow.repository;

import com.disasterdecoded.nivaranflow.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory data store seeded with official SIH 2026 slide cases.
 */
public class DataStore {
    private static final DataStore INSTANCE = new DataStore();

    private final Map<String, ComplaintCase> cases = new ConcurrentHashMap<>();
    private final Map<String, Institution> institutions = new ConcurrentHashMap<>();
    private final Map<String, OverlapAlert> overlapAlerts = new ConcurrentHashMap<>();
    private final Map<String, ReviewDecision> decisions = new ConcurrentHashMap<>();
    private final List<AuditTrailItem> auditTrail = Collections.synchronizedList(new ArrayList<>());

    private DataStore() {
        seedInitialData();
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    private void seedInitialData() {
        // --- 1. Institutions ---
        Institution instA = new Institution(
                "1-1092837461",
                "Institution A (Apex Institute of Engineering & Technology)",
                "Maharashtra",
                "Pune",
                "Engineering & Technology (UG/PG)",
                "Approved (2024-2027)",
                "principal@apextech.edu.in",
                2
        );

        Institution instB = new Institution(
                "1-4829104712",
                "National Institute of Advanced Science & Technology",
                "Karnataka",
                "Bengaluru",
                "Engineering & Management",
                "Approved with Condition",
                "director@niast.edu.in",
                2
        );

        Institution instC = new Institution(
                "1-7738291045",
                "Kaveri Institute of Technology",
                "Tamil Nadu",
                "Chennai",
                "Technical Degree Institute",
                "Under Scrutiny",
                "nodal.aicte@kaveri.edu.in",
                2
        );

        Institution instD = new Institution(
                "1-9928174620",
                "Modern Polytechnic & Skill Institute",
                "Uttar Pradesh",
                "Lucknow",
                "Diploma Polytechnic",
                "Approved",
                "registrar@modernpoly.edu.in",
                1
        );

        Institution instE = new Institution(
                "1-3345829104",
                "Global Business Academy",
                "Delhi NCR",
                "New Delhi",
                "Management (PGDM)",
                "Approved",
                "contact@globalba.ac.in",
                1
        );

        institutions.put(instA.getAictePermanentId(), instA);
        institutions.put(instB.getAictePermanentId(), instB);
        institutions.put(instC.getAictePermanentId(), instC);
        institutions.put(instD.getAictePermanentId(), instD);
        institutions.put(instE.getAictePermanentId(), instE);

        // --- 2. Complaint Cases (Matching Slide 1 & 2 Fictional Example exactly!) ---
        ComplaintCase caseP101 = new ComplaintCase(
                "P-101",
                "1-1092837461",
                "Institution A (Apex Institute of Engineering & Technology)",
                "Faculty Cadre Grievance & Salary Delay",
                "PGRC",
                "Under Review",
                "Dr. Sunita Rao (Grievance Officer, PGRC)",
                "Examine faculty council representation and bank disbursement logs",
                "2026-11-12",
                "2026-10-14",
                "Urgent",
                "Faculty Association",
                "Grievance petition submitted by faculty association alleging 4 months salary arrears and unilateral contract amendments without statutory body ratification."
        );
        caseP101.setHasOverlapAlert(true);
        caseP101.setOverlapAlertId("OA-101");
        caseP101.getActionHistory().add(new ActionHistoryItem(
                "AH-01", "2026-10-14 11:30", "Grievance Registered", "Dr. Sunita Rao", "PGRC", "Formal complaint logged via AICTE PGRC Portal."
        ));
        caseP101.getActionHistory().add(new ActionHistoryItem(
                "AH-02", "2026-10-18 15:45", "Clarification Notice Issued", "Dr. Sunita Rao", "PGRC", "Show-cause letter sent to Chairman of Governing Body."
        ));

        ComplaintCase caseR204 = new ComplaintCase(
                "R-204",
                "1-1092837461",
                "Institution A (Apex Institute of Engineering & Technology)",
                "Faculty Cadre Ratio & Cadre Deficit Non-Compliance",
                "Regulation Bureau",
                "Pending Review",
                "Er. Amit Verma (Deputy Director, Regulation Bureau)",
                "Review Standing Hearing Committee (SHC) report on cadre deficit",
                "2026-11-18",
                "2026-10-20",
                "High",
                "AICTE Regional Office Report",
                "Surprise surprise/scrutiny inspection revealed 38% cadre deficit in CSE and Mechanical departments in violation of AICTE Approval Process Handbook norm 7.10."
        );
        caseR204.setHasOverlapAlert(true);
        caseR204.setOverlapAlertId("OA-101");
        caseR204.getActionHistory().add(new ActionHistoryItem(
                "AH-03", "2026-10-20 10:15", "Deficiency Notice Drafted", "Er. Amit Verma", "Regulation Bureau", "Scrutiny committee flagged persistent cadre ratio breach."
        ));

        // Institution B cases (Example 2: SCSC + Approval Bureau)
        ComplaintCase caseA312 = new ComplaintCase(
                "A-312",
                "1-4829104712",
                "National Institute of Advanced Science & Technology",
                "Unapproved Intake Expansion",
                "Approval Bureau",
                "Pending Joint Review",
                "Smt. K. Meenakshi (Assistant Director, Approval)",
                "Consolidate SCSC verification finding with EVC inspection roster",
                "2026-11-20",
                "2026-10-05",
                "High",
                "Whistleblower",
                "Allegation of conducting 60 additional unapproved seats in AI & Data Science program."
        );
        caseA312.setHasOverlapAlert(true);
        caseA312.setOverlapAlertId("OA-102");
        caseA312.getLinkedCaseIds().add("S-504");

        ComplaintCase caseS504 = new ComplaintCase(
                "S-504",
                "1-4829104712",
                "National Institute of Advanced Science & Technology",
                "Laboratory & Computer Center Deficit",
                "SCSC",
                "Pending Joint Review",
                "Prof. Arvind Joshi (Member Secretary, SCSC)",
                "Conduct joint video-scrutiny session alongside Approval Bureau",
                "2026-11-20",
                "2026-10-10",
                "High",
                "Student Committee",
                "Standing Complaint Scrutiny Committee verifying shortage of 120 required computing workstations for increased intake."
        );
        caseS504.setHasOverlapAlert(true);
        caseS504.setOverlapAlertId("OA-102");
        caseS504.getLinkedCaseIds().add("A-312");

        // Institution C cases (Example 3: Legal Cell + PGRC)
        ComplaintCase caseL089 = new ComplaintCase(
                "L-089",
                "1-7738291045",
                "Kaveri Institute of Technology",
                "Court Matter: Fee Regularization WP No. 4412/2026",
                "Legal Cell",
                "Active",
                "Adv. V. Sundaram (Legal Counsel, AICTE)",
                "File counter-affidavit before Hon'ble High Court",
                "2026-11-25",
                "2026-09-28",
                "Urgent",
                "High Court Notice",
                "Institution filed writ against State Fee Regulatory Committee and AICTE directives."
        );
        caseL089.setHasOverlapAlert(true);
        caseL089.setOverlapAlertId("OA-103");

        ComplaintCase caseP142 = new ComplaintCase(
                "P-142",
                "1-7738291045",
                "Kaveri Institute of Technology",
                "Capitation Fee & Compulsory Hostel Fee Coercion",
                "PGRC",
                "Active",
                "Dr. Sunita Rao (Grievance Officer, PGRC)",
                "Obtain statement from institutional ombudsperson",
                "2026-11-30",
                "2026-10-22",
                "Medium",
                "Student Council",
                "Parents alleged mandatory hostel fee deduction even for day scholar students."
        );
        caseP142.setHasOverlapAlert(true);
        caseP142.setOverlapAlertId("OA-103");

        // Case D & E (Single bureau cases)
        ComplaintCase caseA405 = new ComplaintCase(
                "A-405",
                "1-9928174620",
                "Modern Polytechnic & Skill Institute",
                "Delayed Land Title Mutation for Extension of Approval",
                "Approval Bureau",
                "Active",
                "Smt. K. Meenakshi (Approval Bureau)",
                "Await certified revenue revenue map from Tehsildar office",
                "2026-12-05",
                "2026-10-19",
                "Medium",
                "Institutional Compliance",
                "Land registration mutation records pending submission for 2026-27 extension."
        );

        ComplaintCase caseR118 = new ComplaintCase(
                "R-118",
                "1-3345829104",
                "Global Business Academy",
                "Mandatory Disclosure & Placement Data Non-Disclosure",
                "Regulation Bureau",
                "Active",
                "Er. Amit Verma (Regulation Bureau)",
                "Issue advisory reminder before imposing penalty fine",
                "2026-12-10",
                "2026-10-24",
                "Low",
                "Public Grievance",
                "Website mandatory disclosure link broken and faculty profile list omitted."
        );

        cases.put(caseP101.getId(), caseP101);
        cases.put(caseR204.getId(), caseR204);
        cases.put(caseA312.getId(), caseA312);
        cases.put(caseS504.getId(), caseS504);
        cases.put(caseL089.getId(), caseL089);
        cases.put(caseP142.getId(), caseP142);
        cases.put(caseA405.getId(), caseA405);
        cases.put(caseR118.getId(), caseR118);

        // --- 3. Overlap Alerts ---
        // Alert 1: Unresolved alert for Institution A (matching slide 1 & 2)
        OverlapAlert alert1 = new OverlapAlert(
                "OA-101",
                "1-1092837461",
                "Institution A (Apex Institute of Engineering & Technology)",
                Arrays.asList("P-101", "R-204"),
                Arrays.asList("PGRC", "Regulation Bureau"),
                "PENDING_REVIEW",
                "2026-10-20 10:15",
                "Cross-Bureau Case Detected: Same institution has concurrent cases in PGRC (P-101) regarding faculty salary/cadre and Regulation Bureau (R-204) regarding cadre deficit. Action required to prevent duplicated inquiry or contradictory orders."
        );

        // Alert 2: Pre-decided LINKED alert (National Institute)
        OverlapAlert alert2 = new OverlapAlert(
                "OA-102",
                "1-4829104712",
                "National Institute of Advanced Science & Technology",
                Arrays.asList("A-312", "S-504"),
                Arrays.asList("Approval Bureau", "SCSC"),
                "LINKED",
                "2026-10-10 12:00",
                "Parallel intake expansion and infrastructure deficiency complaints identified."
        );
        ReviewDecision decision2 = new ReviewDecision(
                "DEC-201",
                "OA-102",
                "1-4829104712",
                "LINKED",
                "Both cases directly affect institutional capacity norms for academic year 2026-27. Synchronizing scrutiny prevents repetitive hearings.",
                "Prof. Arvind Joshi (Member Secretary, SCSC)",
                "SCSC & Approval Bureau Joint Panel",
                "Convene combined EVC hearing on 2026-11-20 with both Bureau representatives present.",
                "Dr. Alok Kumar (Advisor-I, AICTE HQ)",
                "2026-10-12 16:30",
                Arrays.asList("A-312", "S-504")
        );
        alert2.setReviewDecision(decision2);
        decisions.put(alert2.getId(), decision2);

        // Alert 3: Pre-decided KEPT_SEPARATE alert (Kaveri Institute)
        OverlapAlert alert3 = new OverlapAlert(
                "OA-103",
                "1-7738291045",
                "Kaveri Institute of Technology",
                Arrays.asList("L-089", "P-142"),
                Arrays.asList("Legal Cell", "PGRC"),
                "KEPT_SEPARATE",
                "2026-10-22 17:00",
                "Legal matter and student welfare complaint detected for same institution."
        );
        ReviewDecision decision3 = new ReviewDecision(
                "DEC-301",
                "OA-103",
                "1-7738291045",
                "KEPT_SEPARATE",
                "High Court writ petition L-089 is sub judice regarding statutory fees, whereas P-142 concerns internal administrative hostel grievance. Separate tracks maintained to avoid legal contempt.",
                "Adv. V. Sundaram (Legal Cell)",
                "Legal Cell",
                "Proceed with judicial counter-affidavit independently while PGRC obtains ombudsman reply.",
                "Dr. Alok Kumar (Advisor-I, AICTE HQ)",
                "2026-10-24 11:15",
                Arrays.asList("L-089", "P-142")
        );
        alert3.setReviewDecision(decision3);
        decisions.put(alert3.getId(), decision3);

        overlapAlerts.put(alert1.getId(), alert1);
        overlapAlerts.put(alert2.getId(), alert2);
        overlapAlerts.put(alert3.getId(), alert3);

        // --- 4. Audit Trail ---
        auditTrail.add(new AuditTrailItem(
                "AUD-101", "2026-10-20 10:15", "OVERLAP_ALERT_GENERATED", "NIVARANFLOW Engine", "System",
                "Generated overlap alert OA-101 for Institution A across PGRC (P-101) and Regulation Bureau (R-204).",
                "1-1092837461", "P-101, R-204"
        ));
        auditTrail.add(new AuditTrailItem(
                "AUD-102", "2026-10-24 11:15", "DECISION_RECORDED", "Dr. Alok Kumar", "AICTE HQ Executive",
                "Decided KEPT_SEPARATE for OA-103 (Kaveri Institute). Reason: Matter sub judice in High Court.",
                "1-7738291045", "L-089, P-142"
        ));
        auditTrail.add(new AuditTrailItem(
                "AUD-103", "2026-10-12 16:30", "DECISION_RECORDED", "Dr. Alok Kumar", "AICTE HQ Executive",
                "Decided LINKED for OA-102 (National Institute). Combined EVC hearing set.",
                "1-4829104712", "A-312, S-504"
        ));
    }

    public Map<String, ComplaintCase> getCases() { return cases; }
    public Map<String, Institution> getInstitutions() { return institutions; }
    public Map<String, OverlapAlert> getOverlapAlerts() { return overlapAlerts; }
    public Map<String, ReviewDecision> getDecisions() { return decisions; }
    public List<AuditTrailItem> getAuditTrail() { return auditTrail; }
}
