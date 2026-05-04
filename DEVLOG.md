## 🤖 AI & LLM Integration Instructions (READ THIS FIRST)

This DEVLOG is a **living document** designed to be parsed by an LLM (like ChatGPT or Claude) to track tasks.

---

## 📈 Learning Trail & Progress Tracker

*(The LLM and User will update this section as features are completed and books are read).*

### Phase 1: Modern Code & Architecture
- [ ] **Unit Testing Principles:** 
  - [ ] Core `Wallet` and `Transaction` domain models are built using TDD. Tests are isolated, fast, and test behaviors, not implementations.
  - [x] Configure code coverage tool
  - [ ] Extract configuration into environment variables
  - [ ] Configure code coverage to not be below 65%
  - [ ] Configure CI/CD to run always after pushing a branch
  - [ ] Lock main for merging if CI/CD dont pass
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

