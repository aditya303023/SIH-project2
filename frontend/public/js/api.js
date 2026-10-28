/**
 * NIVARANFLOW - Frontend API Client
 * Interacts directly with the Java REST backend
 */

const API_BASE = '/api';

const api = {
  async getStats() {
    const res = await fetch(`${API_BASE}/stats`);
    if (!res.ok) throw new Error('Failed to load stats');
    return res.json();
  },

  async getCases(filters = {}) {
    const params = new URLSearchParams();
    if (filters.bureau && filters.bureau !== 'ALL') params.set('bureau', filters.bureau);
    if (filters.status && filters.status !== 'ALL') params.set('status', filters.status);
    if (filters.priority && filters.priority !== 'ALL') params.set('priority', filters.priority);
    if (filters.query) params.set('query', filters.query);

    const res = await fetch(`${API_BASE}/cases?${params.toString()}`);
    if (!res.ok) throw new Error('Failed to load cases');
    return res.json();
  },

  async getCaseById(caseId) {
    const res = await fetch(`${API_BASE}/cases/${encodeURIComponent(caseId)}`);
    if (!res.ok) throw new Error(`Case ${caseId} not found`);
    return res.json();
  },

  async createCase(caseData) {
    const res = await fetch(`${API_BASE}/cases`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(caseData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || 'Failed to create case');
    }
    return res.json();
  },

  async addCaseAction(caseId, actionData) {
    const res = await fetch(`${API_BASE}/cases/${encodeURIComponent(caseId)}/actions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(actionData)
    });
    if (!res.ok) throw new Error('Failed to add action');
    return res.json();
  },

  async getOverlaps() {
    const res = await fetch(`${API_BASE}/overlaps`);
    if (!res.ok) throw new Error('Failed to load overlaps');
    return res.json();
  },

  async getOverlapById(alertId) {
    const res = await fetch(`${API_BASE}/overlaps/${encodeURIComponent(alertId)}`);
    if (!res.ok) throw new Error(`Overlap alert ${alertId} not found`);
    return res.json();
  },

  async recordDecision(alertId, decisionPayload) {
    const res = await fetch(`${API_BASE}/overlaps/${encodeURIComponent(alertId)}/decision`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(decisionPayload)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || 'Failed to record decision');
    }
    return res.json();
  },

  async getInstitutions() {
    const res = await fetch(`${API_BASE}/institutions`);
    if (!res.ok) throw new Error('Failed to load institutions');
    return res.json();
  },

  async getInstitutionProfile(permanentId) {
    const res = await fetch(`${API_BASE}/institutions/${encodeURIComponent(permanentId)}`);
    if (!res.ok) throw new Error(`Institution ${permanentId} not found`);
    return res.json();
  },

  async getAuditLogs() {
    const res = await fetch(`${API_BASE}/audit`);
    if (!res.ok) throw new Error('Failed to load audit trail');
    return res.json();
  }
};
