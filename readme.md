# USSD Banking Service

> A robust, Spring Boot-based USSD application designed for high-concurrency banking transactions. This project provides a seamless interface for account management, deposits, and withdrawals using a stateless architecture.

---

## 🚀 Features

- **USSD Menu Navigation:** A multi-level state machine designed for gateways like AfricasTalking or Hover.
- **Secure Transactions:**
    - **BCrypt Hashing:** User PINs are never stored in plain text; they are hashed using BCrypt with a strength of 12.
    - **Transactional Integrity:** Critical operations use Spring's `@Transactional` to ensure atomicity during balance updates.
- **Distributed Session Management:** USSD state is decoupled from the application logic using Redis, allowing for a stateless application tier.
- **Real-time Alerts:** Integrated Twilio SMS SDK for instant transaction notifications.
- **Containerized Environment:** Fully orchestrated using Docker Compose for easy deployment and environment parity.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 22, Spring Boot 3.x |
| Database | MySQL 8.0 (Persistent storage) |
| Caching/State | Redis (Session tracking) |
| Security | Spring Security (Crypto module) |
| Communication | Twilio API |
| Infrastructure | Docker, Docker Compose |

---

## 🏗 System Architecture & Scalability

Scalability was prioritized during the initial design phase to handle the high-burst nature of USSD traffic.

### 1. Stateless Application Tier

By offloading session states to Redis, the `ussd-app` remains stateless. This allows the application to be horizontally scaled (adding more instances) behind a Load Balancer without losing the user's progress in a menu.

### 2. High-Performance State Management

USSD gateways typically have strict timeout requirements (often < 10 seconds). Using Redis for session lookups ensures sub-millisecond latency for state transitions, keeping the user experience snappy.

### 3. Asynchronous Potential

While current SMS notifications are synchronous, the architecture is prepared to move toward an **Event-Driven model** (using a Message Broker like RabbitMQ or Kafka) to prevent SMS API latency from blocking the USSD response.

---

## 🚦 Getting Started

### Prerequisites

- Docker and Docker Compose
- Maven 3.9+
- Twilio Account (SID, Auth Token, and a Verified Number)

### Setup & Installation

**1. Clone the Repository:**

```bash
git clone https://github.com/your-username/ussd-banking.git
cd ussd-banking
```

**2. Environment Configuration:**

Ensure your Twilio credentials are set in your environment or updated in the `docker-compose.yml`:

```
TWILIO_SID
TWILIO_AUTH
TWILIO_NUMBER
```

**3. Build the JAR:**

```bash
mvn clean package -DskipTests
```

**4. Launch via Docker:**

```bash
docker-compose up --build
```

---

## 🧪 Testing the USSD Flow

Since USSD is essentially a series of POST requests, you can simulate the flow using `curl` or Postman.

### USSD Request Simulation Table

| Action | `text` Parameter | Description |
|---|---|---|
| Initial Dial | `""` (Empty) | Shows the Main Menu |
| Select Create Account | `"1"` | Prompts for a 4-digit PIN |
| Submit PIN | `"1*1234"` | Creates account and sends SMS |
| Check Balance | `"4*1234"` | Verifies PIN and returns balance |

### Example cURL Command

```bash
curl -X POST http://localhost:8080/ussd \
  -d "sessionId=sess_999" \
  -d "phoneNumber=+2348000000000" \
  -d "serviceCode=*384#" \
  -d "text=1*5555"
```

---

## 🔒 Security Summary

- **Data Protection:** Sensitive fields like `hashed_pin` are stored using BCrypt.
- **Precision:** Financial data uses `BigDecimal` with a scale of 4 to prevent precision loss inherent in `Double` or `Float`.
- **Input Sanitization:** All USSD inputs are trimmed and validated via Regex before processing.