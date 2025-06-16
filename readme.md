# 🔗 LLD - URL Shortener (Spring Boot)

This project is a lightweight and scalable **URL shortener** application similar to services like Bit.ly or TinyURL. Built using **Spring Boot**, it follows clean architecture principles, uses **Caffeine Cache**, and supports features like custom aliases, validation, and analytics.

---

## 📁 Project Structure <WIP>

```

src/main/java/com/syscho/lld/urlShortener
│
├── config              → Configuration classes (Cache, Swagger)
├── dao
│   └── entity          → JPA Entity classes
│   └── UrlRepository   → Spring Data repository
│
├── exception           → Global exception handling
│
├── utils               → Utility classes (e.g., short code generator)
│
├── url
│   ├── mapper          → Mappers between entities and DTOs
│   ├── model           → DTOs (Request and Response classes)
│   ├── service         → Core business logic
│   ├── validator       → Validation logic
│   └── RedirectController → REST controller handling URL redirect
|   └── UrlController → REST controller handling URL Shorten

````

---

## 🧩 Core Class Responsibilities

### 🔹 `RedirectController`
- REST controller to handle:
  - POST `/shorten` — shorten a URL
  - GET `/{code}` — redirect to the original URL
  - GET `/analytics/{code}` — get hit count and metadata

### 🔹 `UrlService`
- Core business logic:
  - Create short URL
  - Handle redirection
  - Fetch analytics
  - Save to DB and cache

### 🔹 `ShortCodeGenerator`
- Generates a unique alphanumeric code
- Ensures collision resistance

### 🔹 `CacheConfig`
- Configures **Caffeine Cache** to cache short URL mappings
- Reduces DB hits and improves performance

### 🔹 `UrlRepository`
- JPA repository interface to perform CRUD operations on `UrlMappingEntity`

### 🔹 `UrlMapper`
- Maps between `UrlRequest`, `UrlResponse`, and the entity

### 🔹 `UrlValidatorService`
- Validates:
  - Input URL format
  - Custom alias (if provided)
  - Expiry rules

### 🔹 `UrlRequest`
- DTO representing the request payload:
  ```json
  {
    "originalUrl": "https://example.com",
    "customAlias": "myAlias",
    "expiryInMinutes": "2025-06-20T12:00:00",
    "length":1,
    "password":"password"
  }

### 🔹 `UrlResponse`

* DTO returned after shortening:

  ```json
  {
    "shortUrl": "http://localhost:8080/myAlias",
    "originalUrl": "https://example.com",
    "expiryTime": "2025-06-20T12:00:00",
    "clickCount": 0,
    "active": true
  }
  ```

### 🔹 `UrlAnalyticsResponse`

* Returns stats like click count, created time, and expiry info

---

## 🧬 Flow Overview

### 🔁 Shortening a URL

1. `POST /shorten` with `UrlRequest`
2. Controller calls `UrlValidatorService` for validation
3. `UrlService` checks for custom alias or generates one
4. Entity is saved to DB + cache via `UrlRepository`
5. Returns `UrlResponse` with short URL

---

### 🔄 Redirecting a Short URL

1. `GET /{shortCode}`
2. Lookup cache first (Caffeine)
3. If not found, check DB
4. If expired, return 404 or custom message
5. Redirect (302) to original URL
6. Update analytics (click count, last accessed)

---

### 📊 Analytics

* `GET /analytics/{shortCode}` returns:

    * Total hits
    * Creation and expiry time
    * Original and shortened URLs

---

## 🧪 Sample Request

### Shorten a URL

```bash
curl -X POST http://localhost:8080/shorten \
-H "Content-Type: application/json" \
-d '{
  "originalUrl": "https://github.com/ps1437/lld-url-shortner",
  "customAlias": "gh-url",
  "expiryTime": "2025-06-30T23:59:59"
}'
```

---

## ✅ Features

* 🔐 Custom alias support
* ⏳ Expiry support
* ⚡ In-memory cache (Caffeine)
* 🧼 Validation (malformed URL, duplicate alias)
* 📈 Analytics tracking
* 🧪 Swagger UI integration for easy testing
* 🧱 Clean architecture and SOLID principles
* 🚦 Rate limiting per shortened URL (via Bucket4j)
* 🔒 Password-Protected Links
* 📦 QR Code Generation (based in isQrNeeded flag)

---

## 🚀 Run the Project

```bash
./mvnw spring-boot:run
```

* Access Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📌 Future Enhancements

* Admin dashboard
* Redis for distributed caching
* PostgreSQL or MongoDB integration

---

## 🧑‍💻 Author

* GitHub: [ps1437](https://github.com/ps1437)
