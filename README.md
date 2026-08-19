# Enterprise Notification & Alerting Engine

A high-throughput, fault-tolerant, and universal Full-Stack platform designed to centralize and route Email and SMS notifications across multiple enterprise microservices (e.g., Fintech, E-commerce).

This project demonstrates the seamless integration of a reactive frontend built with **Angular** and a highly concurrent backend architecture powered by **Java 21 & Spring Boot 4.x**.

---

## 🚀 Key Architectural Features

* **Universal API Routing:** Single REST endpoint (`/api/v1/notifications/send`) capable of processing payloads for multiple channels (Email/SMS).
* **Asynchronous Execution (`@Async`):** Notification delivery is decoupled from the main thread using a custom `ThreadPoolTaskExecutor` to maximize application throughput and eliminate user-blocking requests.
* **Resilience & Fault Tolerance:**
    * **Exponential Backoff Retry:** Intelligently retries failed third-party gateway connections with increasing intervals.
    * **Failover/Fallback Routing:** Automatically reroutes critical alerts via SMS if the primary Email server experiences downtime.
* **Enterprise Rate Limiting:** Implements highly optimized in-memory rate limiting to defend against API abuse and spamming (e.g., maximum 3 OTPs per minute per user).
* **Global Exception Handling:** Centralized error management utilizing `@RestControllerAdvice` ensuring zero system crashes and standardized API responses.
* **Angular Admin Dashboard:** A robust management portal leveraging **RxJS Observables** and HTTP Interceptors for secure, real-time monitoring of delivery success/failure logs and dynamic rate limit configurations.

---

## 🛠️ Tech Stack & Dependencies

### Backend
* **Language & Framework:** Java 21, Spring Boot 4.x
* **Security:** Spring Security, JWT (JSON Web Tokens)
* **Data Access:** Spring Data JPA, Hibernate
* **Database:** MySQL / PostgreSQL
* **Validation:** Jakarta Validation 3.0

### Frontend
* **Framework:** Angular 16+ (TypeScript)
* **Reactive Programming:** RxJS Observables
* **UI Components:** Angular Material / Bootstrap

---

## 📂 Project Structure

```text
centralized-notification-system/
│
├── notification-backend/            # Spring Boot 4.x Application
│   ├── src/main/java/com/project/
│   │   ├── controllers/            # REST Endpoints
│   │   ├── services/               # Core Logic, Async Threads, & Rate Limiting
│   │   ├── models/                 # JPA Entities
│   │   └── dtos/                   # Secure Data Transfer Objects
│   └── pom.xml
│
└── notification-frontend/           # Angular Application
    ├── src/app/
    │   ├── components/             # Dashboard Layouts & Analytic Logs
    │   └── services/               # Reactive HTTP Services with Interceptors
    └── package.json
```

---

## 🔒 Security & Best Practices
* **DTO Pattern:** Real database entities are strictly encapsulated, preventing direct leakage to the client interface.
* **Environment Protection:** Production variables and database passwords are managed securely using untracked external configurations (`application.yml` is explicitly ignored in git).
