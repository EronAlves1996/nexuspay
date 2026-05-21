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
    *   **Testing:** Added unit tests for `UserService` covering duplicate email constraints and not-found scenarios. Adopted `sut` (System Under Test) naming convention for test fields to clearly distinguish the tested component from mocks. Added helper shell scripts (`requests/user/`) for manual API testing.
    *   **Coverage Policy:** Configured JaCoCo to enforce a minimum 65% line coverage and 80% branch coverage on the service layer (controllers, repositories, and the main application class excluded). Exclusions will be refined as test layers expand.
    *   *Pending:* Add `@WebMvcTest` for Controller layer (will remove controller exclusion from JaCoCo once complete). Implement a Global Exception Handler (`@ControllerAdvice`).
*   **[Phase 2.1: Continuous Integration Setup]** - Added basic CI pipeline.
    *   **Features:** GitHub Actions workflow (`run-build.yml`) triggered on Pull Requests. Automated Maven build and JaCoCo coverage threshold validation on every PR. Configured with JDK 26 (Temurin) and Maven caching.
    *   **Cleanup:** Removed default `NexuspayApplicationTests` context load test to allow CI to pass without requiring external database dependencies at this stage.
    *   **Artifact Publishing (Completed):** Added steps to generate the JaCoCo HTML report (`jacoco:report`) and upload both JUnit test reports and JaCoCo coverage reports as workflow artifacts. Retention period set to 2 days to avoid clutter.
    *   **Known Issues & Technical Debt:** 
        *   Because the context load test was removed and no database environment variables are injected into the workflow, CI currently only validates compilation and unit tests. It will **not** catch broken Flyway migrations, missing configurations, or Spring bean wiring issues.
    *   *Pending:* Integrate Testcontainers to spin up a PostgreSQL instance during CI runs, and restore the `@SpringBootTest` to validate application context and migrations.
*   **[Phase 3: Wallet Creation & Infrastructure Refactoring]** - Implemented Wallet module and extracted shared JPA abstractions.
    *   **Features:** RESTful endpoint for wallet creation, bound to an existing user. Enforced unique wallet names per user at both application and database levels.
    *   **Infrastructure Refactoring:** Extracted `SensitiveEntity` as a `@MappedSuperclass` containing `id`, `createdAt`, and `updatedAt` fields. Refactored `User` entity to extend it. Extracted `BaseController` with a helper method for consistent `201 Created` responses with `Location` header.
    *   **Database:** Created `V2__create_wallet_table.sql` with soft-delete support, foreign key to `app_user`, and composite unique constraint on `(name, user_id)`.
    *   **Data Integrity:** Configured `@ManyToOne(fetch = FetchType.LAZY)` with `@NotFound(action = EXCEPTION)` on the `user` relation to prevent orphaned wallets. Separated the join column mapping from the actual `user_id` field to avoid conflicts with Hibernate's column insertion/updating logic.
    *   **Validation & Error Handling:** Applied `@NotNull` and `@Size` constraints on the `CreateWalletDto` record. Corrected validation order in `WalletService` (user existence is now checked before wallet name uniqueness to provide accurate error messages). Added `WalletAlreadyExistsException` (`409 Conflict`) and `UserDoesntExistsException` (`400 Bad Request`).
    *   **Wallet Retrieval Endpoint:** Added `GET /wallets/{id}` returning wallet details (excluding balance for now). Uses `RetrieveWalletDto` as an inner record to encapsulate the response. The `@SoftDelete` annotation on `Wallet` ensures soft‑deleted wallets are automatically excluded from queries.
    *   **Testing:** Added unit tests for `WalletService` covering successful creation, duplicate name rejection, and non-existent user rejection. Added manual test script (`requests/wallet/create.sh` and `requests/wallet/getById.sh`).
    *   **Testing Conventions:** Standardized AAA (Arrange-Act-Assert) structure across all unit tests using blank line separation. Within each phase, related setup statements are grouped together; a blank line precedes the Act call and another separates the Assert phase. Complex tests may use explicit `// --- Arrange ---` comment blocks for clarity. Test method names use descriptive, sentence-like snake_case (e.g., `user_with_same_email_as_other_user_is_prohibited`) to tell the story of the scenario and expected outcome, prioritizing readability over rigid naming templates.
    *   *Pending:* Add `@Transactional(readOnly = true)` to `WalletService.findById` to optimise read operations and signal intent. Implement Wallet retrieval by user (list wallets for a given user).
*   **[Phase 3.1: Soft-Delete Index Optimization]** - Improved unique constraints to work correctly with soft-delete.
    *   **Problem:** The original `UNIQUE` constraints on `app_user(email)` and `wallet(name, user_id)` prevented creating new active records with the same value as a soft-deleted one. This made soft-delete less practical.
    *   **Solution:** Replaced the full-table unique constraints with **partial unique indexes** that only enforce uniqueness on active rows (`WHERE deleted = false`).  
    *   **Database Migration:** Created `V3__fix_table_indexes_to_consider_deleted_data.sql`.  
    *   **Result:** Duplicate emails or wallet names are now allowed among soft‑deleted records, while active records remain strictly unique. This aligns perfectly with Hibernate’s `@SoftDelete` behaviour.
    *   **Status:** Completed and merged.
