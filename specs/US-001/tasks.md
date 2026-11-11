# Tasks: US-001

- [ ] T001 FR-US-001-1 (maps to US-001-ac-1): The clinic appointment booking REST API, when accessed via its base URL in a production-like or production-equivalent environment, shall expose only endpoints and resources whose functional purpose is directly related to appointments, slots, bookings, rescheduling, cancellations, or notifications within the clinic booking domain.
- Any endpoint or path whose primary business purpose is carts, draft carts, baskets, cart items, or abandoned carts shall not be present under the clinic booking API base URL.
- "Primary purpose" is determined by the main business operation the endpoint exists to support; if that operation is cart creation, cart manipulation, storage of pending items, or recovery of abandoned selections, it is considered in scope of this exclusion, even if implemented with neutral naming.
- Ambiguity note: The requirement does not define the exact base URL format or environment promotion process; implementers must apply this exclusion to whatever is designated as the clinic booking REST API base path in each target environment.
- [ ] T002 FR-US-001-2 (maps to US-001-ac-2): All generated and human-written API documentation for the clinic booking product (including but not limited to OpenAPI/Swagger descriptions, HTML docs, README files, and developer guides) shall avoid describing any clinic booking endpoints or models using cart-related terminology such as "cart", "basket", "draft cart", or "abandoned cart".
- Cart-related terms may appear only to explicitly state that cart and abandoned-cart concepts are out of scope for the clinic booking product.
- Documentation search for the strings "cart", "basket", "draft cart", and "abandoned cart" shall not return clinic booking API resources or data models whose described behavior implies cart-like selection, temporary storage, or checkout flows.
- Ambiguity note: The acceptance criterion does not specify which documentation search tooling is authoritative; implementers must ensure that any official documentation channels under product control comply with this rule.
- [ ] T003 FR-US-001-3 (maps to US-001-ac-3): For all appointment booking-related API operations, the defined request and response schemas (including nested objects and arrays) shall not introduce fields whose primary purpose is to persist cart or abandoned-cart state.
- Fields whose main purpose is to represent temporary or persistent collections of selectable items intended for later confirmation or checkout (for example, a list of pre-selected appointment options saved for later) shall be excluded from the clinic booking schemas.
- Example payloads associated with appointment booking operations shall not show usage patterns that resemble building, saving, or restoring a cart or abandoned cart.
- Ambiguity note: The acceptance criterion does not enumerate allowed vs. disallowed field names, so implementers must judge "primary purpose" based on business semantics, not merely on the absence of the words "cart" or "basket".
- [ ] T004 FR-US-001-4 (maps to US-001-ac-4): Under the clinic booking API base path in a production-like environment, the API shall not expose any discoverable or invocable endpoints whose purpose is cart-related (including carts, draft carts, baskets, cart items, or abandoned carts).
- Attempts by an API consumer to discover such endpoints (for example, via OpenAPI listings, API gateway catalogs, or link traversal) shall not reveal any cart-related clinic booking endpoints.
- Any attempt to directly invoke a path under the clinic booking base URL that would otherwise correspond to a cart concept (based on product-owned routing configuration) shall either not be routable within the clinic booking API or shall be clearly outside this product's scope.
- Official documentation for the clinic booking API shall contain an explicit statement that cart and abandoned-cart concepts are not part of the clinic booking product scope.
- Ambiguity note: The mechanism for making cart-related endpoints non-discoverable (e.g., not defining them vs. blocking them) is not specified; implementers must ensure the observable behavior to external API consumers matches this requirement.
- [ ] T005 Verify US-001-ac-1: Given the clinic appointment booking REST API base URL in a production-like environment
When I list all publicly documented endpoints and their resources
Then I see only resources related to appointments, slots, bookings, rescheduling, cancellations, and notifications
And I see no resources or paths whose primary purpose is carts, draft carts, baskets, cart items, or abandoned carts
- [ ] T006 Verify US-001-ac-2: Given the generated and human-written API documentation for the clinic booking product
When a backend engineer searches for terms such as "cart", "basket", "draft cart", or "abandoned cart"
Then no clinic booking endpoints or models are described using these terms
And any mention of cart concepts appears only as an explicit statement that they are out of scope for this product
- [ ] T007 Verify US-001-ac-3: Given API request and response schemas for appointment booking operations
When they are reviewed against the scope rules
Then no top-level or nested fields are present whose primary purpose is to persist cart or abandoned-cart state
And no example payloads demonstrate cart-like usage patterns
- [ ] T008 Verify US-001-ac-4: Given an API consumer in a production-like environment
When they attempt to discover or invoke any cart-related endpoint under the clinic booking base path
Then the API does not expose such endpoints
And there is clear documentation stating that cart and abandoned-cart concepts are not part of the clinic booking product scope
- [ ] T009 Verify IMPACT: Verify acceptance criterion: Given the clinic appointment booking REST API base URL in a production-like environment
When I list all publicly documented endpoints and their resources
Then I see only resources related to appointments, slots, bookings, rescheduling, cancellations, and notifications
And I see no resources or paths whose primary purpose is carts, draft carts, baskets, cart items, or abandoned carts
- [ ] T010 Verify IMPACT: Verify acceptance criterion: Given the generated and human-written API documentation for the clinic booking product
When a backend engineer searches for terms such as "cart", "basket", "draft cart", or "abandoned cart"
Then no clinic booking endpoints or models are described using these terms
And any mention of cart concepts appears only as an explicit statement that they are out of scope for this product
- [ ] T011 Verify IMPACT: Verify acceptance criterion: Given API request and response schemas for appointment booking operations
When they are reviewed against the scope rules
Then no top-level or nested fields are present whose primary purpose is to persist cart or abandoned-cart state
And no example payloads demonstrate cart-like usage patterns
- [ ] T012 Verify IMPACT: Verify acceptance criterion: Given an API consumer in a production-like environment
When they attempt to discover or invoke any cart-related endpoint under the clinic booking base path
Then the API does not expose such endpoints
And there is clear documentation stating that cart and abandoned-cart concepts are not part of the clinic booking product scope