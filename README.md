
# 📈 Stock Order Matching System

## 1. Problem Statement & Approach
The goal of this project is to implement a simplified **stock exchange order matching system** with the following requirements:

- Users can place **BUY** or **SELL** orders for stocks.
- Orders are matched based on:
  - **Price priority**: Highest BUY vs. lowest SELL.
  - **Time/sequence priority**: Earlier orders win if prices are the same.
- Supports **partial matches**  
  (e.g., BUY 1000 vs. SELL 600 → trade 600, leave 400 pending).
- Includes a **search feature** over ~1M stock records:
  - Exact lookup by `id` or `shortCode` (hash map / DB indexed).
  - Prefix search by name (trie-based in-memory snapshot).
- Designed for **scalability**, **clarity**, and **testability**.

### Approach
- **In-memory matching engine**:
  - One **OrderBook per stock**, holding BUY and SELL priority queues.
  - Thread-safe via per-stock lock + sequence numbers for FIFO fairness.
- **Trade Executor**:
  - Generates immutable trade records from matched orders.
- **Search**:
  - Hybrid approach:  
    - Exact lookups → always fresh (direct map/DB index).  
    - Prefix searches → served from in-memory snapshot (rebuilt periodically).
- **API-first design**:
  - REST endpoints for order placement, search, and trade responses.
- **Phased implementation**:
  1. Phase 1: Project scaffolding, domain models.  
  2. Phase 2: Matching engine + order placement API.  
  3. Phase 3: Search feature (mocked snapshot).  
  4. Phase 4: DB persistence (designed, optional in this submission).  

---

📖 **Detailed Developer Documentation**:  
[Developer Documentation – Stock Order Matching System](https://psagarsd.atlassian.net/wiki/external/MDVlYmM4ODFiYWJjNDdiNGFlMDFkYWY3YTE1NzZkN2E)

---

## 3. Prerequisites

* Java 17 installed
* Maven 3.9+
* (Optional) Docker + PostgreSQL if you want to enable DB persistence later

---

## 4. Steps to Run the Project

Run locally:

```bash
mvn spring-boot:run
```

The server will start at `http://localhost:8080`.

### Example APIs

* **Place Order** (POST):

  ```
  POST /api/orders
  {
    "userId": "U1",
    "stockId": "AAPL",
    "type": "BUY",
    "price": 100,
    "quantity": 1000
  }
  ```

* **Search Stocks** (GET):

  ```
  GET /api/stocks/search?query=app&limit=5
  ```

* **Exact Stock Lookup** (GET):

  ```
  GET /api/stocks/AAPL?by=shortCode
  ```

### Example Responses

* A **trade** response includes:

  ```json
  {
    "tradeId": "...",
    "buyOrderId": "...",
    "sellOrderId": "...",
    "stockId": "AAPL",
    "quantity": 600,
    "executionPrice": 100,
    "timestamp": "2025-09-04T12:34:56"
  }
  ```

---

## 5. Explanation of Complex Logic

* **Matching Engine**

  * Uses two priority queues per stock:

    * BUY → max-heap by price, then FIFO by sequence.
    * SELL → min-heap by price, then FIFO by sequence.
  * Matches orders atomically within a per-stock lock.
  * Supports exact & partial matches.

* **Sequence Numbers**

  * Each order gets a global increasing `seq` ID.
  * Ensures **time priority** even when timestamps are identical.

* **Search Snapshot**

  * On startup, builds a snapshot:

    * `byId`: HashMap for O(1) lookups.
    * `byShortCode`: HashMap for O(1) lookups.
    * `nameTrie`: Trie for fast prefix search.
  * Snapshot can be rebuilt periodically for freshness.

---

## 6. Areas Requiring Special Consideration

* **Concurrency**: Without locks, simultaneous order placements could corrupt queues. Fixed with per-stock locking and sequence numbers.
* **Persistence**: Current submission runs in-memory. DB schema, entities, and migration scripts are designed but not wired in this version.
* **Scalability**: Snapshot approach allows 1M+ stock records to be searched efficiently. Prefix results may be slightly stale depending on rebuild frequency.
* **Testing**:

  * ✅ Unit tests for matching engine (exact + partial match).
  * ✅ Unit tests for search feature (exact + prefix).
  * ✅ Integration tests with MockMvc for APIs.

---

## 7. Loom Video

📹 Loom demo link: *\[to be added]*

---

```

---
```
