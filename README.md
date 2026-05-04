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

## 📝 Current System State & Changelog

*   **[Initial State]** - Project is an empty directory. No code exists yet. The developer is using Java 26 and Spring Boot 4.

