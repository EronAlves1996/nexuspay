# 💳 NexusPay - Cloud-Native Wallet & Transaction Engine

## 🎯 Project Overview
**NexusPay** is a cloud-native, distributed wallet and payment processing engine. It simulates the core backend of a modern fintech application (like Nubank, Revolut, or Stripe). 

This project is a **learning scaffold** designed to accompany a rigorous reading trail from legacy monolith development to modern Cloud-Native/Senior Backend Architecture. The system starts as a basic monolith and evolves iteratively into a distributed, event-driven, highly secure, and observable microservices ecosystem deployed on Kubernetes.

## 🧠 Business Requirements (The "What")
The system must support the following business capabilities:
1. **User Onboarding:** Users can register and open a digital wallet.
2. **Funds Management:** Users can deposit and withdraw funds.
3. **Peer-to-Peer Transfers:** Users can transfer money to other users instantly.
4. **Transaction History:** Users can search their transaction history with complex filters.
5. **Fraud Detection (AI):** Suspicious transactions must be flagged for review automatically.
6. **Audit & Compliance:** Every state change must be audit-logged and secure.

## 🏗️ Target Technical Architecture (The "How")
*This represents the final state of the project after the reading trail is complete.*

*   **Architecture:** Event-Driven Microservices (Saga Pattern for distributed transactions).
*   **Backend:** Java 26 + Spring Boot 4.
*   **Database:** PostgreSQL (per service).
*   **Messaging:** Apache Kafka (Event Bus & Outbox Pattern).
*   **Search:** Elasticsearch (CQRS Read-Model).
*   **Security:** OAuth 2.0 / OIDC (Keycloak).
*   **Observability:** OpenTelemetry (Traces, Metrics, Logs) -> Grafana/Jaeger.
*   **Infrastructure:** Docker, Kubernetes (AWS EKS), Nginx Ingress.
*   **AI/LLM:** Integration with LLM APIs for fraud detection explanations and transaction categorization.

## 📝 Current System State & Changelog

*   **[Initial State]** - Project is an empty directory. No code exists yet. The developer is using Java 26 and Spring Boot 4.
*   **[Phase 1: Init]** - Project skeleton created.
    *   Infrastructure: Docker Compose configured with PostgreSQL. JaCoCo plugin added for test coverage.
    *   *Pending:* Extract configuration into environment variables.
*   **[Phase 2: User Onboarding & DB Migrations]** - Implemented the foundational User CRUD module.
    *   **Features:** RESTful endpoints for user creation, retrieval (by ID/Email), update, and soft-delete. Jakarta Validation applied to request DTOs.
    *   **Database:** Integrated Flyway for schema versioning. Created `V1__create_user_table.sql` with `deleted` column for Hibernate soft-delete support. Enforced `ddl-auto: validate` to ensure code-database parity.
    *   **Security & Configuration:** Extracted database credentials from `application.yaml` into environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`). Integrated `spring-dotenv` (optional, dev-only scope) to load `.env` files locally. Added `.env` to `.gitignore` and provided `.env.example` for onboarding.
    *   **Code Quality & Fixes:** 
        *   Standardized UUID handling in controllers (leveraging Spring's auto-conversion).
        *   Ensured proper `@Transactional` boundaries for read-then-write operations (Create/Update).
        *   Standardized timezone handling to prevent cloud-environment inconsistencies.
        *   Removed conflicting JPA schema-generation configurations in favor of pure Flyway.
    *   **Testing:** Added unit tests for `UserService` covering duplicate email constraints and not-found scenarios. Added helper shell scripts (`requests/user/`) for manual API testing.
    *   *Pending:* Add `@WebMvcTest` for Controller layer. Implement a Global Exception Handler (`@ControllerAdvice`).
