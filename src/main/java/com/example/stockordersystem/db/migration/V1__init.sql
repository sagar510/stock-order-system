CREATE TABLE stocks (
  id            UUID PRIMARY KEY,
  short_code    VARCHAR(16) UNIQUE NOT NULL,
  name          TEXT NOT NULL,
  sector        TEXT,
  created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Orders (append + status changes)
CREATE TYPE order_type AS ENUM ('BUY','SELL');
CREATE TYPE order_status AS ENUM ('PENDING','PARTIALLY_FILLED','FILLED','CANCELED');

CREATE TABLE orders (
  id                 UUID PRIMARY KEY,
  user_id            VARCHAR(64),
  stock_id           UUID NOT NULL REFERENCES stocks(id),
  type               order_type NOT NULL,
  price              NUMERIC(18,8) NOT NULL,
  quantity           INTEGER NOT NULL,
  remaining_quantity INTEGER NOT NULL,
  status             order_status NOT NULL,
  ts_created         TIMESTAMP NOT NULL,
  ts_updated         TIMESTAMP NOT NULL
);
CREATE INDEX idx_orders_stock_status ON orders(stock_id, status);

-- Trades (append-only)
CREATE TABLE trades (
  id               UUID PRIMARY KEY,
  stock_id         UUID NOT NULL REFERENCES stocks(id),
  buy_order_id     UUID NOT NULL REFERENCES orders(id),
  sell_order_id    UUID NOT NULL REFERENCES orders(id),
  quantity         INTEGER NOT NULL,
  execution_price  NUMERIC(18,8) NOT NULL,
  ts_executed      TIMESTAMP NOT NULL
);
CREATE INDEX idx_trades_stock_ts ON trades(stock_id, ts_executed);

-- Search helpers (Phase 3 fallback)
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_stocks_name_trgm ON stocks USING GIN (name gin_trgm_ops);
