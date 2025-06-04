# URL Shortner Solution Documentation

## Approach
I approached this challenge by first breaking down the core features into small, manageable components and mapping each to a REST endpoint. My goal was to implement a functional, clean, and well-tested URL shortener service within the given time frame. I chose Java with Spring Boot because of its robustness, built-in support for REST APIs, and my personal familiarity with it.

I started with basic endpoint scaffolding, built the core logic to generate short URLs, then progressively added support for redirection, statistics, and extra features like expiration and rate limiting.
## Architecture Decisions

### URL Shortening Algorithm
I opted for a simple random alphanumeric string generator to create unique short codes (6-8 characters). This approach reduces the chance of collision while keeping the code readable. The codes are generated using a random pool of characters and checked against the in-memory map to ensure uniqueness.

### Data Storage
Since persistent storage wasn't required, I used a `ConcurrentHashMap<String, UrlMapping>` to store all shortened URLs keyed by their short codes. This offers thread safety and fast lookups, which are critical even for an in-memory system.

### API Design
I followed REST principles closely, using meaningful HTTP methods:
- POST : `api/shorten` for creating short URLs
- GET : `/{shortCode}` for redirecting
- GET : `/api/stats/{shortCode}` for analytics
- GET : `/api/urls` to list all URLs

Each endpoint returns well-structured JSON with appropriate status codes (201, 200, 301, 404, 413, etc.).


## Key Components

### UrlService
- Purpose: Core business logic for URL shortening, redirection, stats, and listing.
- Implementation details: Validates URLs, generates short codes, handles expiration, tracks visits, and stores data in memory.

### RateLimiterService
- Purpose: Limits total API requests to prevent abuse.
- Implementation details: Implemented a sliding window algorithm with a synchronized queue to allow a max of 100 requests per minute across all endpoints.

### UrlController
- Purpose: Exposes REST APIs for users
- Implementation details: Delegates logic to UrlService, handles exceptions and enforces request size constraints.

### RateLimitFilter
- Purpose: Applies rate limiting globally across all endpoints.
- Implementation details:Checks rate before passing request down the chain; rejects excessive requests with HTTP 429.

## Trade-offs and Assumptions

### Trade-offs
- **In-memory Storage v/s Persistent Database:** I chose to use an in-memory data store `(ConcurrentHashMap)` to meet the requirements of the assignment and to keep the solution simple and self-contained. This made development faster and eliminated external dependencies. However, the trade-off is that all data is lost on application restart and scalability is limited to a single JVM instance. In a production system, I’d switch to a persistent store like PostgreSQL or Redis for durability, clustering and data retention.

- **Random Short Code Generation v/s Hashing or Sequential IDs:** I used a random alphanumeric string generator to create short codes. This avoids predictability and reduces the risk of sequential code guessing or abuse. The downside is that it lacks determinism ie, the same long URL submitted multiple times can result in different short codes unless explicitly handled. In a real system, I'd consider hash-based IDs (e.g. base62-encoded hashes) to ensure idempotency for duplicate URLs or support configurable behavior.

- **Global Rate Limiting v/s Per-User/IP Rate Limiting:** To keep things simple and due to lack of user details, I implemented a global request rate limiter that applies to all incoming traffic. While this protects the service from being overwhelmed, it may unfairly penalize legitimate users during high traffic periods. A more scalable approach would be to track request rates per user or per IP, possibly using a distributed cache like Redis.

- **Expiration Logic on Read v/s Background Cleanup:** Expired short URLs are checked and rejected at the time of retrieval, but they are not actively removed from memory. This is efficient for now but could lead to memory bloat over time. In a production setting, a periodic cleanup task (e.g., using a scheduler) would be preferable.
 
### Assumptions
- **Unique short code for each request:** I assumed that unless a custom short code is provided, each request — even for the same long URL — will result in a new short code. This matches how many URL shorteners behave and simplifies caching logic.

