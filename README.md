# NIVARANFLOW (SIH 2026 - Problem Statement: SIH26264)
### Integrated Dashboard for Monitoring and Tracking Institution-Related Complaint Cases Across AICTE Bureaus/Cells

**Team**: DISASTER DECODED (Team ID: 198035)  
**Theme**: Smart Automation | **Category**: Software  
**Official References**: AICTE Approval Process Handbook (Section 7.10 "Complaint Cases"), sih.gov.in/sih2026PS

---

## 🎯 Executive Summary & Problem Context

One institution can have complaint cases in more than one AICTE Bureau or Cell (e.g., **PGRC** - Public Grievance Redressal Cell, **Regulation Bureau**, **Approval Bureau**, **SCSC** - Standing Complaint Scrutiny Committee, and **Legal Cell**).

### The Problem
When cases are managed in separate office views, staff may not see the full history, current owner, or next action for the same institution. This leads to:
- **Repeated work & duplicated notices**
- **Slow handoffs between bureaus**
- **Conflicting parallel actions or legal issues**

### The NIVARANFLOW Solution
A shared institution profile, anchored by the **AICTE Permanent ID**, brings all related cases into **one unified view**. 
1. **Unified Dossier**: Staff can see each case owner, status, action history, and next step.
2. **Smart Overlap Radar**: An automated alert flags when the same institution has active cases across multiple Bureaus/Cells.
3. **Human-in-the-Loop Confirmation**: *No automatic case linking — every match remains a human decision.* An authorized official reviews the evidence and records whether to link the cases for joint proceedings or keep them separate with rationale.
4. **Transparent Audit Trail**: Immutable log of all cross-bureau decisions for regulatory compliance.

---

## 🏗️ 5-Step Technical Approach (From Slide 3)

1. **Record the Case**: Enter AICTE Permanent ID, case ID, complaint type, office/bureau, owner, and current status. Real-time pre-check warns the user if active cases already exist in other bureaus.
2. **Find Related Records**: Universal instant search across the shared case database by institution name, AICTE Permanent ID (e.g. `1-1092837461`), or Case ID.
3. **Flag a Possible Overlap**: Prominent radar banner & alert badge whenever an institution has concurrent cases across bureaus.
4. **Review the Evidence**: Side-by-side comparison workspace showing dates, allegations, bureau ownership, and current actions.
5. **Record the Decision**: Official decides whether to **Link Cases (Joint Proceeding)** or **Keep Separate (Independent Actions)**, recording why, designated lead owner, and agreed next steps.

---

## 🌟 Seeded Fictional Cases (From Slide 1 & Slide 2)

- **Institution A (`1-1092837461`) - Apex Institute of Engineering & Technology**:
  - **PGRC (Case `P-101`)**: Faculty salary delay & contract grievance.
  - **Regulation Bureau (Case `R-204`)**: Faculty cadre deficit and ratio non-compliance.
  - **Alert `OA-101`**: Ready for instant review and human linking demonstration!
- **National Institute of Advanced Science & Technology (`1-4829104712`)**:
  - Approval Bureau (`A-312`) + SCSC (`S-504`) - Pre-linked for combined EVC hearing.
- **Kaveri Institute of Technology (`1-7738291045`)**:
  - Legal Cell (`L-089`) + PGRC (`P-142`) - Kept separate (sub judice in High Court).

---

## 💻 Tech Stack

- **Backend**: **Java 25 LTS**
  - Built-in `com.sun.net.httpserver.HttpServer` with Virtual Threads (`newVirtualThreadPerTaskExecutor`)
  - RESTful API endpoints with full CORS support
  - Zero external dependency requirements (runs instantly with standard JDK)
- **Frontend**:
  - HTML5 & CSS3 with Glassmorphism, tailored Indian tricolor accents, and sleek dark mode
  - Modern Typography via Google Fonts (*Inter* & *Outfit*)
  - Interactive side-by-side comparison workspace, modals, and real-time filters
- **Standard Build Configuration**: Includes `pom.xml` for standard Maven toolchains.

---

## 🚀 Quick Start Guide

### Prerequisites
- Java 21+ or Java 25 installed (`java --version`)

### Running the Application

Simply execute the start script:
```bash
./start.sh
```

Or manually:
```bash
# 1. Compile Java sources
mkdir -p bin
javac -d bin $(find backend/src -name "*.java")

# 2. Run the application
java -cp bin com.disasterdecoded.nivaranflow.Main 8080
```

### Accessing the System
- **Web Dashboard**: Open your browser to [http://localhost:8080](http://localhost:8080)
- **REST API Base**: [http://localhost:8080/api](http://localhost:8080/api)

---

## 📡 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/cases` | Filter cases by `bureau`, `status`, `priority`, or `query` |
| `GET` | `/api/cases/{id}` | Retrieve case details, owner, deadline, and action history |
| `POST` | `/api/cases` | Register a new complaint case (triggers automatic overlap check) |
| `POST` | `/api/cases/{id}/actions` | Add a new official action/directive to case timeline |
| `GET` | `/api/overlaps` | List all cross-bureau overlap alerts |
| `GET` | `/api/overlaps/{id}` | Get overlap alert with side-by-side comparison of conflicting cases |
| `POST` | `/api/overlaps/{id}/decision` | Record human decision (`LINKED` or `KEPT_SEPARATE`) with rationale |
| `GET` | `/api/institutions` | List registered institutions |
| `GET` | `/api/institutions/{id}` | 360° unified dossier of institution across all AICTE bureaus |
| `GET` | `/api/stats` | Aggregated dashboard metrics & bureau breakdown |
| `GET` | `/api/audit` | Regulatory audit and compliance log |

---

## 👥 Hackathon Credits
- **Hackathon**: Smart India Hackathon 2026
- **Team**: DISASTER DECODED (Team ID: 198035)
- **Problem Statement**: SIH26264
- **Solution**: NIVARANFLOW
