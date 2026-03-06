# Specification: API Gateway Service

## Purpose

The API Gateway Service SHALL serve as the unified, secure entry point for all client interactions with the Digital Aggregators Partner Portal. It SHALL route requests to backend microservices, enforce authentication and authorization, validate inputs, manage rate limits, and provide centralized logging and error handling.

## Technologies and Runtime Stack

| Category | Technology |
|----------|------------|
| Gateway Platform | Kong / Apigee / AWS API Gateway / Nginx |
| Authentication | OAuth2, JWT |
| API Specification | OpenAPI/Swagger 3.0 |
| Runtime | Node.js (Express/NestJS) or equivalent |
| Deployment | Kubernetes (EKS/AKS/GKE) |
| Caching | Redis (session/permission cache) |
| Monitoring | Prometheus, Grafana, ELK Stack |

## Components

### Presentation Layer
| Component | Description |
|-----------|-------------|
| ApiGatewayController | Primary REST controller exposing unified endpoints, parsing requests, applying authentication, and dispatching to handlers |

### Application Layer
| Component | Description |
|-----------|-------------|
| ApiRoutingService | Encapsulates logic for dynamic routing to backend services, API version mapping, and response aggregation |
| RequestContext | Immutable context for each request tracking correlation ID, user, roles, tenant/geo, and request metadata |

### Cross-Cutting Concerns
| Component | Description |
|-----------|-------------|
| AuthenticationMiddleware | Validates access tokens/API keys for incoming requests; supports OAuth2/JWT |
| RBACMiddleware | Evaluates user roles and permissions for request path/data, integrating with RBAC Infrastructure Service |
| RequestValidationMiddleware | Checks input payloads for schema/format violations using JSON Schema |
| AuditLoggingMiddleware | Logs all API events, user actions, and sensitive accesses for compliance |
| ErrorHandlingMiddleware | Global handler for exceptions, ensuring standardized error responses |

### Infrastructure Layer
| Component | Description |
|-----------|-------------|
| BackendServiceAdapter | Handles all outbound HTTP/gRPC calls to microservices with retry and circuit-breaker logic |
| ApiGatewayConfig | Configuration management; loads route map, policy configs, and service URLs |

---

## APIs

### Lead Management Endpoints

#### Requirement: Lead Listing
The system SHALL provide paginated lead listing with filtering capabilities.

##### Scenario: List Leads with Pagination
- **Given** an authenticated user with appropriate RBAC permissions
- **When** a GET request is made to `/api/v1/leads` with pagination parameters
- **Then** the system SHALL return a PaginatedResponse containing leads
- **And** results SHALL be filtered by user's geo-scope and partner context

#### Requirement: Lead Creation
The system SHALL support single and bulk lead creation.

##### Scenario: Create Single Lead
- **Given** an authenticated user with lead creation permission
- **When** a POST request is made to `/api/v1/leads` with valid Lead JSON
- **Then** the system SHALL route to Lead Management Microservice
- **And** return HTTP 201 with created lead details

##### Scenario: Bulk Lead Upload
- **Given** an authenticated user with bulk upload permission
- **When** a POST request is made to `/api/v1/leads` with multipart/form-data (CSV/XLS)
- **Then** the system SHALL accept the file and initiate async processing
- **And** return HTTP 202 with job tracking information

#### Requirement: Lead Operations
The system SHALL support GET, PUT, DELETE operations on individual leads.

##### Scenario: Update Lead
- **Given** an authenticated user with lead update permission
- **When** a PUT request is made to `/api/v1/leads/{leadId}`
- **Then** the system SHALL validate ownership/RBAC
- **And** route the update to Lead Management Microservice

---

### Document Management Endpoints

#### Requirement: Document Upload
The system SHALL support secure document uploads with virus scanning.

##### Scenario: Upload Document for Lead
- **Given** an authenticated user with document upload permission
- **When** a POST request is made to `/api/v1/leads/{leadId}/documents` with file
- **Then** the system SHALL route to Document Management Microservice
- **And** the document SHALL be queued for AV scanning

#### Requirement: Document Download
The system SHALL provide secure document downloads via presigned URLs.

##### Scenario: Download Document
- **Given** an authenticated user with document access permission
- **When** a GET request is made to `/api/v1/documents/{documentId}`
- **Then** the system SHALL verify RBAC and geo-scope
- **And** return document metadata with presigned download URL
- **And** downloads MAY include watermarking based on policy

---

### Analytics Endpoints

#### Requirement: Funnel Analytics
The system SHALL provide real-time funnel analytics.

##### Scenario: Get Funnel Data
- **Given** an authenticated user with analytics permission
- **When** a GET request is made to `/api/v1/analytics/funnel` with filters
- **Then** the system SHALL return AnalyticsFunnelResponse with stage counts and conversion rates

#### Requirement: Async Export
The system SHALL support asynchronous report exports.

##### Scenario: Initiate Export Job
- **Given** an authenticated user with export permission
- **When** a POST request is made to `/api/v1/analytics/exports`
- **Then** the system SHALL return HTTP 202 with jobId
- **And** the user SHALL poll `/api/v1/exports/jobs/{jobId}` for status