- **Valid URLs are absolute and properly formatted:** I used basic Java validation to check if the submitted URL is well-formed. I'm assuming the URL is syntactically correct and that no authentication or deep validation is needed (e.g., checking if the URL is live or safe).

- **Global rate limit of 100 requests per minute is sufficient:** I assumed this rate limit would be enough to simulate basic DoS protection for the purposes of this assessment. It's hardcoded for now but could be configurable in a real-world app.

## Performance Considerations
While building this service, I aimed for responsiveness and efficiency, even though it’s a simple, in-memory solution. Here are a few things I considered from a performance perspective:

- **Fast Lookups with `ConcurrentHashMap` :** For storing URL mappings and their metadata, I used a `ConcurrentHashMap`. This allows O(1) access time for most operations and is thread-safe by default, ensuring performance under concurrent access without requiring manual synchronization. It’s lightweight and well-suited for this kind of in-memory cache.
- **Short Code Generation with Minimal Collision Risk:** I used a random 6–8 character alphanumeric code. The character pool (a–z, A–Z, 0–9) offers a large number of permutations, reducing the probability of collisions. However, I still added a check to ensure uniqueness, which does involve a small trade-off in performance for safety. In this limited scope, the performance impact is negligible.
- **Minimal Synchronized Operations in Rate Limiting:** For the global rate limiter, I used a sliding window with a synchronized queue to store timestamps. While synchronization introduces some contention, the critical section is small and quick, minimizing blocking. In a high-throughput environment, this could be improved with non-blocking algorithms or an external rate limiter like Redis.
- **Lightweight DTOs and Serialization:** All DTOs are minimal and use Jackson for JSON serialization. I avoided deep object graphs or excessive nesting to keep serialization and deserialization fast and lean.
- **No Cleanup Mechanism for Expired URLs:** I implemented expiration checks at runtime, expired URLs are not removed from memory. Over time, this could affect memory usage. In a production system, I’d consider using scheduler service cleanup to avoid unbounded memory growth.
## Security Considerations
1. Validates that URLs are well-formed before accepting them.
2. Rejects long URLs over 2048 characters to avoid abuse.
3. Prevents DoS-style attacks using global rate limiting.

## Future Improvements
If I had more time, I would:
1. Add persistence with Redis or a relational database.
2. Implement per-IP rate limiting instead of global.
3. Add support for analytics over time (e.g., visits per day, week or month etc..)

## Challenges Faced
- **Balancing Scope with Time :** One of the main challenges was balancing the scope of the solution with the time frame constraint. There were several “nice-to-have” features listed and I had to make quick decisions about which ones to include without compromising on the core requirements or code quality. For example, I initially considered implementing per-IP rate limiting but decided to stick with a global rate limiter to stay within scope.
- **Designing a Clean Flexible Structure :** I wanted the architecture to be clean and modular while also being flexible enough to extend later. Finding the right balance between keeping things simple and avoiding tightly coupled code was a bit tricky. I spent some time reorganizing service and model classes to ensure separation of concerns and testability.
- **Ensuring Correct Expiration Handling :** Implementing expiration was straightforward in concept but required careful handling during lookups and redirection. I had to make sure expired URLs were gracefully rejected without affecting statistics and that the logic remained efficient.
- **Testing Edge Cases Quickly :** Writing meaningful unit tests in a short time frame was challenging, especially for edge cases like overly long URLs, expired links, or non-existent short codes. I prioritized writing a few high-value test cases that would cover the most critical behaviors.
- **Rate Limiting Without External Tools :** Implementing global rate limiting without Redis or an external library was a bit of a challenge. I chose to build a simple sliding window rate limiter using Java’s built-in data structures. It works well for the demo, but it was a bit tricky to design the algorithm to be both thread-safe and reasonably efficient under concurrent load.

## Time Breakdown
- Setup and planning: 15 minutes
- Core implementation:  65 minutes
- Testing: 20 minutes
- Documentation: 20 minutes
- Total: ~2 hours
