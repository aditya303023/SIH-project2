/**
 * NIVARANFLOW - Frontend API Client
 * Supports both Live Java REST Backend and Seamless Client-side Demo Fallback (for GitHub Pages)
 */

const API_BASE = '/api';
const isGitHubPages = window.location.hostname.includes('github.io');

// Pre-seeded offline mock data matching SIH 2026 slides (for static hosting on GitHub Pages)
const initialSeedData = {
  stats: {
    totalCases: 8,
    activeCases: 8,
    totalInstitutions: 5,
    totalOverlapsDetected: 3,
    pendingOverlaps: 1,
    linkedOverlaps: 1,
    separateOverlaps: 1
  },
  institutions: {
    "1-1092837461": {
      aictePermanentId: "1-1092837461",
      name: "Institution A (Apex Institute of Engineering & Technology)",
      state: "Maharashtra",
      city: "Pune",
      institutionType: "Engineering & Technology (UG/PG)",
      approvalStatus: "Approved (2024-2027)",
      nodalEmail: "principal@apextech.edu.in",
      activeCaseCount: 2
    },
    "1-4829104712": {
      aictePermanentId: "1-4829104712",
      name: "National Institute of Advanced Science & Technology",
      state: "Karnataka",
      city: "Bengaluru",
      institutionType: "Engineering & Management",
      approvalStatus: "Approved with Condition",
      nodalEmail: "director@niast.edu.in",
      activeCaseCount: 2
    },
    "1-7738291045": {
      aictePermanentId: "1-7738291045",
      name: "Kaveri Institute of Technology",
      state: "Tamil Nadu",
      city: "Chennai",
      institutionType: "Technical Degree Institute",
      approvalStatus: "Under Scrutiny",
      nodalEmail: "nodal.aicte@kaveri.edu.in",
      activeCaseCount: 2
    }
  },
  cases: [
    {
      id: "P-101",
      aictePermanentId: "1-1092837461",
      institutionName: "Institution A (Apex Institute of Engineering & Technology)",
      complaintType: "Faculty Cadre Grievance & Salary Delay",
      bureau: "PGRC",
      currentStatus: "Under Review",
      caseOwner: "Dr. Sunita Rao (Grievance Officer, PGRC)",
      nextStep: "Examine faculty council representation and bank disbursement logs",
      deadlineDate: "2026-11-12",
      filedDate: "2026-10-14",
      priority: "Urgent",
      complainantType: "Faculty Association",
      description: "Grievance petition submitted by faculty association alleging 4 months salary arrears and unilateral contract amendments without statutory body ratification.",
      hasOverlapAlert: true,
      overlapAlertId: "OA-101",
      linkedCaseIds: [],
      actionHistory: [
        { id: "AH-01", timestamp: "2026-10-14 11:30", actionTaken: "Grievance Registered", performedBy: "Dr. Sunita Rao", bureau: "PGRC", remarks: "Formal complaint logged via AICTE PGRC Portal." },
        { id: "AH-02", timestamp: "2026-10-18 15:45", actionTaken: "Clarification Notice Issued", performedBy: "Dr. Sunita Rao", bureau: "PGRC", remarks: "Show-cause letter sent to Chairman of Governing Body." }
      ]
    },
    {
      id: "R-204",
      aictePermanentId: "1-1092837461",
      institutionName: "Institution A (Apex Institute of Engineering & Technology)",
      complaintType: "Faculty Cadre Ratio & Cadre Deficit Non-Compliance",
      bureau: "Regulation Bureau",
      currentStatus: "Pending Review",
      caseOwner: "Er. Amit Verma (Deputy Director, Regulation Bureau)",
      nextStep: "Review Standing Hearing Committee (SHC) report on cadre deficit",
      deadlineDate: "2026-11-18",
      filedDate: "2026-10-20",
      priority: "High",
      complainantType: "AICTE Regional Office Report",
      description: "Surprise scrutiny inspection revealed 38% cadre deficit in CSE and Mechanical departments in violation of AICTE Approval Process Handbook norm 7.10.",
      hasOverlapAlert: true,
      overlapAlertId: "OA-101",
      linkedCaseIds: [],
      actionHistory: [
        { id: "AH-03", timestamp: "2026-10-20 10:15", actionTaken: "Deficiency Notice Drafted", performedBy: "Er. Amit Verma", bureau: "Regulation Bureau", remarks: "Scrutiny committee flagged persistent cadre ratio breach." }
      ]
    },
    {
      id: "A-312",
      aictePermanentId: "1-4829104712",
      institutionName: "National Institute of Advanced Science & Technology",
      complaintType: "Unapproved Intake Expansion",
      bureau: "Approval Bureau",
      currentStatus: "Pending Joint Review",
      caseOwner: "Smt. K. Meenakshi (Assistant Director, Approval)",
      nextStep: "Consolidate SCSC verification finding with EVC inspection roster",
      deadlineDate: "2026-11-20",
      filedDate: "2026-10-05",
      priority: "High",
      complainantType: "Whistleblower",
      description: "Allegation of conducting 60 additional unapproved seats in AI & Data Science program.",
      hasOverlapAlert: true,
      overlapAlertId: "OA-102",
      linkedCaseIds: ["S-504"],
      actionHistory: []
    },
    {
      id: "S-504",
      aictePermanentId: "1-4829104712",
      institutionName: "National Institute of Advanced Science & Technology",
      complaintType: "Laboratory & Computer Center Deficit",
      bureau: "SCSC",
      currentStatus: "Pending Joint Review",
      caseOwner: "Prof. Arvind Joshi (Member Secretary, SCSC)",
      nextStep: "Conduct joint video-scrutiny session alongside Approval Bureau",
      deadlineDate: "2026-11-20",
      filedDate: "2026-10-10",
      priority: "High",
      complainantType: "Student Committee",
      description: "Standing Complaint Scrutiny Committee verifying shortage of 120 required computing workstations for increased intake.",
      hasOverlapAlert: true,
      overlapAlertId: "OA-102",
      linkedCaseIds: ["A-312"],
      actionHistory: []
    },
    {
      id: "L-089",
      aictePermanentId: "1-7738291045",
      institutionName: "Kaveri Institute of Technology",
      complaintType: "Court Matter: Fee Regularization WP No. 4412/2026",
      bureau: "Legal Cell",
      currentStatus: "Active",
      caseOwner: "Adv. V. Sundaram (Legal Counsel, AICTE)",
      nextStep: "File counter-affidavit before Hon'ble High Court",
      deadlineDate: "2026-11-25",
      filedDate: "2026-09-28",
      priority: "Urgent",
      complainantType: "High Court Notice",
      description: "Institution filed writ against State Fee Regulatory Committee and AICTE directives.",
      hasOverlapAlert: true,
      overlapAlertId: "OA-103",
      linkedCaseIds: [],
      actionHistory: []
    },
    {
      id: "P-142",
      aictePermanentId: "1-7738291045",
      institutionName: "Kaveri Institute of Technology",
      complaintType: "Capitation Fee & Compulsory Hostel Fee Coercion",
      bureau: "PGRC",
      currentStatus: "Active",
      caseOwner: "Dr. Sunita Rao (Grievance Officer, PGRC)",
      nextStep: "Obtain statement from institutional ombudsperson",
      deadlineDate: "2026-11-30",
      filedDate: "2026-10-22",
      priority: "Medium",
      complainantType: "Student Council",
      description: "Parents alleged mandatory hostel fee deduction even for day scholar students.",
      hasOverlapAlert: true,
      overlapAlertId: "OA-103",
      linkedCaseIds: [],
      actionHistory: []
    }
  ],
  overlaps: [
    {
      id: "OA-101",
      aictePermanentId: "1-1092837461",
      institutionName: "Institution A (Apex Institute of Engineering & Technology)",
      caseIds: ["P-101", "R-204"],
      bureaus: ["PGRC", "Regulation Bureau"],
      status: "PENDING_REVIEW",
      detectedDate: "2026-10-20 10:15",
      alertDescription: "Cross-Bureau Case Detected: Same institution has concurrent cases in PGRC (P-101) regarding faculty salary/cadre and Regulation Bureau (R-204) regarding cadre deficit. Action required to prevent duplicated inquiry or contradictory orders."
    },
    {
      id: "OA-102",
      aictePermanentId: "1-4829104712",
      institutionName: "National Institute of Advanced Science & Technology",
      caseIds: ["A-312", "S-504"],
      bureaus: ["Approval Bureau", "SCSC"],
      status: "LINKED",
      detectedDate: "2026-10-10 12:00",
      alertDescription: "Parallel intake expansion and infrastructure deficiency complaints identified.",
      reviewDecision: {
        id: "DEC-201",
        decisionType: "LINKED",
        justification: "Both cases directly affect institutional capacity norms for academic year 2026-27. Synchronizing scrutiny prevents repetitive hearings.",
        designatedOwner: "Prof. Arvind Joshi (Member Secretary, SCSC)",
        agreedNextStep: "Convene combined EVC hearing on 2026-11-20 with both Bureau representatives present.",
        reviewedBy: "Dr. Alok Kumar (Advisor-I, AICTE HQ)",
        reviewedAt: "2026-10-12 16:30"
      }
    },
    {
      id: "OA-103",
      aictePermanentId: "1-7738291045",
      institutionName: "Kaveri Institute of Technology",
      caseIds: ["L-089", "P-142"],
      bureaus: ["Legal Cell", "PGRC"],
      status: "KEPT_SEPARATE",
      detectedDate: "2026-10-22 17:00",
      alertDescription: "Legal matter and student welfare complaint detected for same institution.",
      reviewDecision: {
        id: "DEC-301",
        decisionType: "KEPT_SEPARATE",
        justification: "High Court writ petition L-089 is sub judice regarding statutory fees, whereas P-142 concerns internal administrative hostel grievance. Separate tracks maintained.",
        designatedOwner: "Adv. V. Sundaram (Legal Cell)",
        agreedNextStep: "Proceed with judicial counter-affidavit independently while PGRC obtains ombudsman reply.",
        reviewedBy: "Dr. Alok Kumar (Advisor-I, AICTE HQ)",
        reviewedAt: "2026-10-24 11:15"
      }
    }
  ],
  audit: [
    { id: "AUD-101", timestamp: "2026-10-20 10:15", action: "OVERLAP_ALERT_GENERATED", actor: "NIVARANFLOW Engine", bureau: "System", details: "Generated overlap alert OA-101 for Institution A across PGRC (P-101) and Regulation Bureau (R-204).", aictePermanentId: "1-1092837461" },
    { id: "AUD-102", timestamp: "2026-10-24 11:15", action: "DECISION_RECORDED", actor: "Dr. Alok Kumar", bureau: "AICTE HQ Executive", details: "Decided KEPT_SEPARATE for OA-103 (Kaveri Institute). Reason: Matter sub judice in High Court.", aictePermanentId: "1-7738291045" },
    { id: "AUD-103", timestamp: "2026-10-12 16:30", action: "DECISION_RECORDED", actor: "Dr. Alok Kumar", bureau: "AICTE HQ Executive", details: "Decided LINKED for OA-102 (National Institute). Combined EVC hearing set.", aictePermanentId: "1-4829104712" }
  ]
};

