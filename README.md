# Backend Engineer Take-Home Challenge

## Challenge: URL Shortener Service

### Overview
Build a simple URL shortening service API that allows users to:
1. Submit a long URL and receive a shortened URL
2. Access the original URL by visiting the shortened URL
3. View analytics for shortened URLs

### Time Limit
This challenge is designed to be completed in approximately 2 hours.

### Requirements

#### Core Features (Must Have)
1. **Create Short URL** - `POST /api/shorten`
    - Accept a JSON payload with a long URL
    - Generate a unique short code (6-8 characters)
    - Return the shortened URL
    - Example request:
      ```json
      {
        "url": "https://www.example.com/very/long/path/to/page"
      }
      ```
    - Example response:
      ```json
      {
        "short_url": "http://localhost:8080/abc123",
        "short_code": "abc123",
        "original_url": "https://www.example.com/very/long/path/to/page",
        "created_at": "2024-01-15T10:30:00Z"
      }
      ```

2. **Redirect** - `GET /{shortCode}`
    - Redirect to the original URL with HTTP 301
    - Track visit count

3. **Get URL Stats** - `GET /api/stats/{shortCode}`
    - Return statistics about the shortened URL
    - Example response:
      ```json
      {
        "short_code": "abc123",
        "original_url": "https://www.example.com/very/long/path/to/page",
        "created_at": "2024-01-15T10:30:00Z",
        "visit_count": 42
      }
      ```

#### Bonus Features (Nice to Have)
- Custom short codes (allow users to specify their own)
- Expiration time for URLs
- Basic rate limiting
- List all shortened URLs - `GET /api/urls`

### Technical Requirements
1. Use any of these languages: Python, Java, or Go
2. Include proper error handling
3. Validate URLs before shortening
4. Use appropriate HTTP status codes
5. Include at least 3 unit tests
6. Data can be stored in-memory (no database required)

### Deliverables
1. Source code
2. `README.md` with setup and run instructions
3. `SOLUTION.md` explaining your approach
4. Any necessary configuration or dependency files

### Evaluation Criteria
- Code quality and organization
- API design and RESTful principles
- Error handling and edge cases
- Testing approach
- Documentation clarity
- Performance considerations

### Example Scenarios to Handle
1. Invalid URL format
2. Duplicate URL submissions (return existing short code or create new?)
3. Non-existent short codes
4. Empty request bodies
5. Very long URLs (>2048 characters)

### Getting Started
Choose your preferred language and framework:
- **Python**: Flask, FastAPI, or Django REST
- **Java**: Spring Boot, Spark Java, or Javalin
- **Go**: Gin, Echo, or standard library

Focus on clean, production-ready code rather than over-engineering. We value simplicity and correctness.

---

## Submission
Please submit your solution as a ZIP file or GitHub repository link. Ensure all files are included and the project can be run with minimal setup.
