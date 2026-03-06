# Implementation Tasks: API Gateway Service

## Phase 1: Core Infrastructure

### Configuration and Setup
- [ ] Create project structure with `src/controllers`, `src/services`, `src/middleware`, `src/config`, `src/models`, `src/utils` directories
- [ ] Set up TypeScript/Node.js project with Express or NestJS framework
- [ ] Configure ESLint, Prettier, and testing framework (Jest)
- [ ] Create `ApiGatewayConfig.ts` for loading route configurations and service URLs
- [ ] Create `routes.json` configuration file with route-to-service mappings

### Data Models
- [ ] Implement `RequestContext.ts` with correlationId, userId, roles, geo, tenant, requestTimestamp fields
- [ ] Implement `ApiResponse.ts` with status, message, data, errors fields
- [ ] Implement `RouteConfig.ts` with routeKey, method, backendServiceId, requiresAuth, rbacPolicy, schema fields
- [ ] Implement `ErrorResponse.ts` with code, message, details, traceId fields
- [ ] Implement `PaginatedResponse.ts` wrapper model

## Phase 2: Middleware Layer

### Authentication Middleware
- [ ] Implement `AuthenticationMiddleware.ts` with `authenticate(req, res, next)` method
- [ ] Add JWT token extraction from Authorization header
- [ ] Implement token signature verification using public key
- [ ] Add token expiration validation
- [ ] Attach user claims to RequestContext on successful authentication
- [ ] Return 401 with ErrorResponse on authentication failure

### RBAC Middleware
- [ ] Implement `RBACMiddleware.ts` with `authorize(context, routeKey)` method
- [ ] Integrate with RBAC Infrastructure Service (SVC-RBAC) via REST/gRPC
- [ ] Implement permission evaluation based on user roles and geo-scope
- [ ] Return 403 with ErrorResponse on authorization failure
- [ ] Implement fail-closed behavior when RBAC service unavailable

### Request Validation Middleware
- [ ] Implement `RequestValidationMiddleware.ts` with `validatePayload(req, res, next)` method
- [ ] Integrate JSON Schema validation library (ajv or similar)
- [ ] Load schemas from route configuration
- [ ] Return 400 with field-level validation errors in ErrorResponse

### Audit Logging Middleware
- [ ] Implement `AuditLoggingMiddleware.ts` with `logRequest(context, apiResponse)` method
- [ ] Create async logging mechanism (fire-and-forget)
- [ ] Integrate with Audit Logging Service (SVC-AUDIT) via REST or Kafka
- [ ] Include request metadata, user context, response status, timestamp
- [ ] Implement local buffering for resilience

### Error Handling Middleware
- [ ] Implement `ErrorHandlingMiddleware.ts` with `handleError(err, req, res, next)` method
- [ ] Map exception types to appropriate HTTP status codes
- [ ] Generate standardized ErrorResponse with traceId
- [ ] Log error details for debugging (mask PII)

## Phase 3: Application Layer

### API Routing Service
- [ ] Implement `ApiRoutingService.ts` with `routeRequest(context, routeKey, payload)` method
- [ ] Load route configurations from `ApiGatewayConfig`
- [ ] Implement dynamic routing logic based on request path and method
- [ ] Add response aggregation/transformation capabilities
- [ ] Handle API versioning (v1, v2, etc.)

### Backend Service Adapter
- [ ] Implement `BackendServiceAdapter.ts` with `callService(serviceId, endpoint, payload, headers)` method
- [ ] Create HTTP client with configurable timeout
- [ ] Implement retry policy with exponential backoff
- [ ] Implement circuit breaker pattern (open/half-open/closed states)
- [ ] Add request/response logging with correlation ID propagation
- [ ] Handle backend error mapping to gateway errors

### Circuit Breaker Utility
- [ ] Implement `CircuitBreaker.ts` utility class
- [ ] Configure failure threshold for circuit opening
- [ ] Implement half-open state for recovery testing
- [ ] Add metrics for circuit breaker state changes

### Retry Policy Utility
- [ ] Implement `RetryPolicy.ts` utility class
- [ ] Configure max retries and backoff intervals
- [ ] Implement exponential backoff with jitter
- [ ] Add retry logging for debugging

