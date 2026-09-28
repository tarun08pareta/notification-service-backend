# Centralized Notification & Alerting Engine — Backend

The Centralized Notification & Alerting Engine Backend is a robust Spring Boot application serving as the orchestration core for multi-channel notification delivery. It acts as the single source of truth for all notifications, ensuring reliable, idempotent, and secure processing.

This backend handles:
- Authentication and authorization
- Notification creation and queuing
- Notification persistence and idempotency
- Provider routing and load-balancing abstraction
- Provider failover and retry processing
- Delivery attempt tracking
- Email delivery (with extendable SMS support)
- Email template management
- Company profile configurations
- API token authentication for programmatic access
- User and Admin dashboards
- Provider administration
- Observability and health monitoring

## 1. Overview

The backend exposes RESTful APIs consumed by the frontend platform and potentially third-party clients via API tokens. 

- **USER APIs:** Enable clients to send notifications, monitor their traffic, manage templates, API tokens, and configure sender identities.
- **ADMIN APIs:** Enable platform administrators to observe global metrics, manage upstream notification providers (e.g., Mailpit, SendGrid), and track delivery performance across all tenants.

The backend is absolutely authoritative regarding authentication, role-based authorization, request payload validation, and notification processing state.

## 2. Technology Stack

- **Java 21**
- **Spring Boot 3.3.x**
- **Spring Security** (JWT + OAuth2 Client)
- **Spring Data JPA & Hibernate**
- **PostgreSQL** (Relational data store)
- **Flyway** (Database migration)
- **jjwt** (JSON Web Token processing)
- **Spring Boot Actuator** (Observability)
- **Micrometer** (Metrics)
- **Cloudinary SDK** (Image/Logo storage)
- **Lombok** (Boilerplate reduction)
- **Maven**

## 3. Backend Architecture

The application relies on a domain-driven package structure, abstracting away framework concerns from business rules:

`
src/main/java/com/notificationengine/
├── admin/          # Admin-specific APIs and dashboard aggregations
├── apiToken/       # API token generation, hashing, and validation
├── authentication/ # JWT, Login, and Google OAuth2 controllers/services
├── company/        # Company profile, sender IDs, and logo management
├── dashboard/      # User dashboard metrics and aggregations
├── emailtemplate/  # Email template CRUD and variable rendering
├── exception/      # Global exception handlers and custom exceptions
├── infrastructure/ # Upstream provider abstractions and integrations
├── notification/   # Core domain: idempotency, queuing, attempts, scheduling
├── security/       # Spring Security configs, JWT filters, authentication providers
├── seeder/         # Initial database state bootstrapping
├── storage/        # Cloudinary integration for file uploads
└── user/           # User management
`

## 4. Request Flow

The core notification delivery sequence follows an asynchronous event-driven pattern for high availability:

1. **Client** POSTs to NotificationController.
2. **Authentication/Authorization** verifies the JWT or API Token.
3. **NotificationService** intercepts the request and verifies the Idempotency-Key.
4. **TransactionService** securely persists the notification entity in a QUEUED state.
5. A **202 Accepted** response is returned to the client (delivery is NOT synchronous).
6. **NotificationCreatedEvent** is fired internally.
7. **DeliveryProcessor** handles the event and passes it to the **ProviderRouter**.
8. **ProviderRouter** selects the highest-priority enabled NotificationProvider.
9. The Provider attempts delivery.
10. A **DeliveryAttempt** is recorded (SENT, FAILED, or RETRY_SCHEDULED).

## 5. Authentication

Authentication leverages Spring Security.

- **Email/Password:** POST /api/v1/auth/login uses the AuthenticationManager to verify credentials against the database and returns a signed application JWT.
- **Google OAuth2:** The platform supports a Google OAuth2 exchange-code flow. The backend processes the Google authorization code, extracts the Google user details, upserts the user in the database, and mints an application JWT for the frontend.

## 6. Authorization