*   **[Phase 3.2: Wallet Update Endpoint]** - Implemented `PUT /wallets/{id}` to modify an existing wallet.
    *   **Features:** Updates wallet `name` and `minLimit` (must be ≤ 0, enforced by `@NegativeOrZero`). The `userId` in the request body must match the current owner; transferring a wallet to another user is forbidden and returns `403 Forbidden`.
    *   **Validation & Error Handling:** Added proper exceptions and HTTP status codes.
    *   **Entity Validation:** Added Jakarta Bean Validation constraints directly to the `Wallet` entity: `@NotNull` and `@NegativeOrZero` on `minLimit`, `@NotNull` and `@Size(max=255)` on `name`, and `@NotNull` on `userId`. These constraints are automatically enforced by Hibernate during persistence lifecycle events (e.g., `PreUpdate`), ensuring data integrity before any database operation.
    *   **Testing:** Unit tests cover happy path, not found, user not found, duplicate name, and cross‑user transfer attempts.
    *   **Manual Testing:** Added `requests/wallet/update.sh` script.
*   **[Phase 3.3: Service Layer CQS Refactoring]** - Refactored `UserService` and `WalletService` to follow Command-Query Separation (CQS) principles.
    *   **Changes:** Write methods return `void`, throw `NotFoundException` instead of returning empty `Optional`, controllers adjusted.
    *   **Exception Handling:** Annotated exceptions with `@ResponseStatus`; stack traces omitted via configuration.
    *   **Testing:** Unit tests updated to expect exceptions or verify side effects.
    *   **Impact:** Cleaner separation of commands and queries, more explicit error signalling.
*   **[Phase 4: Transaction Domain Model (Double-Entry Accounting)]** - **COMPLETED**  
    Designed and implemented the core transaction engine based on a double-entry bookkeeping model.
    *   **Concepts:** A transaction is a zero-sum, unidirectional resource transfer between two players. Every transfer incurs a debit (reduction) on the source and a credit (addition) on the target, modelled using double-entry accounting.
    *   **Database Changes (Migration `V4__create_transaction_table.sql`):**
        *   Created PostgreSQL enum `transaction_operation_type` (`'CREDIT'`, `'DEBIT'`).
        *   Created `app_transaction` table with `BIGSERIAL` primary key, `wallet_id` (foreign key to `wallet`), `created_at` (timestamp with time zone), `operation`, `description`, and `amount` with `CHECK (amount > 0)`.
        *   **Note:** The index on `wallet_id` is planned as a separate migration (tracked via GitHub issue) to keep the current change focused.
    *   **Application Entities:**
        *   `Transaction.java` – immutable entity (`@Immutable`) with `@Builder`, protected no-arg constructor, and proper mapping for the PostgreSQL enum (`columnDefinition = "transaction_operation_type"`).
        *   `TransactionOperation.java` – simple enum.
    *   **Pending Implementation (next phases):**
        *   `TransactionService` with `transfer(sourceWalletId, targetWalletId, amount, description)` method, including pessimistic locking for the source wallet, balance validation, and insertion of both DEBIT and CREDIT rows.
        *   Materialized balance column (`balance`, `last_processed_transaction_id`) on `Wallet` table (separate migration).
        *   Nightly compensation and balance materialisation jobs.
        *   Unit and integration tests covering concurrent transfers, insufficient funds, etc.
    *   **Technical Debt / Future Issues:**
        *   Missing `transfer_id` column to group paired DEBIT/CREDIT rows – left for later phase (GitHub issue created).
        *   Index on `app_transaction(wallet_id)` will be added in a follow‑up migration for performance.
    *   **Design Documentation:** The detailed design is captured in `docs/transaction-domain-model.md`.
    *   **Status:** Core entity and schema are merged. Service layer implementation is the next deliverable.
*   **[Phase 5: Account Classification (Chart of Accounts)]** - **COMPLETED**  
    Added reference data to support double‑entry accounting: a chart of accounts that classifies the nature of each balance (ACTIVE vs PASSIVE). This is a foundational building block for the transaction engine, enabling proper accounting rules and financial statement generation.
    *   **Features:** Lookup entity `AccountClassification` with `description` and `balanceSide`. Initial seed data: Demand Deposits (ACTIVE), Pending Transfers (ACTIVE), User Deposits (PASSIVE).
    *   **Database Migration (`V5__create_account_classification_table.sql`):**
        *   Created PostgreSQL enum `balance_side_enum` (`'ACTIVE'`, `'PASSIVE'`).
        *   Created `account_classification` table (`bigserial` PK, `balance_side`, `description`).
        *   Seeded initial classification rows.
    *   **Application Entity:** `AccountClassification.java` – immutable reference entity (`@Immutable`), Jakarta validation (`@NotNull`, `@Size(max=255)`), builder pattern.
    *   **Design Notes:** This entity does **not** extend `SensitiveEntity` because it is static reference data that never changes and does not require audit timestamps.
    *   **Testing:** Not unit‑tested (pure entity + migration); passes `mvn verify` and will be covered by future integration tests.
    *   **Status:** Merged. The system now has a minimal chart of accounts to support the upcoming transaction service.

## 🔜 Upcoming Phases (Preview)
*   **Phase 6:** Transaction Service & Balance Management – materialized balance, `SELECT FOR UPDATE`, and the `transfer` API.
*   **Phase 7:** Scheduled Jobs – nightly compensation and balance materialisation.
*   **Phase 8:** Transaction History & Search (CQRS with Elasticsearch).
*   **Phase 9:** Fraud Detection integration (LLM).
*   **Phase 10:** Migration to event‑driven microservices (Kafka, Outbox, Saga).

---

*Last updated after Phase 3.2 validation improvements.*
