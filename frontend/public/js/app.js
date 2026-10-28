/**
 * NIVARANFLOW - Main Application Logic
 * Problem Statement: SIH26264 (AICTE Inter-Bureau Integrated Tracking)
 * Team: DISASTER DECODED (Team ID: 198035)
 */

document.addEventListener('DOMContentLoaded', () => {
  // Application State
  const state = {
    cases: [],
    overlaps: [],
    stats: {},
    activeBureau: 'ALL',
    activeStatus: 'ALL',
    activePriority: 'ALL',
    searchQuery: '',
    selectedOverlapForReview: null,
    selectedInstitutionId: null,
    selectedCaseId: null
  };

  // DOM Elements
  const heroAlertBanner = document.getElementById('heroAlertBanner');
  const alertHeadline = document.getElementById('alertHeadline');
  const alertDetails = document.getElementById('alertDetails');
  const btnReviewHeroAlert = document.getElementById('btnReviewHeroAlert');
  const casesTableBody = document.getElementById('casesTableBody');
  const alertsFeedList = document.getElementById('alertsFeedList');
  const globalSearchInput = document.getElementById('globalSearchInput');
  const toastContainer = document.getElementById('toastContainer');

  // Modals
  const reviewModal = document.getElementById('reviewModal');
  const caseDetailModal = document.getElementById('caseDetailModal');
  const newCaseModal = document.getElementById('newCaseModal');
  const instProfileModal = document.getElementById('instProfileModal');
  const auditModal = document.getElementById('auditModal');

  // --- Initializer ---
  async function init() {
    setupEventListeners();
    await loadDashboardData();
  }

  // --- Data Fetching ---
  async function loadDashboardData() {
    try {
      await Promise.all([
        refreshStats(),
        refreshCases(),
        refreshOverlaps()
      ]);
    } catch (err) {
      console.error('Error loading dashboard data:', err);
      showToast('Error loading dashboard data: ' + err.message, 'error');
    }
  }

  async function refreshStats() {
    try {
      const stats = await api.getStats();
      state.stats = stats;
      document.getElementById('statTotalCases').textContent = stats.totalCases ?? 0;
      document.getElementById('statActiveCases').textContent = stats.activeCases ?? 0;
      document.getElementById('statPendingOverlaps').textContent = stats.pendingOverlaps ?? 0;
      document.getElementById('statLinkedCases').textContent = stats.linkedOverlaps ?? 0;
    } catch (e) {
      console.warn('Could not refresh stats', e);
    }
  }

  async function refreshCases() {
    try {
      const cases = await api.getCases({
        bureau: state.activeBureau,
        status: state.activeStatus,
        priority: state.activePriority,
        query: state.searchQuery
      });
      state.cases = cases;
      renderCasesTable(cases);
    } catch (e) {
      console.error(e);
      showToast('Could not fetch cases', 'error');
    }
  }

  async function refreshOverlaps() {
    try {
      const overlaps = await api.getOverlaps();
      state.overlaps = overlaps;
      renderOverlapsFeed(overlaps);
      updateHeroAlertBanner(overlaps);
    } catch (e) {
      console.error(e);
    }
  }

  // --- Rendering UI ---

  function updateHeroAlertBanner(overlaps) {
    // Find highest priority pending overlap (e.g. Institution A)
    const pending = overlaps.filter(o => o.status === 'PENDING_REVIEW');
    if (pending.length > 0) {
      const topAlert = pending[0];
      heroAlertBanner.style.display = 'flex';
      alertHeadline.innerHTML = `⚠️ Possible Cross-Bureau Overlap Detected: <strong>${escapeHtml(topAlert.institutionName)}</strong>`;
      alertDetails.textContent = topAlert.alertDescription;
      document.getElementById('heroCaseTags').innerHTML = topAlert.caseIds.map(cid => 
        `<span style="background:rgba(255,255,255,0.12); padding:2px 8px; border-radius:4px; font-family:monospace; font-weight:700;">Case ${escapeHtml(cid)}</span>`
      ).join(' + ') + ` <span style="opacity:0.85;">(${topAlert.bureaus.join(' & ')})</span>`;

      btnReviewHeroAlert.onclick = () => openOverlapReviewModal(topAlert.id);
    } else {
      heroAlertBanner.style.display = 'none';
    }
  }

  function renderCasesTable(cases) {
    if (!cases || cases.length === 0) {
      casesTableBody.innerHTML = `
        <tr>
          <td colspan="7" style="text-align:center; padding: 2.5rem; color: var(--text-muted);">
            No complaint cases found matching criteria.
          </td>
        </tr>`;
      return;
    }

    casesTableBody.innerHTML = cases.map(c => {
      const bureauClass = getBureauTagClass(c.bureau);
      const statusClass = getStatusClass(c.currentStatus);
      const priorityClass = getPriorityClass(c.priority);

      let overlapBadge = '';
      if (c.hasOverlapAlert) {
        overlapBadge = `
          <div class="overlap-pill" onclick="window.appOpenOverlap('${escapeHtml(c.overlapAlertId)}')">
            <span style="display:inline-block; width:6px; height:6px; border-radius:50%; background:#F59E0B;"></span>
            Cross-Bureau Alert
          </div>`;
      }

      let linkedBadge = '';
      if (c.linkedCaseIds && c.linkedCaseIds.length > 0) {
        linkedBadge = `
          <div style="font-size:0.68rem; color:var(--emerald); margin-top:2px;">
            🔗 Linked to ${c.linkedCaseIds.join(', ')}
          </div>`;
      }

      return `
        <tr>
          <td>
            <div class="case-id-badge" onclick="window.appOpenCaseDetail('${escapeHtml(c.id)}')">
              <span>📄</span> ${escapeHtml(c.id)}
            </div>
          </td>
          <td>
            <div class="institution-cell">
              <span class="inst-name" onclick="window.appOpenInstitution('${escapeHtml(c.aictePermanentId)}')">${escapeHtml(c.institutionName)}</span>
              <span class="inst-id">AICTE ID: ${escapeHtml(c.aictePermanentId)}</span>
              ${overlapBadge}
              ${linkedBadge}
            </div>
          </td>
          <td>
            <span class="bureau-tag ${bureauClass}">${escapeHtml(c.bureau)}</span>
            <div style="font-size:0.75rem; color:var(--text-secondary); margin-top:0.2rem;">${escapeHtml(c.complaintType)}</div>
          </td>
          <td>
            <span class="status-pill ${statusClass}">${escapeHtml(c.currentStatus)}</span>
          </td>
          <td>
            <span class="priority-badge ${priorityClass}">${escapeHtml(c.priority)}</span>
          </td>
          <td>
            <div style="font-size:0.78rem; font-weight:600; color:var(--text-primary);">${escapeHtml(c.caseOwner || 'Unassigned')}</div>
            <div style="font-size:0.72rem; color:var(--text-muted); max-width:200px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${escapeHtml(c.nextStep || 'Pending action plan')}</div>
          </td>
          <td>
            <div style="display:flex; gap:0.4rem;">
              <button class="btn btn-secondary" style="padding:0.35rem 0.65rem; font-size:0.75rem;" onclick="window.appOpenCaseDetail('${escapeHtml(c.id)}')">View</button>
            </div>
          </td>
        </tr>
      `;
    }).join('');
  }

  function renderOverlapsFeed(overlaps) {
    if (!overlaps || overlaps.length === 0) {
      alertsFeedList.innerHTML = `<div style="text-align:center; padding:1.5rem; color:var(--text-muted); font-size:0.8rem;">No active cross-bureau conflicts.</div>`;
      return;
    }

    alertsFeedList.innerHTML = overlaps.map(alert => {
      let statusBadge = '';
      if (alert.status === 'PENDING_REVIEW') {
        statusBadge = `<span class="alert-item-status status-pending">Action Needed</span>`;
      } else if (alert.status === 'LINKED') {
        statusBadge = `<span class="alert-item-status status-linked">Linked</span>`;
      } else {
        statusBadge = `<span class="alert-item-status status-separated">Kept Separate</span>`;
      }

      return `
        <div class="alert-feed-item">
          <div class="alert-feed-header">
            <span style="font-family:monospace; font-size:0.75rem; font-weight:700; color:var(--text-muted);">${escapeHtml(alert.id)}</span>
            ${statusBadge}
          </div>
          <div class="alert-feed-body">
            <h4>${escapeHtml(alert.institutionName)}</h4>
            <p>${escapeHtml(alert.alertDescription)}</p>
          </div>
          <div class="alert-feed-meta">
            <span>Cases: ${alert.caseIds.join(', ')}</span>
            <button class="btn btn-secondary" style="padding:0.25rem 0.6rem; font-size:0.72rem;" onclick="window.appOpenOverlap('${escapeHtml(alert.id)}')">
              ${alert.status === 'PENDING_REVIEW' ? 'Review & Link' : 'Inspect Decision'}
            </button>
          </div>
        </div>
      `;
    }).join('');
  }

  // --- Overlap Review & Human Decision Modal (Slide 3: Steps 3, 4, 5) ---

  async function openOverlapReviewModal(alertId) {
    try {
      const data = await api.getOverlapById(alertId);
      const alert = data.alert;
      const cases = data.cases;
      state.selectedOverlapForReview = alert;

      document.getElementById('reviewModalInstTitle').textContent = alert.institutionName;
      document.getElementById('reviewModalInstId').textContent = `AICTE Permanent ID: ${alert.aictePermanentId} | Alert ID: ${alert.id}`;
      document.getElementById('reviewAlertSummary').textContent = alert.alertDescription;

      // Render Comparison Grid
      const compContainer = document.getElementById('reviewComparisonContainer');
      compContainer.innerHTML = cases.map(c => `
        <div class="comparison-card highlight">
          <div class="comp-header">
            <span style="font-family:monospace; font-size:1rem; font-weight:800; color:var(--text-primary);">${escapeHtml(c.id)}</span>
            <span class="bureau-tag ${getBureauTagClass(c.bureau)}">${escapeHtml(c.bureau)}</span>
          </div>
          <div class="comp-field">
            <label>Complaint Type</label>
            <span style="font-weight:600;">${escapeHtml(c.complaintType)}</span>
          </div>
          <div class="comp-field">
            <label>Current Status</label>
            <span class="status-pill ${getStatusClass(c.currentStatus)}">${escapeHtml(c.currentStatus)}</span>
          </div>
          <div class="comp-field">
            <label>Case Owner</label>
            <span>${escapeHtml(c.caseOwner || 'Unassigned')}</span>
          </div>
          <div class="comp-field">
            <label>Filed Date</label>
            <span>${escapeHtml(c.filedDate || 'N/A')}</span>
          </div>
          <div class="comp-field">
            <label>Allegation Summary</label>
            <p style="font-size:0.78rem; color:var(--text-secondary); line-height:1.4;">${escapeHtml(c.description || 'No description recorded')}</p>
          </div>
          <div class="comp-field">
            <label>Current Next Step</label>
            <span style="font-size:0.78rem; color:#FCD34D;">${escapeHtml(c.nextStep || 'None recorded')}</span>
          </div>
        </div>
      `).join('');

      // Form & Decisions
      const formBox = document.getElementById('reviewDecisionFormSection');
      const decisionViewBox = document.getElementById('existingDecisionBox');

      if (alert.status === 'PENDING_REVIEW') {
        formBox.style.display = 'block';
        decisionViewBox.style.display = 'none';

        // Pre-fill suggested values
        document.getElementById('decisionOfficialName').value = 'Dr. Alok Kumar (Advisor-I, AICTE HQ)';
        document.getElementById('decisionJustification').value = `Cross-bureau inquiry confirms that ${alert.institutionName} faces concurrent inquiries in ${alert.bureaus.join(' and ')}. Cases share overlapping subject matter regarding regulatory compliance.`;
        document.getElementById('decisionLeadBureau').value = alert.bureaus[0] || 'PGRC';
        document.getElementById('decisionLeadOwner').value = cases[0]?.caseOwner || 'Joint Bureau Panel';
        document.getElementById('decisionAgreedNext').value = 'Convene combined scrutiny hearing with representatives from both bureaus.';

        selectDecisionType('LINKED');
      } else {
        formBox.style.display = 'none';
        decisionViewBox.style.display = 'block';

        const dec = alert.reviewDecision;
        document.getElementById('existingDecisionType').textContent = alert.status === 'LINKED' ? '🔗 Cases Officially Linked' : '🛡️ Cases Kept Separate';
        document.getElementById('existingDecisionType').className = alert.status === 'LINKED' ? 'bureau-tag tag-approval' : 'bureau-tag tag-pgrc';
        document.getElementById('existingDecisionWhy').textContent = dec?.justification || 'No justification recorded';
        document.getElementById('existingDecisionOwner').textContent = dec?.designatedOwner || 'N/A';
        document.getElementById('existingDecisionNext').textContent = dec?.agreedNextStep || 'N/A';
        document.getElementById('existingDecisionOfficial').textContent = `${dec?.reviewedBy || 'Authorized Official'} at ${dec?.reviewedAt || 'N/A'}`;
      }

      openModal(reviewModal);
    } catch (e) {
      console.error(e);
      showToast('Failed to load overlap review details: ' + e.message, 'error');
    }
  }

  let currentDecisionType = 'LINKED';
  function selectDecisionType(type) {
    currentDecisionType = type;
    const cardLink = document.getElementById('optLinkCases');
    const cardSeparate = document.getElementById('optSeparateCases');

    if (type === 'LINKED') {
      cardLink.classList.add('selected-link');
      cardSeparate.classList.remove('selected-separate');
    } else {
      cardSeparate.classList.add('selected-separate');
      cardLink.classList.remove('selected-link');
    }
  }

  async function submitDecision() {
    if (!state.selectedOverlapForReview) return;
    const alertId = state.selectedOverlapForReview.id;

    const justification = document.getElementById('decisionJustification').value.trim();
    const designatedOwner = document.getElementById('decisionLeadOwner').value.trim();
    const designatedBureau = document.getElementById('decisionLeadBureau').value.trim();
    const agreedNextStep = document.getElementById('decisionAgreedNext').value.trim();
    const reviewedBy = document.getElementById('decisionOfficialName').value.trim();

    if (!justification) {
      showToast('Please provide an official justification / reason for the decision.', 'warning');
      return;
    }

    try {
      await api.recordDecision(alertId, {
        decisionType: currentDecisionType,
        justification,
        designatedOwner,
        designatedBureau,
        agreedNextStep,
        reviewedBy
      });

      closeModal(reviewModal);
      showToast(
        currentDecisionType === 'LINKED' 
          ? 'Cases successfully linked! Joint bureau review assigned.' 
          : 'Decision recorded: Cases maintained in separate tracks.',
        'success'
      );

      await loadDashboardData();
    } catch (e) {
      console.error(e);
      showToast('Error recording decision: ' + e.message, 'error');
    }
  }

  // --- Record New Case Wizard (Slide 3: Step 1) ---

  function openNewCaseModal() {
    document.getElementById('newCaseForm').reset();
    document.getElementById('newCaseOverlapWarning').style.display = 'none';
    openModal(newCaseModal);
  }

  async function checkPermanentIdOverlap(permanentId) {
    if (!permanentId || permanentId.trim().length < 4) {
      document.getElementById('newCaseOverlapWarning').style.display = 'none';
      return;
    }

    try {
      const inst = await api.getInstitutionProfile(permanentId.trim());
      if (inst && inst.cases && inst.cases.length > 0) {
        const activeCases = inst.cases.filter(c => c.currentStatus !== 'Closed' && c.currentStatus !== 'Resolved');
        const bureaus = [...new Set(activeCases.map(c => c.bureau))];

        if (bureaus.length > 0) {
          const warnBox = document.getElementById('newCaseOverlapWarning');
          warnBox.style.display = 'block';
          warnBox.innerHTML = `
            <strong>⚠️ Overlap Pre-Check:</strong> Institution <em>${escapeHtml(inst.institution.name)}</em> already has ${activeCases.length} active case(s) in <strong>[${bureaus.join(', ')}]</strong>.
            Registering this case will automatically trigger an Overlap Alert for cross-bureau review.
          `;
          document.getElementById('inputInstitutionName').value = inst.institution.name;
        }
      }
    } catch (e) {
      // Institution may be brand new, ignore
      document.getElementById('newCaseOverlapWarning').style.display = 'none';
    }
  }

  async function handleNewCaseSubmit(e) {
    e.preventDefault();

    const aictePermanentId = document.getElementById('inputAicteId').value.trim();
    const institutionName = document.getElementById('inputInstitutionName').value.trim();
    const bureau = document.getElementById('inputBureau').value;
    const complaintType = document.getElementById('inputComplaintType').value.trim();
    const priority = document.getElementById('inputPriority').value;
    const caseOwner = document.getElementById('inputCaseOwner').value.trim();
    const nextStep = document.getElementById('inputNextStep').value.trim();
    const description = document.getElementById('inputDescription').value.trim();
    const complainantType = document.getElementById('inputComplainantType').value;

    if (!aictePermanentId || !institutionName || !complaintType) {
      showToast('Please fill in all mandatory fields.', 'warning');
      return;
    }

    try {
      const created = await api.createCase({
        aictePermanentId,
        institutionName,
        bureau,
        complaintType,
        priority,
        caseOwner,
        nextStep,
        description,
        complainantType
      });

      closeModal(newCaseModal);
      showToast(`Case ${created.id} registered successfully!`, 'success');

      if (created.hasOverlapAlert) {
        showToast(`Cross-Bureau Overlap Flagged: Alert ${created.overlapAlertId} generated!`, 'warning');
      }

      await loadDashboardData();
    } catch (err) {
      console.error(err);
      showToast('Failed to create case: ' + err.message, 'error');
    }
  }

  // --- Institution 360° Profile (Slide 1 & 2: "One shared institution profile") ---

  async function openInstitutionModal(permanentId) {
    try {
      const profile = await api.getInstitutionProfile(permanentId);
      state.selectedInstitutionId = permanentId;
      const inst = profile.institution;
      const cases = profile.cases;

      document.getElementById('instProfileName').textContent = inst.name;
      document.getElementById('instProfileId').textContent = `AICTE Permanent ID: ${inst.aictePermanentId}`;
      document.getElementById('instProfileState').textContent = `${inst.city}, ${inst.state}`;
      document.getElementById('instProfileType').textContent = inst.institutionType || 'Technical College';
      document.getElementById('instProfileApproval').textContent = inst.approvalStatus || 'Approved';
      document.getElementById('instProfileEmail').textContent = inst.nodalEmail || 'N/A';

      document.getElementById('instTotalCasesBadge').textContent = profile.totalCases;
      document.getElementById('instActiveCasesBadge').textContent = profile.activeCasesCount;
      document.getElementById('instBureausBadge').textContent = profile.involvedBureaus.join(', ') || 'None';

      const casesContainer = document.getElementById('instProfileCasesList');
      casesContainer.innerHTML = cases.map(c => `
        <div class="alert-feed-item">
          <div class="alert-feed-header">
            <span style="font-family:monospace; font-weight:700;">${escapeHtml(c.id)}</span>
            <span class="bureau-tag ${getBureauTagClass(c.bureau)}">${escapeHtml(c.bureau)}</span>
            <span class="status-pill ${getStatusClass(c.currentStatus)}">${escapeHtml(c.currentStatus)}</span>
          </div>
          <div style="font-weight:600; font-size:0.85rem; margin-top:0.3rem;">${escapeHtml(c.complaintType)}</div>
          <p style="font-size:0.75rem; color:var(--text-secondary); margin-top:0.25rem;">${escapeHtml(c.description)}</p>
          <div style="font-size:0.72rem; color:var(--text-muted); margin-top:0.4rem; display:flex; justify-content:space-between;">
            <span>Owner: <strong>${escapeHtml(c.caseOwner || 'Unassigned')}</strong></span>
            <span>Next: <strong>${escapeHtml(c.nextStep || 'N/A')}</strong></span>
          </div>
        </div>
      `).join('');

      openModal(instProfileModal);
    } catch (e) {
      console.error(e);
      showToast('Could not load institution dossier: ' + e.message, 'error');
    }
  }

  // --- Case Detail & Timeline Modal ---

  async function openCaseDetailModal(caseId) {
    try {
      const c = await api.getCaseById(caseId);
      state.selectedCaseId = caseId;

      document.getElementById('detailCaseId').textContent = c.id;
      document.getElementById('detailInstitution').textContent = `${c.institutionName} (${c.aictePermanentId})`;
      document.getElementById('detailBureau').textContent = c.bureau;
      document.getElementById('detailBureau').className = `bureau-tag ${getBureauTagClass(c.bureau)}`;
      document.getElementById('detailStatus').textContent = c.currentStatus;
      document.getElementById('detailStatus').className = `status-pill ${getStatusClass(c.currentStatus)}`;
      document.getElementById('detailPriority').textContent = c.priority;
      document.getElementById('detailPriority').className = `priority-badge ${getPriorityClass(c.priority)}`;
      document.getElementById('detailOwner').textContent = c.caseOwner || 'Unassigned';
      document.getElementById('detailNextStep').textContent = c.nextStep || 'None recorded';
      document.getElementById('detailDeadline').textContent = c.deadlineDate || 'None set';
      document.getElementById('detailFiled').textContent = c.filedDate || 'N/A';
      document.getElementById('detailDescription').textContent = c.description || 'No description provided.';

      // Linked cases
      const linkedBox = document.getElementById('detailLinkedCases');
      if (c.linkedCaseIds && c.linkedCaseIds.length > 0) {
        linkedBox.style.display = 'block';
        document.getElementById('detailLinkedCasesText').textContent = c.linkedCaseIds.join(', ');
      } else {
        linkedBox.style.display = 'none';
      }

      // Action History Timeline
      const timeline = document.getElementById('detailTimeline');
      timeline.innerHTML = (c.actionHistory || []).map(item => `
        <div class="timeline-item">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-meta">
              <strong>${escapeHtml(item.actionTaken)}</strong>
              <span>${escapeHtml(item.timestamp)}</span>
            </div>
            <div style="font-size:0.75rem; color:var(--text-secondary);">${escapeHtml(item.remarks)}</div>
            <div style="font-size:0.7rem; color:var(--text-muted); margin-top:0.2rem;">By: ${escapeHtml(item.performedBy)} (${escapeHtml(item.bureau)})</div>
          </div>
        </div>
      `).join('');

      openModal(caseDetailModal);
    } catch (e) {
      console.error(e);
      showToast('Could not load case details: ' + e.message, 'error');
    }
  }

  async function handleAddActionSubmit(e) {
    e.preventDefault();
    if (!state.selectedCaseId) return;

    const actionTaken = document.getElementById('newActionTaken').value.trim();
    const performedBy = document.getElementById('newActionPerformedBy').value.trim();
    const bureau = document.getElementById('newActionBureau').value;
    const remarks = document.getElementById('newActionRemarks').value.trim();

    if (!actionTaken || !performedBy) {
      showToast('Please specify action and official name.', 'warning');
      return;
    }

    try {
      await api.addCaseAction(state.selectedCaseId, {
        actionTaken,
        performedBy,
        bureau,
        remarks
      });

      document.getElementById('newActionForm').reset();
      showToast('Action logged in case history.', 'success');
      await openCaseDetailModal(state.selectedCaseId);
      await refreshCases();
    } catch (err) {
      console.error(err);
      showToast('Failed to log action: ' + err.message, 'error');
    }
  }

  // --- Audit Trail Modal (Slide 4: "review trail that staff can understand") ---

  async function openAuditTrailModal() {
    try {
      const logs = await api.getAuditLogs();
      const list = document.getElementById('auditLogsList');
      list.innerHTML = logs.map(item => `
        <div class="alert-feed-item">
          <div class="alert-feed-header">
            <span style="font-family:monospace; font-weight:700; color:var(--primary-light);">${escapeHtml(item.action)}</span>
            <span style="font-size:0.72rem; color:var(--text-muted);">${escapeHtml(item.timestamp)}</span>
          </div>
          <p style="font-size:0.8rem; color:var(--text-primary); margin-top:0.25rem;">${escapeHtml(item.details)}</p>
          <div style="font-size:0.72rem; color:var(--text-muted); margin-top:0.35rem; display:flex; justify-content:space-between;">
            <span>Actor: <strong>${escapeHtml(item.actor)}</strong> (${escapeHtml(item.bureau)})</span>
            <span>AICTE ID: ${escapeHtml(item.aictePermanentId || 'N/A')}</span>
          </div>
        </div>
      `).join('');

      openModal(auditModal);
    } catch (e) {
      console.error(e);
      showToast('Failed to load audit logs: ' + e.message, 'error');
    }
  }

  // --- Event Listeners ---

  function setupEventListeners() {
    // Bureau Filters
    document.querySelectorAll('.filter-chip').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('.filter-chip').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        state.activeBureau = btn.dataset.bureau;
        refreshCases();
      });
    });

    // Priority Filter
    document.getElementById('filterPrioritySelect').addEventListener('change', (e) => {
      state.activePriority = e.target.value;
      refreshCases();
    });

    // Status Filter
    document.getElementById('filterStatusSelect').addEventListener('change', (e) => {
      state.activeStatus = e.target.value;
      refreshCases();
    });

    // Global Search
    let debounceTimer;
    globalSearchInput.addEventListener('input', (e) => {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        state.searchQuery = e.target.value.trim();
        refreshCases();
      }, 250);
    });

    // Keyboard shortcut '/'
    window.addEventListener('keydown', (e) => {
      if (e.key === '/' && document.activeElement !== globalSearchInput && !document.querySelector('.modal-backdrop.open')) {
        e.preventDefault();
        globalSearchInput.focus();
      }
    });

    // Buttons
    document.getElementById('btnNewCase').addEventListener('click', openNewCaseModal);
    document.getElementById('btnViewAudit').addEventListener('click', openAuditTrailModal);
    document.getElementById('optLinkCases').addEventListener('click', () => selectDecisionType('LINKED'));
    document.getElementById('optSeparateCases').addEventListener('click', () => selectDecisionType('KEPT_SEPARATE'));
    document.getElementById('btnSubmitDecision').addEventListener('click', submitDecision);
    document.getElementById('newCaseForm').addEventListener('submit', handleNewCaseSubmit);
    document.getElementById('newActionForm').addEventListener('submit', handleAddActionSubmit);

    // AICTE ID instant overlap pre-check
    document.getElementById('inputAicteId').addEventListener('input', (e) => {
      checkPermanentIdOverlap(e.target.value);
    });

    // Modal Close buttons
    document.querySelectorAll('[data-close-modal]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const modal = e.target.closest('.modal-backdrop');
        if (modal) closeModal(modal);
      });
    });

    // Close on backdrop click
    document.querySelectorAll('.modal-backdrop').forEach(modal => {
      modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal(modal);
      });
    });
  }

  // --- Modal Helpers ---

  function openModal(modal) {
    modal.classList.add('open');
    document.body.style.overflow = 'hidden';
  }

  function closeModal(modal) {
    modal.classList.remove('open');
    document.body.style.overflow = '';
  }

  // --- Global Window Hooks for inline table clicks ---
  window.appOpenCaseDetail = openCaseDetailModal;
  window.appOpenInstitution = openInstitutionModal;
  window.appOpenOverlap = openOverlapReviewModal;

  // --- Utility Helpers ---

  function getBureauTagClass(bureau) {
    if (!bureau) return 'tag-pgrc';
    const b = bureau.toUpperCase();
    if (b.includes('PGRC')) return 'tag-pgrc';
    if (b.includes('REGULATION')) return 'tag-regulation';
    if (b.includes('APPROVAL')) return 'tag-approval';
    if (b.includes('SCSC')) return 'tag-scsc';
    if (b.includes('LEGAL')) return 'tag-legal';
    return 'tag-pgrc';
  }

  function getStatusClass(status) {
    if (!status) return 'status-active';
    const s = status.toUpperCase();
    if (s.includes('JOINT')) return 'status-joint';
    if (s.includes('REVIEW')) return 'status-review';
    if (s.includes('RESOLVED') || s.includes('CLOSED')) return 'status-resolved';
    return 'status-active';
  }

  function getPriorityClass(p) {
    if (!p) return 'priority-medium';
    const s = p.toUpperCase();
    if (s.includes('URGENT')) return 'priority-urgent';
    if (s.includes('HIGH')) return 'priority-high';
    return 'priority-medium';
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'warning') icon = '⚠️';
    if (type === 'error') icon = '❌';

    toast.innerHTML = `<span>${icon}</span> <span>${escapeHtml(message)}</span>`;
    toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4500);
  }

  // Start app
  init();
});
