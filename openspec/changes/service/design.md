# Design: API Gateway Service

## Technical Approach

The API Gateway Service follows an **API-first, stateless gateway pattern** that centralizes cross-cutting concerns while delegating business logic to domain microservices. The design emphasizes:

1. **Separation of Concerns:** Clear layering between presentation (controllers), application (routing/aggregation), cross-cutting (middleware), and infrastructure (adapters)
2. **Policy-Driven Security:** Authentication and authorization externalized to dedicated middleware and services
3. **Resilient Integration:** Circuit breakers and retries for backend service calls
4. **Observability:** Comprehensive logging, metrics, and tracing

## Architecture Decisions

### AD-1: Middleware Chain Pattern
**Decision:** Implement cross-cutting concerns as composable middleware  
**Rationale:** Enables clean separation, testability, and reusability of authentication, RBAC, validation, logging, and error handling  
**Consequences:** Consistent request processing; easy to add/modify middleware

### AD-2: Stateless Gateway
**Decision:** Gateway maintains no domain state; all state in backend services  
**Rationale:** Enables horizontal scaling, simplifies deployment, improves reliability  
**Consequences:** Every request must include authentication context; no session affinity required

### AD-3: Circuit Breaker for Backend Calls
**Decision:** Implement circuit breaker pattern in BackendServiceAdapter  
**Rationale:** Prevents cascade failures when backend services are unhealthy  
**Consequences:** Graceful degradation; requires fallback handling

### AD-4: Centralized RBAC Evaluation
**Decision:** All permission checks delegated to RBAC Infrastructure Service  
**Rationale:** Single source of truth for access policies; consistent enforcement  
**Consequences:** Dependency on RBAC service; fail-closed on unavailability

### AD-5: Async Audit Logging
**Decision:** Audit logs written asynchronously via event bus or fire-and-forget REST  
**Rationale:** Prevents audit logging from impacting request latency  
**Consequences:** Potential for log loss in extreme failure scenarios; mitigated by local buffering

## Data Flow

```
┌─────────────┐     HTTPS/JWT      ┌─────────────────┐
│   Client    │ ─────────────────► │   API Gateway   │
│ (Web/Mobile)│                    │                 │
└─────────────┘                    └────────┬────────┘
                                            │
                    ┌───────────────────────┼───────────────────────┐
                    │                       │                       │
                    ▼                       ▼                       ▼
           ┌───────────────┐      ┌───────────────┐      ┌───────────────┐
           │ Authentication│      │     RBAC      │      │   Validation  │
           │  Middleware   │      │  Middleware   │      │  Middleware   │
           └───────┬───────┘      └───────┬───────┘      └───────┬───────┘
                   │                      │                      │
                   └──────────────────────┼──────────────────────┘
                                          │
                                          ▼
                                 ┌─────────────────┐
                                 │ ApiRoutingService│
                                 └────────┬────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
           ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
           │  Lead Mgmt    │     │  Doc Mgmt     │     │  Analytics    │
           │ Microservice  │     │ Microservice  │     │ Microservice  │
           └───────────────┘     └───────────────┘     └───────────────┘
```

## API Design

### URL Structure
- Base URL: `https://api.sammaan.com/v1`
- Resource-oriented paths: `/api/v1/{resource}` (plural nouns)
- Sub-resources: `/api/v1/{resource}/{id}/{sub-resource}`
- Actions: POST to `/api/v1/{resource}/{id}/{action}`

### Versioning Strategy
- URL-based versioning (`/v1/`, `/v2/`)
- Major versions for breaking changes
- Backward compatibility within major version

### Response Envelope
```json
{
  "status": 200,
  "message": "Success",
  "data": { ... },
  "errors": []
}
```

### Pagination Envelope
```json
{
  "items": [...],
  "page": 1,
  "pageSize": 20,
  "totalPages": 10,
  "totalItems": 200
}
```

## Component Structure

```
src/
├── controllers/
│   └── ApiGatewayController.ts
├── services/
│   ├── ApiRoutingService.ts
│   └── BackendServiceAdapter.ts
├── middleware/
│   ├── AuthenticationMiddleware.ts
│   ├── RBACMiddleware.ts
│   ├── RequestValidationMiddleware.ts
│   ├── AuditLoggingMiddleware.ts
│   └── ErrorHandlingMiddleware.ts
├── config/
│   ├── ApiGatewayConfig.ts
│   └── routes.json
├── models/
│   ├── RequestContext.ts
│   ├── ApiResponse.ts
│   ├── RouteConfig.ts
│   └── ErrorResponse.ts
└── utils/
    ├── CircuitBreaker.ts
    └── RetryPolicy.ts
```

## Security Design

### Authentication Flow
1. JWT token extracted from `Authorization: Bearer <token>` header
2. Token signature verified against public key
3. Token expiration checked
4. User claims extracted and attached to RequestContext

### RBAC Enforcement
1. Route matched to RBAC policy from configuration
2. User roles and geo-scope extracted from RequestContext
3. RBAC service queried for permission decision
4. Request blocked with 403 if unauthorized

### Rate Limiting
- Per-partner/user limits (default: 1000 req/min)
- Sliding window algorithm
- Redis-backed counter for distributed enforcement
- HTTP 429 response with `Retry-After` header

## Error Handling Strategy

| Error Type | HTTP Status | Error Code | Handling |
|------------|-------------|------------|----------|
| Validation Error | 400 | VALIDATION_ERROR | Return field-level details |
| Authentication Error | 401 | AUTH_ERROR | Clear error message |
| Authorization Error | 403 | FORBIDDEN | Log for compliance |
| Not Found | 404 | NOT_FOUND | Resource-specific message |
| Rate Limit | 429 | RATE_LIMITED | Include Retry-After |
| Backend Timeout | 504 | GATEWAY_TIMEOUT | Circuit breaker engaged |
| Internal Error | 500 | INTERNAL_ERROR | Generic message; log details |

## Observability Design

### Logging
- Structured JSON logs
- Correlation ID in all log entries
- Request/response logging (PII masked)
- Integration with ELK Stack

### Metrics (Prometheus)
- `api_requests_total` (counter by endpoint, status)
- `api_request_duration_seconds` (histogram)
- `api_rate_limit_exceeded_total` (counter)
- `backend_circuit_breaker_state` (gauge)

### Tracing
- OpenTelemetry integration
- Trace ID propagated to backend services
- Span for each middleware and backend call

---