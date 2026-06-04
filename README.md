# Rewards Points Ledger

A REST API service for managing a customer rewards points program. Members earn points through purchases, referrals, and cashback, and can redeem them against their balance.

## Prerequisites

- [Docker](https://www.docker.com/) and Docker Compose

No local Java or Maven installation required to run the application.

## Quick Start

```bash
./run.sh
```

Or with Make:

```bash
make run
```

The API will be available at **`http://localhost:8083`**.

## Other Commands

```bash
make stop     # Stop all containers
make logs     # Tail application logs
make test     # Run integration tests (requires Java 17 + Maven locally)
make rebuild  # Force rebuild and restart
```

---

## API Reference

### POST /members
Create a new member account.

**Request:**
```json
{
  "name": "Alice Johnson",
  "email": "alice@example.com"
}
```

**Response 201:**
```json
{
  "member_id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "created_at": "2024-01-15T09:00:00Z"
}
```

---

### GET /members/:memberId
Retrieve member info including current points balance.

**Response 200:**
```json
{
  "member_id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "points_balance": 450,
  "created_at": "2024-01-15T09:00:00Z"
}
```

---

### POST /rewards
Create a reward entry. `points` must be a positive number — the system applies the sign based on `point_type_id`.

| point_type_id | Description      | Direction |
|---------------|------------------|-----------|
| 1             | Purchase Earning | Credit    |
| 2             | Referral Bonus   | Credit    |
| 3             | Cashback         | Credit    |
| 4             | Redemption       | Debit     |

**Request:**
```json
{
  "member_id": 1,
  "point_type_id": 1,
  "points": 500,
  "description": "Purchase at Store A"
}
```

**Response 201:**
```json
{
  "reward_id": 1,
  "member_id": 1,
  "point_type_id": 1,
  "points": 500,
  "description": "Purchase at Store A",
  "event_date": "2024-02-01T14:22:10Z"
}
```

---

### GET /members/:memberId/rewards
Retrieve all reward entries for a member, ordered by event date.

**Response 200:**
```json
[
  {
    "reward_id": 1,
    "member_id": 1,
    "point_type_id": 1,
    "points": 500,
    "description": "Purchase at Store A",
    "event_date": "2024-02-01T14:22:10Z"
  }
]
```

---

## Error Responses

```json
{
  "status": 409,
  "error": "DUPLICATE_EMAIL",
  "message": "Member with same email already exists",
  "timestamp": "2024-02-01T14:22:10Z"
}
```

| Status | Error Code             | Cause                                    |
|--------|------------------------|------------------------------------------|
| 400    | `VALIDATION_ERROR`     | Missing or invalid request fields        |
| 400    | `INVALID_POINT_TYPE`   | `point_type_id` not in range 1–4         |
| 404    | `MEMBER_NOT_FOUND`     | `member_id` does not exist               |
| 409    | `DUPLICATE_EMAIL`      | Email already registered                 |
| 422    | `INSUFFICIENT_BALANCE` | Redemption exceeds available balance     |

---

## Running Tests

Tests use Testcontainers — Docker must be running. No separate database needed.

```bash
make test
```

Or directly:

```bash
mvn test
```

---

## Tech Stack

- Java 17 / Spring Boot 2.7
- PostgreSQL 15
- Flyway (schema migrations)
- Docker / Docker Compose
- Testcontainers (integration tests)
