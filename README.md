# 📘 Nephro Platform – Distributed Clinical Decision System

## ⚠️ Important Context

Nephro is a **large-scale microservices-based medical decision support platform**.

This repository represents **only a subset of the full system (1 of 10 core modules)**.

Each microservice is:
- independently developed
- independently deployable
- responsible for a specific domain

👉 The full application is composed of **10 interconnected modules**, each owned by different contributors and teams.

This repository focuses on the **clinical analysis + prescription intelligence layer**, which integrates into the broader ecosystem via the API Gateway.

---

## 🧠 System Vision

Nephro transforms raw medical data into **structured clinical intelligence**, enabling:

- automated anomaly detection
- decision validation
- AI-assisted explanations
- time-based clinical tracking
- cross-module verification

The system follows a strict pipeline:

> Data → Analysis → Validation → Decision → Action → Monitoring

---

## 🏗️ Architecture Overview

### Core Infrastructure

- Frontend: Angular dashboard (clinical interface)
- API Gateway: Single entry point for all requests
- Eureka Server: Service discovery and registration
- Microservices: Domain-specific clinical modules
- Kubernetes + Docker: Deployment & orchestration layer

---

## 🧩 This Module (Current Repository Scope)

This module is responsible for:

---

### 🧪 1. Lab Results Intelligence Engine

- Processing biological reports
- Detecting anomalies in patient results
- Assigning severity levels:
  - NORMAL
  - FOLLOW_UP
  - URGENT
- Generating structured clinical insights

---

### 📊 2. Cross-Module Validation System

- Validates prescriptions against latest lab results
- Communicates through API Gateway
- Returns structured decision status:
  - OK
  - WARNING
  - ERROR

👉 Ensures **inter-service medical consistency**

---

### 🧠 3. AI Clinical Assistant (Ollama – Gemma Model)

- Generates explanations for prescriptions
- Assists clinicians with decision interpretation
- Provides contextual reasoning based on patient data

**Endpoint:**

GET /prescriptions/{id}/ai-explanation


---

### 📅 4. Smart Clinical Calendar System

The calendar is a **decision-driven scheduling engine**, not just a UI component.

#### Features:
- Auto-generates events from lab reports
- Automatically schedules follow-ups based on severity

#### Logic:
- NORMAL → +30 days
- FOLLOW_UP → +7 days
- URGENT → +2 days

#### Event Types:
- REPORT event (exact report date)
- FOLLOW-UP event (computed critical scheduling)

#### UI Behavior:
- Monthly grid display
- Navigation between periods
- Event click → detailed report view

#### Visual Encoding:
- 🔵 Blue → REPORT
- 🟢 Green → NORMAL
- 🟡 Yellow → FOLLOW_UP
- 🔴 Red → URGENT (with alert emphasis)

---

### 🖱️ 5. Digital Mouse Pad Signature System

A secure clinical authentication mechanism enabling:

- handwritten-style digital signature capture
- physician approval validation
- tamper-resistant signing flow

**Purpose:**
Ensures that prescriptions and decisions are clinically authorized and traceable

---

### 🔒 6. Auto-Lock Security Layer

Security mechanism that:

- locks sensitive clinical actions after inactivity
- prevents unauthorized modifications
- forces re-authentication for critical operations

**Applied to:**
- prescriptions
- validation steps
- sensitive patient data actions

---

### 🚨 7. Cross-Module Alert System

A real-time clinical safety layer that:

- detects abnormal lab results
- triggers alerts across services
- propagates risk signals to other modules

#### Alert Levels:
- INFO
- WARNING
- CRITICAL

👉 Ensures **system-wide awareness of patient risk states**

---

### 📈 8. Clinical Statistics Engine

Provides aggregated insights:

- patient trends over time
- anomaly frequency tracking
- prescription effectiveness signals
- severity distribution analytics

**Used for:**
- clinical dashboards
- decision support
- long-term monitoring

---

## 🧠 UX / Clinical Design Principles

The system is built around:

- clarity of medical priority
- reduction of cognitive overload
- fast identification of critical cases
- decision traceability
- time-aware medical action planning

---

## ⚙️ Key Endpoints

### Calendar

GET /lab-reports/calendar


### AI Assistant

GET /prescriptions/{id}/ai-explanation


### Validation

GET /prescriptions/{id}/validation


---

## 🚧 Current Limitations (Module Scope)

- No global patient aggregation layer
- Limited cross-module visualization
- No predictive risk model (future AI upgrade)
- No unified multi-module dashboard
- No real-time streaming UI updates
