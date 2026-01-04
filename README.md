# 🛡️ Intelligent API Rate-Limiting & Abuse Detection Engine

### 🚀 Overview
This project implements a production-grade, quota-based rate-limiting and abuse-prevention engine for backend APIs.  
Unlike simple request counters, this system supports:

- Endpoint-specific policies
- Progressive punishment (Throttle → Temp Block → Hard Block)
- Fair cooldown-based forgiveness
- Human-friendly behavior (no surprise hard blocks)
- Deterministic, test-driven enforcement

The engine is integrated into a Spring Boot application using a custom servlet filter and is fully covered by integration tests.


---

### ❓ Why this project is different
Most rate-limiting demos:
- Only count requests
- Have no escalation
- Permanently punish users
- Ignore user experience

This project focuses on real-world API behavior, balancing:
- Security
- Fairness
- Predictability
- Recoverability

This is how actual production APIs (Stripe, GitHub, AWS) behave.

---

### 🧠 Core Concepts

#### 1️⃣ Quota-Based Rate Limiting
Each endpoint has:
- A maximum number of requests
- A rolling time window

#### 2️⃣ Progressive Enforcement States

| State       | HTTP | Meaning                          |
|-------------|------|----------------------------------|
| ALLOW       | 200  | Normal traffic                   |
| THROTTLE    | 429  | Soft back-pressure               |
| TEMP_BLOCK  | 429  | Cooldown period                  |
| HARD_BLOCK  | 403  | Punishment for sustained abuse   |

---

### 📐 Rate-Limit Policy (Current Configuration)

| Endpoint | Max Requests | Window | TEMP_BLOCK | HARD_BLOCK |
|----------|--------------|--------|------------|------------|
| /login   | 5            | 1 min  | 60 sec     | 10 min     |
| /orders  | 10           | 1 min  | 60 sec     | 10 min     |
| /search  | 30           | 1 min  | 10 sec     | 5 min      |

Policies are endpoint-aware and isolated.

---

### 🔁 Enforcement Flow (High Level)  

```bash 
Request ↓ RateLimitFilter ↓ BehaviorProfile (request history) ↓ EnforcementsService ├─ Active block? → enforce ├─ Quota exceeded? → block └─ Otherwise → allow
```

---

### ⚖️ Fairness Rules (Important)
- TEMP_BLOCK retries do **not** immediately escalate
- Users see a decreasing timer
- Escalation happens only after clear repeated abuse
- All blocks expire automatically
- No server restart is ever required

This prevents:
- Accidental hard blocks
- Permanent punishment
- Poor user experience

---

### 🧪 Testing Strategy
The project uses Spring Boot integration tests with MockMvc to validate:
- Normal traffic is allowed
- TEMP_BLOCK triggers correctly
- HARD_BLOCK escalation works
- Timers decrement correctly
- Forgiveness after cooldown
- Endpoint isolation
- No punishment leakage

All tests reset application state using:
```java
@DirtiesContext(AFTER_EACH_TEST_METHOD)
```

### 📂 Project Structure
```bash
    src/main/java
    ├─ engine
    │   ├─ behaviour      // Request tracking & sliding window
    │   ├─ decision       // Decision model (ALLOW, BLOCK, etc.)
    │   ├─ enforcement    // Core enforcement logic
    │   ├─ policy         // Rate-limit rules
    │   ├─ violation      // Violation storage & lifecycle
    │   └─ model          // Request context & types
    ├─ filter             // RateLimitFilter
    └─ controller         // Demo endpoints
    
    src/test/java
    └─ RateLimitIntegrationTest
```

### 🧠 Design Decisions
- Quota-based over heuristic scoring → deterministic & testable
- State-machine enforcement → predictable behavior
- Time-bounded violations → automatic recovery
- Idempotent HARD_BLOCK → no timer reset abuse
- Integration tests over unit tests → realistic validation

### 🔧 Tech Stack
- Java 17
- Spring Boot
- Servlet Filters
- JUnit 5
- MockMvc
- In-memory stores (extensible to Redis)

### 🔮 Future Enhancements
- Redis-backed stores
- Distributed rate limiting
- Configuration via YAML
- Metrics (Prometheus)
- Admin dashboards

### 🏁 How to Run
```bash
>>> mvn clean spring-boot:run
```
Test endpoints:
- GET /login
- GET /orders
- GET /search

### 👤 Author
Sunny John Balla
Backend Engineer | Java | Systems Design
GitHub: [JohnSunny](https://github.com/JohnSunny21)
