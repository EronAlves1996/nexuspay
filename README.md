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
    *   **Testing:** Added unit tests for `WalletService` covering successful creation, duplicate name rejection, and non-existent user rejection. Added manual test script (`requests/wallet/create.sh`).
    *   **Testing Conventions:** Standardized AAA (Arrange-Act-Assert) structure across all unit tests using blank line separation. Within each phase, related setup statements are grouped together; a blank line precedes the Act call and another separates the Assert phase. Complex tests may use explicit `// --- Arrange ---` comment blocks for clarity. Test method names use descriptive, sentence-like snake_case (e.g., `user_with_same_email_as_other_user_is_prohibited`) to tell the story of the scenario and expected outcome, prioritizing readability over rigid naming templates.
    *   *Pending:* Implement a Global Exception Handler (`@ControllerAdvice`) to centralize error responses. Add `@WebMvcTest` for `WalletController`. Implement Wallet retrieval endpoints (by ID, by User). Implement Funds Management (deposit/withdraw).
*   **[Phase 4: Transaction Domain Model (Double-Entry Accounting)]** - Designed the core transaction engine based on a double-entry bookkeeping model.
    *   **Concepts:** A transaction is a zero-sum, unidirectional resource transfer between two players. Every transfer incurs a debit (reduction) on the source and a credit (addition) on the target. This is modelled using double-entry accounting, ensuring the total amount of money in the system remains invariant.
    *   **Database Changes (Pending Implementation):**
        *   **Wallet table additions:** Two new columns – `balance DECIMAL(10,2)` (materialized current balance) and `last_processed_transaction_id BIGINT` (marks the last transaction included in the materialized balance). The wallet also retains a `minimum_balance` constraint (can be negative for credit lines).
        *   **Transaction table (`V3__create_transaction_table.sql`):** Immutable audit log with columns:
            *   `id BIGSERIAL PRIMARY KEY`
            *   `wallet_id UUID` (the wallet affected by this entry – either source or target)
            *   `operation_timestamp TIMESTAMP`
            *   `operation ENUM('DEBIT', 'CREDIT')`
            *   `description VARCHAR(255)`
            *   `amount DECIMAL(10,2)`
        *   **Important:** Each logical transfer (e.g., P2P payment) produces **two** rows in the transaction table: one DEBIT for the source wallet and one CREDIT for the target wallet, inserted inside the same database transaction to preserve the zero-sum invariant.
    *   **Application Logic:**
        *   **Balance computation:** `current_balance = wallet.balance - sum(debits since last_processed_id) + sum(credits since last_processed_id)`.
        *   **Transaction execution (source side):**
            1. Lock the source wallet row using `SELECT ... FOR UPDATE`.
            2. Fetch all transactions with `id > wallet.last_processed_transaction_id` (no lock needed for reads).
            3. Calculate the true current balance.
            4. Verify that `current_balance - transfer_amount >= wallet.minimum_balance`. If not, abort.
            5. Insert the DEBIT row for the source and the CREDIT row for the target inside the same `@Transactional` method.
            6. *The target wallet is not locked* – its balance will be eventually consistent; credit operations never fail due to upper limits (no upper limits exist in this model).
        *   **Nightly compensations (external pseudo-accounts):**
            *   External players (e.g., National Treasure, Nexus Pay custody accounts) are represented as wallets owned by a special "system" user.
            *   Every night, the system fetches all such external wallets.
            *   For each wallet:
                *   If balance > 0 → debit the external wallet, credit the custody wallet (root user).
                *   If balance < 0 → credit the external wallet, debit the custody wallet.
            *   This ensures the custody wallet absorbs all residual imbalances, maintaining the zero-sum property across the entire system.
        *   **Nightly materialized balance update:**
            *   Runs after compensations.
            *   Iterates over all wallets, outside of any long-running transaction.
            *   For each wallet, starts a new transaction with `REPEATABLE READ` isolation, locks the wallet row (`SELECT FOR UPDATE`), aggregates all unprocessed transactions (where `id > last_processed_transaction_id`), updates `balance` and sets `last_processed_transaction_id` to the maximum `id` of the processed transactions.
            *   If a live transaction is concurrently holding the lock, the nightly update waits (lock acquisition order prevents deadlocks).
    *   **Pending Implementation Details:**
        *   Flyway migration scripts for `V3__create_transaction_table.sql` and the `ALTER TABLE wallet ADD COLUMN ...` statements.
        *   `Transaction` entity (JPA) and `TransactionRepository`.
        *   `TransactionService` with methods: `transfer(sourceWalletId, targetWalletId, amount, description)`.
        *   Unit and integration tests covering:
            *   Happy path (successful transfer).
            *   Insufficient funds (balance below minimum).
            *   Concurrent transfers from the same wallet (pessimistic locking prevents race conditions).
            *   Nightly compensation and balance materialization logic (scheduled jobs using `@Scheduled` or a cron‑based trigger).
        *   Global exception handler for transaction-specific exceptions (`InsufficientFundsException`, `WalletNotFoundException`).
        *   Manual test script `requests/transaction/transfer.sh`.
    *   **Design Documentation:** The detailed design is captured in `docs/transaction-domain-model.md`, which served as the blueprint for this phase.
    *   *Note on future evolution:* This monolith design intentionally uses row‑level locking for simplicity. When the project evolves to microservices, the transaction engine will be refactored into an event‑driven saga with Kafka and the outbox pattern, and idempotency keys will be introduced. Those changes are out of scope for the current phase.
