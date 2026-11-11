# Implementation Plan: US-001

## Technical Context
- Codebase grounding snapshot (c2aa26bdd0af674c)
- Accepted impact analysis (sha256:3cd9b4c86818ed7b3a1052d2d42a523762591064ac9a98ae7a79f892c92ee027)
- Implement only against Accepted impact analysis (revision 1) (fingerprint sha256:3cd9b4c86818ed7b3a1052d2d42a523762591064ac9a98ae7a79f892c92ee027)
- Resolve or explicitly accept the following implementation-readiness items:
  - Candidate verification flagged a possibly better-matching alternative that was not selected: CartModificationException (25091) -- review before accepting.
  - Candidate verification found the selected focus object implausible for this requirement: The requirement concerns excluding cart or abandoned-cart resources from a clinic booking API, while the selected object is a checkout Java method in an order-related e-commerce API, which is not clearly tied to clinic booking or cart resource exposure. Other candidates like CartModificationException or PersistableShoppingCartItem are more directly related to cart semantics that the requirement targets.
  - No OpenAPI/Swagger or documentation text was provided in CAST facts; only code and transaction graphs are available.
  - No pre-existing implementation was found for this requirement; scope is derived solely from the requirement text, not from measured code evidence.
  - No-match classification: Initial classification (net-new capability) has a noted caveat: Clinic booking and appointment concepts are fairly generic for a healthcare system, and existing APIs might already embody a booking surface under different names that were not searched, even if explicit cart semantics are new.
  - No-match consistency check raised a caveat about this classification: Clinic booking and appointment concepts are fairly generic for a healthcare system, and existing APIs might already embody a booking surface under different names that were not searched, even if explicit cart semantics are new.
  - Request/response payload shapes and field names for checkout(9083) are not present, limiting schema-level analysis.
  - Requirement detail is incomplete (see evidence_gaps); no conflicting existing implementation was found
  - Searched 4 term(s) derived from this requirement (booking api, appointment, cart, checkout) against Cast; none resolved to an existing code object.
  - Which components will need to be built to implement each acceptance criterion, since no existing implementation was found?

## Contract Changes
- No material API, event, or data contract change is evidenced.

## Security and Quality
- Preserve the security controls evidenced by c2aa26bdd0af674c
- Preserve the availability, reliability, and observability constraints grounded by c2aa26bdd0af674c

## Verification Plan
- **US-001-ac-1**: Given the clinic appointment booking REST API base URL in a production-like environment
When I list all publicly documented endpoints and their resources
Then I see only resources related to appointments, slots, bookings, rescheduling, cancellations, and notifications
And I see no resources or paths whose primary purpose is carts, draft carts, baskets, cart items, or abandoned carts
- **US-001-ac-2**: Given the generated and human-written API documentation for the clinic booking product
When a backend engineer searches for terms such as "cart", "basket", "draft cart", or "abandoned cart"
Then no clinic booking endpoints or models are described using these terms
And any mention of cart concepts appears only as an explicit statement that they are out of scope for this product
- **US-001-ac-3**: Given API request and response schemas for appointment booking operations
When they are reviewed against the scope rules
Then no top-level or nested fields are present whose primary purpose is to persist cart or abandoned-cart state
And no example payloads demonstrate cart-like usage patterns
- **US-001-ac-4**: Given an API consumer in a production-like environment
When they attempt to discover or invoke any cart-related endpoint under the clinic booking base path
Then the API does not expose such endpoints
And there is clear documentation stating that cart and abandoned-cart concepts are not part of the clinic booking product scope
- **IMPACT**: Verify acceptance criterion: Given the clinic appointment booking REST API base URL in a production-like environment
When I list all publicly documented endpoints and their resources
Then I see only resources related to appointments, slots, bookings, rescheduling, cancellations, and notifications
And I see no resources or paths whose primary purpose is carts, draft carts, baskets, cart items, or abandoned carts
- **IMPACT**: Verify acceptance criterion: Given the generated and human-written API documentation for the clinic booking product
When a backend engineer searches for terms such as "cart", "basket", "draft cart", or "abandoned cart"
Then no clinic booking endpoints or models are described using these terms
And any mention of cart concepts appears only as an explicit statement that they are out of scope for this product
- **IMPACT**: Verify acceptance criterion: Given API request and response schemas for appointment booking operations
When they are reviewed against the scope rules
Then no top-level or nested fields are present whose primary purpose is to persist cart or abandoned-cart state
And no example payloads demonstrate cart-like usage patterns
- **IMPACT**: Verify acceptance criterion: Given an API consumer in a production-like environment
When they attempt to discover or invoke any cart-related endpoint under the clinic booking base path
Then the API does not expose such endpoints
And there is clear documentation stating that cart and abandoned-cart concepts are not part of the clinic booking product scope