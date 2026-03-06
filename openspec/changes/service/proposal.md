# Proposal: API Gateway Service

## Overview

**Service ID:** SVC-API-GW  
**Service Name:** API Gateway Service  
**Change Name:** service

## Purpose and Business Value

The API Gateway Service is the centralized entry point for the Digital Aggregators Partner Portal, providing unified access to all backend microservices. It delivers critical business value by:

- **Unified API Access:** Single entry point for partners, agents, admins, and external integrators to interact with lead management, document handling, analytics, finance, ticketing, and knowledge hub capabilities.
- **Security Enforcement:** Centralized authentication (OAuth2/JWT) and fine-grained Role-Based Access Control (RBAC) with geo-scoping ensures only authorized users access appropriate resources.
- **Operational Excellence:** Rate limiting, request validation, audit logging, and standardized error handling improve reliability, compliance, and partner experience.
- **Integration Enablement:** Supports bulk operations, webhooks, and async export jobs for high-scale B2B partner integrations.

## Scope

### In-Scope

- Routing all RESTful API requests to appropriate backend microservices
- OAuth2/JWT authentication and token validation
- RBAC policy enforcement with geo/resource scoping
- Request validation using JSON Schema
- Rate limiting and throttling per partner/user
- Centralized audit logging for compliance
- API versioning and lifecycle management
- Aggregation and transformation of backend responses
- Health, metrics, and info endpoints for observability
- Error handling with standardized error contracts

### Out-of-Scope

- Business logic execution (delegated to domain microservices)
- Direct database access (stateless gateway)
- Document storage or processing (delegated to Document Management Microservice)
- Payment processing (delegated to Finance & Payouts Microservice)
- User provisioning (delegated to User & Access Management Microservice)

## Responsibilities

- Expose and aggregate RESTful endpoints for all business processes
- Route requests to appropriate backend services (Lead, Document, Analytics, Finance, User, Ticketing, Knowledge Hub)
- Enforce authentication, RBAC, and rate limiting
- Centralize API analytics, versioning, and logging
- Integrate with webhooks for partner notifications
- Provide standardized error responses with traceability

## Impacted Systems and Data Stores

### Upstream Dependencies (Consumers)
- Partner Portal Web/Mobile Frontend (UI-01)
- External Partner Systems (API/Webhook integrations)
- System Integrators (External Developer APIs)

### Downstream Dependencies (Providers)
| Service ID | Service Name | Protocol | Purpose |
|------------|--------------|----------|---------|
| SVC-LEAD-MGMT | Lead Management Microservice | REST/HTTPS | Lead CRUD, bulk intake |
| SVC-DOC-MGMT | Document Management Microservice | REST/HTTPS | Document upload/download |
| SVC-ANALYTICS | Analytics & Reporting Microservice | REST/HTTPS | Funnel, geo-heatmap, exports |
| SVC-FINANCE | Finance & Payouts Microservice | REST/HTTPS | Payouts, invoices, disputes |
| SVC-USER-MGMT | User & Access Management Microservice | REST/HTTPS + OAuth2 | User context, authentication |
| SVC-TICKETING | Ticketing & Communication Microservice | REST/HTTPS | Support tickets |
| SVC-KNOWLEDGE | Knowledge Hub / Compliance Microservice | REST/HTTPS | Training content, certifications |
| SVC-RBAC | RBAC Infrastructure Service | REST/gRPC | Permission evaluation |
| SVC-AUDIT | Audit Logging Service | REST/Event | Immutable audit trails |

### External Systems
- OAuth2/SSO Provider (External Authentication)
- CDN (Static content delivery)
- External Monitoring (ELK/Prometheus/Grafana)

### Data Stores (Indirect via Microservices)
- PostgreSQL (via backend services)
- Redis (Session/cache)
- Elasticsearch (Analytics/search)

## Acceptance Criteria

### AC-1: Authentication and Authorization
- **Given** a request with a valid OAuth2 Bearer token
- **When** the API Gateway receives the request
- **Then** the token SHALL be validated and user context attached to RequestContext
- **And** RBAC policies SHALL be evaluated before routing

### AC-2: Request Routing
- **Given** an authenticated request to `/api/v1/leads`
- **When** the request passes validation
- **Then** it SHALL be routed to the Lead Management Microservice
- **And** the response SHALL be returned with appropriate status codes

### AC-3: Rate Limiting
- **Given** a partner exceeding 1000 requests/minute
- **When** subsequent requests arrive
- **Then** the gateway SHALL return HTTP 429 (Too Many Requests)
- **And** the rate limit violation SHALL be logged

### AC-4: Audit Logging
- **Given** any API request (authenticated or not)
- **When** the request is processed
- **Then** request metadata, user context, and response status SHALL be logged to the Audit Logging Service

### AC-5: Error Handling
- **Given** any error condition (validation, auth, backend failure)
- **When** an error occurs
- **Then** a standardized ErrorResponse SHALL be returned with code, message, details, and traceId

### AC-6: Health and Observability
- **Given** a request to `/health` or `/metrics`
- **When** the endpoint is called
- **Then** system health status or Prometheus metrics SHALL be returned

## Related Features

- F-01, F-02, F-03, F-04, F-05, F-06, F-07, F-08, F-09

---