Endpoint access is restricted via Spring Security using role-based access control (USER, ADMIN).

- **/api/v1/****: Base endpoints accessible by authenticated USER or ADMIN roles.
- **/api/v1/admin/****: Strictly isolated namespace requiring the ROLE_ADMIN authority.

## 7. JWT Security

The platform utilizes stateless JWT authentication.
- JwtAuthenticationFilter intercepts incoming requests, validates the signature, and verifies token expiry.
- JwtService handles generation and claims extraction.
- The authenticated principal (UserDetails) is populated into the SecurityContext, enabling safe resolution of the current user down the call stack.

## 8. API Token Authentication

Programmatic notification creation is supported via API tokens.
- Users generate API tokens from the frontend.
- Tokens are hashed using a secure algorithm and stored in the database.
- Clients pass the raw token via the X-API-Key HTTP header.
- A dedicated filter authenticates the API key and resolves the owning user context, allowing programmatic integration independent of session-based JWTs.

## 9. Notification API

**POST /api/v1/notifications**

*Headers:* 
- Authorization: Bearer <token> or X-API-Key: <token>
- Idempotency-Key: <uuid>

*Payload:*
`json
{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "template": "WELCOME_EMAIL",
  "variables": {
    "firstName": "John"
  },
  "advancedVariables": {
    "orderId": "12345"
  }
}
`
*Note:* Returns 202 Accepted indicating successful queuing, not immediate delivery.

## 10. Idempotency

To prevent duplicate deliveries (e.g., due to network timeouts or client retries):
- Clients must provide an Idempotency-Key header.
- Keys are scoped to the user.
- If a request matches an existing key with the identical payload, the existing notification is returned.
- If a request matches an existing key but with a *different* payload, a 409 Conflict is returned.
- Enforced at the database level via unique constraints.

## 11. Request Validation

The backend leverages Spring Boot Bean Validation (JSR 380).
- Strict email format validation.
- Required field verification on DTOs.
- Validation failures are caught globally and returned as structured 400 Bad Request API responses.

## 12. Notification Domain

The central Notification entity tracks state:
- id, userId, channel, ecipient, 	emplate
- ariables, dvancedVariables (stored as JSONB)
- status: QUEUED, PROCESSING, RETRY_SCHEDULED, SENT, FAILED
- etryCount, 
extRetryAt, idempotencyKey

## 13. Provider Architecture

Upstream integrations are managed through a provider abstraction.

`
ProviderRouter
      ↓
NotificationProvider (Interface)
      ├── Mailpit Email Provider (Local/Dev)
      ├── Mock SMS Provider
      └── Mock Transient Provider
`

- **ProviderRouter:** Dynamically evaluates enabled providers, sorting them by configured priority and channel capability, deciding which concrete provider executes the delivery.

## 14. Retry & Failover

Robust fault tolerance is built-in.
- **Transient Failures:** If a provider fails temporarily (e.g., 5xx error, timeout), the notification status becomes RETRY_SCHEDULED.
- **RetryScheduler:** A background polling mechanism claims RETRY_SCHEDULED notifications when 
extRetryAt is reached, utilizing exponential backoff logic.
- **Provider Fallback:** If a provider continuously fails and exhausts max retries, the ProviderRouter will seamlessly attempt the next highest-priority provider capable of handling the channel.

## 15. Delivery Attempts

Every interaction with an upstream provider is recorded immutably in the delivery_attempts table.
- Records provider, ttemptNumber, status, errorCode, providerMessageId.
- Powers the notification detail histories in the UI and acts as the data source for deriving provider health metrics.

## 16. Provider Health

Provider operational status is not hardcoded but dynamically derived from actual delivery history.
- Tracks lastAttemptAt, lastSuccessAt, lastFailureAt, and lastFailureCode.
- Admin dashboards use this data to present real-time upstream health.

## 17. Admin Provider Management

**/api/v1/admin/providers**
- Platform administrators can list all registered providers.
- Providers can be dynamically enabled/disabled without deploying code.
- Priorities can be adjusted to immediately shift traffic routing.
- State is persisted in the 
otification_provider_states table.