## Phase 4: Controller Layer

### API Gateway Controller
- [ ] Implement `ApiGatewayController.ts` with `handleRequest(req, res)` method
- [ ] Wire middleware chain (auth → RBAC → validation → routing → error handling)
- [ ] Register routes for all API endpoints from configuration
- [ ] Implement request parsing and response formatting

### Health and Observability Endpoints
- [ ] Implement `/health` endpoint returning system health status
- [ ] Implement `/metrics` endpoint exposing Prometheus metrics
- [ ] Implement `/api/v1/info` endpoint returning build/version information

### Authentication Endpoints
- [ ] Implement `/api/v1/auth/login` endpoint (proxy to User Management)
- [ ] Implement `/api/v1/auth/refresh` endpoint (proxy to User Management)

## Phase 5: Rate Limiting

### Rate Limiter Implementation
- [ ] Implement rate limiting middleware using sliding window algorithm
- [ ] Integrate with Redis for distributed counter storage
- [ ] Configure per-partner/user limits (default: 1000 req/min)
- [ ] Return HTTP 429 with `Retry-After` header on limit exceeded
- [ ] Add rate limit metrics for monitoring

## Phase 6: Observability

### Logging Integration
- [ ] Configure structured JSON logging
- [ ] Add correlation ID to all log entries
- [ ] Implement PII masking in request/response logs
- [ ] Configure log shipping to ELK Stack

### Metrics Integration
- [ ] Integrate Prometheus client library
- [ ] Implement `api_requests_total` counter (by endpoint, status)
- [ ] Implement `api_request_duration_seconds` histogram
- [ ] Implement `api_rate_limit_exceeded_total` counter
- [ ] Implement `backend_circuit_breaker_state` gauge

### Tracing Integration
- [ ] Integrate OpenTelemetry SDK
- [ ] Configure trace ID generation and propagation
- [ ] Add spans for middleware and backend calls
- [ ] Configure trace export to observability platform

## Phase 7: Testing

### Unit Tests
- [ ] Write unit tests for `AuthenticationMiddleware`
- [ ] Write unit tests for `RBACMiddleware`
- [ ] Write unit tests for `RequestValidationMiddleware`
- [ ] Write unit tests for `ApiRoutingService`
- [ ] Write unit tests for `BackendServiceAdapter`
- [ ] Write unit tests for `CircuitBreaker`
- [ ] Write unit tests for `RetryPolicy`

### Integration Tests
- [ ] Write integration tests for authentication flow
- [ ] Write integration tests for RBAC enforcement
- [ ] Write integration tests for backend service routing
- [ ] Write integration tests for rate limiting
- [ ] Write integration tests for error handling

### Contract Tests
- [ ] Define OpenAPI contract tests for all endpoints
- [ ] Validate request/response schemas against OpenAPI spec
- [ ] Test error response contracts

## Phase 8: Deployment

### Containerization
- [ ] Create Dockerfile for API Gateway Service
- [ ] Configure multi-stage build for optimized image
- [ ] Add health check configuration to Dockerfile

### Kubernetes Deployment
- [ ] Create Kubernetes Deployment manifest
- [ ] Create Kubernetes Service manifest
- [ ] Create Kubernetes Ingress manifest
- [ ] Configure horizontal pod autoscaling (HPA)
- [ ] Create ConfigMap for route configuration
- [ ] Create Secret for sensitive configuration

### CI/CD Pipeline
- [ ] Configure build pipeline (lint, test, build, scan)
- [ ] Configure deployment pipeline (dev, staging, prod)
- [ ] Add security scanning (SAST, dependency scan)
- [ ] Configure rollback procedures

## TODO: Clarifications Needed

- [ ] **TODO:** Clarify exact rate limit thresholds per partner tier from business requirements
- [ ] **TODO:** Confirm circuit breaker thresholds (failure count, timeout duration) with SRE team
- [ ] **TODO:** Obtain JSON schemas for all request/response payloads from API design team
- [ ] **TODO:** Confirm RBAC service protocol (REST vs gRPC) with platform team
- [ ] **TODO:** Clarify audit log retention policy and compliance requirements
- [ ] **TODO:** Confirm CDN integration requirements for static content routing