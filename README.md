# 🧾 InvoiceGuard – Vendor Invoice Management Backend

InvoiceGuard is a **Spring Boot–based backend system** designed to manage vendor invoices with **real-world business rules**, **controlled approval workflows**, and **full audit traceability**.

This project focuses on **how enterprise backends actually work** — enforcing state transitions, preventing duplicates, validating vendors, and supporting scalable data access — not just exposing CRUD APIs.

---

## 📌 Project Highlights

- Clean **layered architecture** (Controller → Service → Repository)
- Real-world **invoice lifecycle state machine**
- **Vendor validation** and duplicate invoice prevention
- **Audit logging** for every invoice action
- **Pagination-enabled search APIs** for scalability
- DTO-driven API contracts (no entity leakage)
- Business-rule enforcement at service & database levels

---

## 🧱 Architecture Overview

controller/   → REST endpoints (API layer)  
service/      → Business rules & workflow logic  
repository/   → JPA repositories (data access)  
model/        → Domain entities & enums  
dto/          → API request/response contracts  

### Why this architecture?
- Clear separation of concerns  
- Easy to maintain and extend  
- Matches real-world enterprise backend design  
- Business logic stays independent of controllers  

---

## 🧑‍💼 Core Domains

### 🏢 Vendor
- Represents a business entity submitting invoices
- Can be **ACTIVE** or **BLOCKED**
- Blocked vendors cannot submit invoices

### 🧾 Invoice
- Belongs to a vendor
- Has amount, invoice number, billing details, and dates
- Follows a strict lifecycle

### 🕵️ InvoiceAudit
- Captures **every state change**
- Stores previous status, new status, action type, and timestamp
- Enables full traceability and compliance

---

## 🔄 Invoice Lifecycle (State Machine)

CREATED → SUBMITTED → APPROVED / REJECTED → PAID

### Key Rules
- Only CREATED invoices can be submitted
- Only SUBMITTED invoices can be approved or rejected
- Only APPROVED invoices can be paid
- Invalid transitions are blocked at service level

---

## ⚙️ Business Rules Enforced

- Vendor must exist and be ACTIVE  
- Duplicate invoices are rejected  
- Amount-based auto-approval for low-value invoices  
- Strict validation before every state change  
- All actions generate an audit record  

---

## 🔍 Search & Pagination

Supports scalable invoice retrieval using Spring Data pagination.

Search by:
- Status
- Vendor
- Invoice date range

Example:
GET /api/invoices/search?status=APPROVED&page=0&size=10

---

## 📑 REST API Overview

### Invoice Actions
- POST /api/invoices/create
- POST /api/invoices/{id}/submit
- POST /api/invoices/{id}/approve
- POST /api/invoices/{id}/reject
- POST /api/invoices/{id}/pay

### Audit Logs
- GET /api/invoices/{id}/audit

### Search
- GET /api/invoices/search

---

## 📘 API Documentation (Swagger / OpenAPI)

InvoiceGuard provides **interactive API documentation** using **Swagger UI**.

### Access Swagger UI
http://localhost:8080/swagger-ui/index.html

### OpenAPI Spec
http://localhost:8080/v3/api-docs

### What Swagger Covers
- All invoice lifecycle endpoints
- Request & response DTO schemas
- Enum-based fields
- Pagination parameters

### Dependency Used
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x.x'

---

## 🛠️ Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Swagger / OpenAPI

---

## 🚀 Running the Project

1. Clone the repository
2. Run using:
./gradlew bootRun
3. Access APIs at:
http://localhost:8080/api/invoices

---

## ⭐ Final Note

InvoiceGuard demonstrates **production-style backend design**, focusing on business workflows, scalability, and traceability rather than simple CRUD operations.

If you find this project helpful, consider starring ⭐ the repository.