## 18. Email Templates

Templates allow dynamic content generation.
- Entities track code, subject, htmlBody, 	extBody, and ariables.
- Advanced rendering supports standard {{variables}} supplied by the API request, as well as automatic injection of COMPANY_PROFILE variables (e.g., logos, company names).

## 19. Email Rendering

The rendering engine processes template strings, securely escaping HTML injections where necessary, and replacing dynamic placeholders before handing the finalized payload to the selected Email Provider.

## 20. Email Providers

- **Mailpit:** Fully integrated for local development to catch and inspect outgoing SMTP traffic without risking external spam.

## 21. Company Profile

Users can define their organizational identity.
- Stores company name, support emails, and SMS sender IDs.
- Integrates with the **Cloudinary** SDK to securely upload, host, and delete company logos used in email templates.

## 22. Storage

An abstract StorageService interface isolates file-handling logic.
- Currently implemented via CloudinaryStorageService.
- Handles secure file uploads, deletion, and public URL generation for image assets.

## 23. User Notification APIs

- GET /api/v1/notifications (Paginated, filterable by status/channel)
- GET /api/v1/notifications/{id}
- GET /api/v1/notifications/{id}/attempts

*Security:* Queries automatically scope to the authenticated JWT's userId. The client does not supply a user ID.

## 24. Admin Notification APIs

- GET /api/v1/admin/notifications
- GET /api/v1/admin/notifications/{id}

*Security:* Exposes global traffic. Sensitive user credentials or provider secrets are strictly omitted from DTOs.

## 25. Dashboards

Provides aggregated metrics via efficient database queries.
- **User Dashboard:** Total sent/failed/queued, channel splits, recent traffic specifically for the authenticated tenant.
- **Admin Dashboard:** Platform-wide metrics, global user counts, provider health statuses, and recent systemic failures.

## 26. Database

- **PostgreSQL** is the primary relational datastore.
- Entity relationships are mapped via Spring Data JPA / Hibernate.
- Extensive use of JSONB for ariables and dvancedVariables to allow schema-less payload flexibility while maintaining relational integrity for core fields.

## 27. Flyway Migrations

Database schema evolution is strictly controlled via Flyway.
- Migrations reside in src/main/resources/db/migration.
- Existing V__ scripts are immutable; schema modifications require new versioned SQL scripts.

## 28. Database Constraints

Data integrity is enforced at the database level:
- Unique composite index on (user_id, idempotency_key).
- Unique constraints on email_template codes.
- Strict foreign key relationships between notifications, delivery attempts, and users.

## 29. Exception Handling

A @RestControllerAdvice annotated GlobalExceptionHandler ensures standard JSON error formats across all APIs:
`json
{
  "timestamp": "2026-09-28T...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/notifications"
}
`
- 400 Validation Errors
- 401 Unauthorized
- 403 Forbidden
- 409 Idempotency Conflicts

## 30. Observability

Spring Boot Actuator and Micrometer are configured to expose internal health, JVM metrics, and application metrics safely. 
- Structured logging ensures debuggability.
- **Security Rule:** Logs are sanitized to guarantee passwords, JWTs, API tokens, and Provider secrets are never output to standard out.

## 31. Configuration

Application environments are managed via YAML profiles (pplication-dev.yaml, etc.). Configurations include:
- Database connectivity
- JWT expiry and issuer properties
- Cloudinary storage configurations
- Mail server/provider settings
- Retry backoff intervals

*Actual secret values are injected via environment variables.*

## 32. Environment Variables

To run this backend locally, configure the following environment variables (never commit these values):

`env
DATABASE_URL=jdbc:postgresql://localhost:5432/notification_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=

JWT_SECRET=

GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
`
*(Variables are safely loaded by the application configuration at runtime.)*