---

### Authentication Endpoints

#### Requirement: User Authentication
The system SHALL provide login and token refresh capabilities.

##### Scenario: User Login
- **Given** valid username and password credentials
- **When** a POST request is made to `/api/v1/auth/login`
- **Then** the system SHALL return JWT access token and refresh token
- **And** access tokens SHALL expire in 60 minutes

##### Scenario: Token Refresh
- **Given** a valid refresh token
- **When** a POST request is made to `/api/v1/auth/refresh`
- **Then** the system SHALL return a new access token

---

### Health and Observability Endpoints

#### Requirement: Health Check
The system SHALL expose health status.

##### Scenario: Health Endpoint
- **Given** any client (no authentication required)
- **When** a GET request is made to `/health`
- **Then** the system SHALL return HTTP 200 with health status

#### Requirement: Metrics Exposure
The system SHALL expose Prometheus-compatible metrics.

##### Scenario: Metrics Endpoint
- **Given** any client (no authentication required)
- **When** a GET request is made to `/metrics`
- **Then** the system SHALL return application metrics

---

## Data Models

### RequestContext
| Field | Type | Description |
|-------|------|-------------|
| correlationId | string | Unique request trace identifier |
| userId | string | Authenticated user ID |
| roles | array[string] | User's assigned roles |
| geo | string | User's geographic scope |
| tenant | string | Partner/tenant identifier |
| requestTimestamp | Date | Request initiation time |

### ApiResponse
| Field | Type | Description |
|-------|------|-------------|
| status | number | HTTP status code |
| message | string | Human-readable message |
| data | any | Response payload |
| errors | array[string] | Error details if applicable |

### RouteConfig
| Field | Type | Description |
|-------|------|-------------|
| routeKey | string | Unique route identifier |
| method | string | HTTP method (GET/POST/PUT/DELETE) |
| backendServiceId | string | Target microservice ID |
| requiresAuth | boolean | Authentication required flag |
| rbacPolicy | object | RBAC policy configuration |
| schema | object | JSON Schema for validation |

### ErrorResponse
| Field | Type | Description |
|-------|------|-------------|
| code | string | Error code |
| message | string | Error message |
| details | array[{field, issue}] | Field-level validation errors |
| traceId | string | Correlation ID for incident tracking |

### PaginatedResponse
| Field | Type | Description |
|-------|------|-------------|
| items | array[object] | Result items |
| page | integer | Current page number |
| pageSize | integer | Items per page |
| totalPages | integer | Total pages available |
| totalItems | integer | Total items count |

---

## Interactions with Dependencies

### RBAC Infrastructure Service (SVC-RBAC)
- **Protocol:** REST/gRPC
- **Calls:** `authorize(context, routeKey)` for permission evaluation
- **Payload:** RequestContext with user, roles, geo, resource path
- **Error Handling:** Fail-closed on RBAC service unavailability (return 403)

### User & Access Management Microservice (SVC-USER-MGMT)
- **Protocol:** REST/HTTPS + OAuth2
- **Calls:** Token validation, user context retrieval
- **Payload:** JWT token for validation
- **Error Handling:** Return 401 on invalid/expired tokens

### Backend Microservices (Lead, Document, Analytics, Finance, Ticketing, Knowledge)
- **Protocol:** REST/HTTPS
- **Calls:** Proxied requests based on route configuration
- **Retry Policy:** Configurable retries with exponential backoff
- **Circuit Breaker:** Open circuit after consecutive failures; half-open for recovery

### Audit Logging Service (SVC-AUDIT)
- **Protocol:** REST or Event (Kafka)
- **Calls:** `logRequest(context, apiResponse)` for every API call
- **Payload:** Request metadata, user context, response status, timestamp
- **Error Handling:** Async/fire-and-forget; failures logged locally

---

## Key Flows

### Request Processing Flow
1. Client sends HTTPS request to API Gateway
2. AuthenticationMiddleware validates OAuth2/JWT token
3. RequestContext created with user, roles, geo, tenant
4. RBACMiddleware evaluates permissions for route/resource
5. RequestValidationMiddleware validates payload against JSON Schema
6. ApiRoutingService determines target backend service
7. BackendServiceAdapter invokes microservice with retry/circuit-breaker
8. Response transformed and returned to client
9. AuditLoggingMiddleware logs request/response asynchronously
10. ErrorHandlingMiddleware catches any exceptions and returns standardized error

### Authentication Flow
1. Client POSTs credentials to `/api/v1/auth/login`
2. Gateway forwards to User & Access Management Microservice
3. Credentials validated against identity store
4. JWT access token (60min) and refresh token (7 days) returned
5. Client includes Bearer token in subsequent requests
6. Token refresh via `/api/v1/auth/refresh` before expiry

### Bulk Upload Flow
1. Client POSTs multipart file to `/api/v1/leads`
2. Gateway validates file type/size
3. Request forwarded to Lead Management Microservice
4. Async job created; jobId returned with HTTP 202
5. Client polls `/api/v1/exports/jobs/{jobId}` for status
6. On completion, results available for download

---