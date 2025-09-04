
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
````markdown
### 6.1 Place Order

Below are two realistic flows that demonstrate **exact match** and **partial match** behavior.  
(Prices are per share; matching uses price-time priority.)

---

#### A) Exact Order Match

**Step 1 — Place BUY**
```http
POST /api/orders
Content-Type: application/json

{
  "type": "BUY",
  "stock_id": "AAPL",
  "price": 100,
  "quantity": 1000
}
````

**Response**

```json
{
  "order_id": "ORD_BUY_1",
  "status": "PENDING",
  "remaining_quantity": 1000
   "trades": []
}
```

**Step 2 — Place SELL (same price & quantity)**

```http
POST /api/orders
Content-Type: application/json

{
  "type": "SELL",
  "stock_id": "AAPL",
  "price": 100,
  "quantity": 1000
}
```

**Response**

```json
{
  "order_id": "ORD_SELL_1",
  "status": "FILLED",
  "remaining_quantity": 0,
   "trades": [
        {
            "tradeId": "9869b0cf-50a9-41c4-a04d-5cccc81ac792",
            "buyOrderId": "32e634ae-8f6b-46ae-a545-2704de2a87a7",
            "sellOrderId": "c676c320-3a09-4117-9312-b87d28daf9d2",
            "stockId": "AAPL",
            "quantity": 1000,
            "executionPrice": 100,
            "timestamp": "2025-09-04T23:11:21.6677186"
        }]
}
```

> Result: The SELL matches the prior BUY **completely** at 100.
> The earlier BUY will also now be **FILLED** with `remaining_quantity = 0`.

---

#### B) Partial Order Match

**Step 1 — Place BUY**

```http
POST /api/orders
Content-Type: application/json

{
  "type": "BUY",
  "stock_id": "AAPL",
  "price": 100,
  "quantity": 1000
}
```

**Response**

```json
{
  "order_id": "ORD_BUY_2",
  "status": "PENDING",
  "remaining_quantity": 1000
}
```

**Step 2 — Place SELL (less quantity at same price)**

```http
POST /api/orders
Content-Type: application/json

{
  "type": "SELL",
  "stock_id": "AAPL",
  "price": 100,
  "quantity": 600
}
```

**Response**

```json
{
  "order_id": "ORD_SELL_2",
  "status": "FILLED",
  "remaining_quantity": 0
}
```

> Result: A **partial trade of 600** executes at 100.
> The BUY order now has `remaining_quantity = 400` and status **PARTIALLY\_FILLED** (still open in the book).

---

**Notes**

* `status` transitions: `PENDING` → `PARTIALLY_FILLED` → `FILLED` (or `CANCELED` if supported).
* Matching uses **highest BUY vs lowest SELL**; ties use **earlier order first** (sequence-based FIFO).
* Execution price equals the **resting order’s price** in this design (common matching convention).

```

If you want, I can also add a tiny “Troubleshooting” tip right under this—e.g., what you’ll see if prices don’t cross (no match, order remains pending).
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

## 7. Loom Videos

1. 📹 [Requirement Understanding](https://www.loom.com/share/e867a8c64f08496a89c2f2aade919d0e)
2. 📹 [Demo](https://www.loom.com/share/2b0ecc66d8944dc6a29fe93f5de7f8d1)
3. 📹 [Approach & Trade-Offs](https://www.loom.com/share/ccd58b3b8dc94992b764ca84653e690b)

```

---
```
