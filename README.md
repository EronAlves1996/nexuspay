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
*   **Backend:** Java 21 + Spring Boot 3.
*   **Database:** PostgreSQL (per service), DB2 is strictly forbidden.
*   **Messaging:** Apache Kafka (Event Bus & Outbox Pattern).
*   **Search:** Elasticsearch (Read-model for transaction history).
*   **Security:** OAuth 2.0 / OIDC (Keycloak), Secure-by-Design domain models.
*   **Observability:** OpenTelemetry (Traces, Metrics, Logs) -> Grafana/Jaeger.
*   **Infrastructure:** Docker, Kubernetes (AWS EKS), Nginx Ingress.
*   **AI/LLM:** Integration with LLM APIs for fraud detection explanations and transaction categorization.

---

## 🤖 AI & LLM Integration Instructions (READ THIS FIRST)

This README is a **living document** designed to be parsed by an LLM (like ChatGPT or Claude) to generate hands-on coding tasks.

**How to use this README with an LLM:**
1. Copy this entire README.
2. Paste it into your LLM prompt along with the book/chapter you just read.
3. Use the following prompt template:

> "I am reading [Book Name], specifically the chapter about [Topic/Concept]. Based on my current project progress in the README below, generate a practical coding task to apply this concept. The task must include: Acceptance Criteria, Implementation Steps, and a Testing Strategy. Do not give me the code; guide me to write it."

---

## 📈 Learning Trail & Progress Tracker

*(The LLM and User will update this section as features are completed and books are read).*

### Phase 1: Modern Code & Architecture
- [ ] **Unit Testing Principles:** Core `Wallet` and `Transaction` domain models are built using TDD. Tests are isolated, fast, and test behaviors, not implementations.
- [ ] **Building Microservices:** Monolith split into `Identity Service`, `Wallet Service`, and `Transaction Service`. 
- [ ] **Microservices Patterns:** Implemented API Gateway pattern and Saga Pattern for P2P transfers (ensuring atomicity without distributed locking).

### Phase 2: Infrastructure & Deployment
- [ ] **The DevOps Handbook:** CI/CD pipeline created via GitHub Actions. Automated testing and deployment.
- [ ] **AWS for Solutions Architects:** Infrastructure provisioned using VPC, Subnets, RDS (PostgreSQL), and EC2.
- [ ] **The Nginx Handbook:** Nginx configured as a reverse proxy/rate-limiter in front of the services.
- [ ] **The Kubernetes Book:** All services containerized with Docker and orchestrated via Kubernetes (Minikube/EKS).

### Phase 3: Data & Observability
- [ ] **Kafka:** Asynchronous event-driven communication established. Transactional Outbox pattern implemented for reliable publishing.
- [ ] **Mastering OpenTelemetry:** Distributed tracing implemented across all services. Metrics and logs correlated with Trace IDs.
- [ ] **Elasticsearch:** Transaction querying offloaded to Elasticsearch. CQRS (Command Query Responsibility Segregation) implemented.

### Phase 4: Security
- [ ] **Secure by Design:** Domain models hardened against invalid states. No primitive obsession in financial calculations (using `BigDecimal` value objects).
- [ ] **Mastering OAuth 2.0:** Keycloak integrated. Services validate JWTs. Machine-to-machine communication uses Client Credentials flow.
- [ ] **Hacking APIs:** Performed threat modeling. Implemented rate limiting, input sanitization, and prevented BOLA (Broken Object Level Authorization).

### Phase 5: The Frontier
- [ ] **AI Engineering:** LLM integrated to categorize transactions (e.g., "Food", "Transport") based on description.
- [ ] **The LLM Engineering Handbook:** RAG (Retrieval-Augmented Generation) implemented so users can "chat" with their transaction history.

---

## 📝 Current System State & Changelog

*   **[Initial State]** - Project is an empty directory. No code exists yet. The developer is using Java 26 and Spring Boot 4.