function getLocalStore() {
  const stored = localStorage.getItem('nivaranflow_state');
  if (stored) {
    try { return JSON.parse(stored); } catch (e) {}
  }
  localStorage.setItem('nivaranflow_state', JSON.stringify(initialSeedData));
  return JSON.parse(JSON.stringify(initialSeedData));
}

function saveLocalStore(data) {
  localStorage.setItem('nivaranflow_state', JSON.stringify(data));
}

const api = {
  async getStats() {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/stats`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }
    const data = getLocalStore();
    return data.stats;
  },

  async getCases(filters = {}) {
    if (!isGitHubPages) {
      try {
        const params = new URLSearchParams();
        if (filters.bureau && filters.bureau !== 'ALL') params.set('bureau', filters.bureau);
        if (filters.status && filters.status !== 'ALL') params.set('status', filters.status);
        if (filters.priority && filters.priority !== 'ALL') params.set('priority', filters.priority);
        if (filters.query) params.set('query', filters.query);

        const res = await fetch(`${API_BASE}/cases?${params.toString()}`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }

    const data = getLocalStore();
    let list = data.cases;
    if (filters.bureau && filters.bureau !== 'ALL') list = list.filter(c => c.bureau.toLowerCase() === filters.bureau.toLowerCase());
    if (filters.status && filters.status !== 'ALL') list = list.filter(c => c.currentStatus.toLowerCase() === filters.status.toLowerCase());
    if (filters.priority && filters.priority !== 'ALL') list = list.filter(c => c.priority.toLowerCase() === filters.priority.toLowerCase());
    if (filters.query) {
      const q = filters.query.toLowerCase();
      list = list.filter(c => c.id.toLowerCase().includes(q) || c.institutionName.toLowerCase().includes(q) || c.aictePermanentId.includes(q) || c.complaintType.toLowerCase().includes(q));
    }
    return list;
  },

  async getCaseById(caseId) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/cases/${encodeURIComponent(caseId)}`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }
    const data = getLocalStore();
    const c = data.cases.find(x => x.id === caseId);
    if (!c) throw new Error(`Case ${caseId} not found`);
    return c;
  },

  async createCase(caseData) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/cases`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(caseData)
        });
        if (res.ok) return await res.json();
      } catch (e) {}
    }

    const data = getLocalStore();
    const prefix = caseData.bureau ? caseData.bureau.substring(0, 1).toUpperCase() + '-' : 'C-';
    const newId = prefix + (data.cases.length + 101);
    const newCase = {
      ...caseData,
      id: newId,
      currentStatus: 'Active',
      filedDate: new Date().toISOString().substring(0, 10),
      actionHistory: [{
        id: 'AH-' + Math.floor(Math.random()*9000+1000),
        timestamp: new Date().toLocaleString(),
        actionTaken: 'Case Logged in System',
        performedBy: caseData.caseOwner || 'Authorized Official',
        bureau: caseData.bureau,
        remarks: 'Initial complaint registration under AICTE ' + caseData.bureau
      }],
      linkedCaseIds: []
    };

    // Check overlap
    const existing = data.cases.filter(c => c.aictePermanentId === caseData.aictePermanentId);
    const bureaus = new Set(existing.map(c => c.bureau));
    bureaus.add(caseData.bureau);

    if (bureaus.size > 1) {
      const alertId = 'OA-' + (data.overlaps.length + 101);
      const newAlert = {
        id: alertId,
        aictePermanentId: caseData.aictePermanentId,
        institutionName: caseData.institutionName,
        caseIds: [...existing.map(c => c.id), newId],
        bureaus: Array.from(bureaus),
        status: 'PENDING_REVIEW',
        detectedDate: new Date().toLocaleString(),
        alertDescription: `Multi-Bureau Conflict Alert: Active cases found across [${Array.from(bureaus).join(', ')}] for ${caseData.institutionName}.`
      };
      newCase.hasOverlapAlert = true;
      newCase.overlapAlertId = alertId;
      data.overlaps.unshift(newAlert);
      data.stats.pendingOverlaps++;
    }

    data.cases.push(newCase);
    data.stats.totalCases++;
    data.stats.activeCases++;
    saveLocalStore(data);
    return newCase;
  },

  async addCaseAction(caseId, actionData) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/cases/${encodeURIComponent(caseId)}/actions`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(actionData)
        });
        if (res.ok) return await res.json();
      } catch (e) {}
    }

    const data = getLocalStore();
    const c = data.cases.find(x => x.id === caseId);
    if (!c) throw new Error('Case not found');
    const newAction = {
      id: 'AH-' + Math.floor(Math.random()*9000+1000),
      timestamp: new Date().toLocaleString(),
      ...actionData
    };
    c.actionHistory = c.actionHistory || [];
    c.actionHistory.push(newAction);
    saveLocalStore(data);
    return newAction;
  },

  async getOverlaps() {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/overlaps`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }
    const data = getLocalStore();
    return data.overlaps;
  },

  async getOverlapById(alertId) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/overlaps/${encodeURIComponent(alertId)}`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }
    const data = getLocalStore();
    const alert = data.overlaps.find(o => o.id === alertId);
    if (!alert) throw new Error('Alert not found');
    const cases = data.cases.filter(c => alert.caseIds.includes(c.id));
    return { alert, cases };
  },

  async recordDecision(alertId, decisionPayload) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/overlaps/${encodeURIComponent(alertId)}/decision`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(decisionPayload)
        });
        if (res.ok) return await res.json();
      } catch (e) {}
    }

    const data = getLocalStore();
    const alert = data.overlaps.find(o => o.id === alertId);
    if (!alert) throw new Error('Alert not found');

    alert.status = decisionPayload.decisionType;
    alert.reviewDecision = {
      id: 'DEC-' + Math.floor(Math.random()*900+100),
      reviewedAt: new Date().toLocaleString(),
      ...decisionPayload
    };

    if (decisionPayload.decisionType === 'LINKED') {
      alert.caseIds.forEach(cid => {
        const c = data.cases.find(x => x.id === cid);
        if (c) {
          c.currentStatus = 'Joint Bureau Review';
          c.linkedCaseIds = alert.caseIds.filter(id => id !== cid);
          if (decisionPayload.designatedOwner) c.caseOwner = decisionPayload.designatedOwner;
          if (decisionPayload.agreedNextStep) c.nextStep = decisionPayload.agreedNextStep;
        }
      });
      data.stats.linkedOverlaps = (data.stats.linkedOverlaps || 0) + 1;
    } else {
      data.stats.separateOverlaps = (data.stats.separateOverlaps || 0) + 1;
    }
    data.stats.pendingOverlaps = Math.max(0, (data.stats.pendingOverlaps || 1) - 1);

    data.audit.unshift({
      id: 'AUD-' + Math.floor(Math.random()*9000+1000),
      timestamp: new Date().toLocaleString(),
      action: 'DECISION_RECORDED',
      actor: decisionPayload.reviewedBy,
      bureau: decisionPayload.designatedBureau || 'AICTE HQ',
      details: `Official review for ${alertId}. Decision: ${decisionPayload.decisionType}. Rationale: ${decisionPayload.justification}`,
      aictePermanentId: alert.aictePermanentId
    });

    saveLocalStore(data);
    return alert.reviewDecision;
  },

  async getInstitutions() {
    const data = getLocalStore();
    return Object.values(data.institutions);
  },

  async getInstitutionProfile(permanentId) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/institutions/${encodeURIComponent(permanentId)}`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }
    const data = getLocalStore();
    const inst = data.institutions[permanentId] || {
      aictePermanentId: permanentId,
      name: `Institution (${permanentId})`,
      city: 'Delhi NCR',
      state: 'Delhi',
      institutionType: 'Technical College',
      approvalStatus: 'Approved'
    };
    const cases = data.cases.filter(c => c.aictePermanentId === permanentId);
    const bureaus = Array.from(new Set(cases.map(c => c.bureau)));
    return {
      institution: inst,
      totalCases: cases.length,
      activeCasesCount: cases.filter(c => c.currentStatus !== 'Closed').length,
      involvedBureaus: bureaus,
      hasCrossBureauOverlap: bureaus.length > 1,
      cases
    };
  },

  async getAuditLogs() {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/audit`);
        if (res.ok) return await res.json();
      } catch (e) {}
    }
    const data = getLocalStore();
    return data.audit;
  },

  async login(email, password) {
    if (!isGitHubPages) {
      try {
        const res = await fetch(`${API_BASE}/auth/login`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, password })
        });
        if (res.ok) {
          const result = await res.json();
          localStorage.setItem('nivaranflow_user', JSON.stringify(result.user));
          return result;
        }
      } catch (e) {}
    }

    // Client-side fallback for GitHub Pages
    const demoUsers = {
      'admin@aicte-india.org': {
        id: 'USR-01', name: 'Dr. Alok Kumar', email: 'admin@aicte-india.org',
        role: 'AICTE_HQ_EXECUTIVE', designation: 'Advisor-I & Chief Grievance Appellate',
        bureau: 'AICTE Headquarters', clearanceLevel: 'Level 1 (All Bureaus)',
        institutionId: null, avatar: '👨‍💼', token: 'NF-JWT-DEMO-HQ'
      },
      'pgrc@aicte-india.org': {
        id: 'USR-02', name: 'Dr. Sunita Rao', email: 'pgrc@aicte-india.org',
        role: 'PGRC_OFFICER', designation: 'Grievance Redressal Officer',
        bureau: 'PGRC', clearanceLevel: 'Level 2 (Grievance Cell)',
        institutionId: null, avatar: '👩‍⚖️', token: 'NF-JWT-DEMO-PGRC'
      },
      'regulation@aicte-india.org': {
        id: 'USR-03', name: 'Er. Amit Verma', email: 'regulation@aicte-india.org',
        role: 'REGULATION_OFFICER', designation: 'Deputy Director (Norms & Compliance)',
        bureau: 'Regulation Bureau', clearanceLevel: 'Level 2 (Regulation)',
        institutionId: null, avatar: '👨‍💻', token: 'NF-JWT-DEMO-REG'
      },
      'approval@aicte-india.org': {
        id: 'USR-04', name: 'Smt. K. Meenakshi', email: 'approval@aicte-india.org',
        role: 'APPROVAL_OFFICER', designation: 'Assistant Director (Extension of Approval)',
        bureau: 'Approval Bureau', clearanceLevel: 'Level 2 (Approval)',
        institutionId: null, avatar: '👩‍💼', token: 'NF-JWT-DEMO-APP'
      },
      'principal@apextech.edu.in': {
        id: 'USR-05', name: 'Dr. S. K. Kulkarni', email: 'principal@apextech.edu.in',
        role: 'INSTITUTION_NODAL', designation: 'Principal & AICTE Nodal Liaison',
        bureau: 'Institution A (Apex Tech)', clearanceLevel: 'Level 3 (Institution)',
        institutionId: '1-1092837461', avatar: '🏛️', token: 'NF-JWT-DEMO-INST'
      }
    };

    const user = demoUsers[email.toLowerCase()] || {
      id: 'USR-CUSTOM',
      name: email.split('@')[0].toUpperCase(),
      email,
      role: 'AICTE_STAFF',
      designation: 'Authorized AICTE Official',
      bureau: 'AICTE General',
      clearanceLevel: 'Level 2',
      institutionId: null,
      avatar: '👤',
      token: 'NF-JWT-CUSTOM'
    };

    localStorage.setItem('nivaranflow_user', JSON.stringify(user));
    return { success: true, token: user.token, user };
  },

  getCurrentUser() {
    const saved = localStorage.getItem('nivaranflow_user');
    if (saved) {
      try { return JSON.parse(saved); } catch (e) {}
    }
    // Default to AICTE HQ Executive for immediate rich evaluation
    const defaultUser = {
      id: 'USR-01', name: 'Dr. Alok Kumar', email: 'admin@aicte-india.org',
      role: 'AICTE_HQ_EXECUTIVE', designation: 'Advisor-I & Chief Grievance Appellate',
      bureau: 'AICTE Headquarters', clearanceLevel: 'Level 1 (All Bureaus)',
      institutionId: null, avatar: '👨‍💼', token: 'NF-JWT-DEFAULT'
    };
    localStorage.setItem('nivaranflow_user', JSON.stringify(defaultUser));
    return defaultUser;
  },

  logout() {
    localStorage.removeItem('nivaranflow_user');
  }
};
