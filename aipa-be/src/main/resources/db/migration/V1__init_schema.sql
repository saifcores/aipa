-- AIPA schema: customers & payment transactions (read-focused demo)

CREATE TABLE customers (
    id          UUID PRIMARY KEY,
    firstname   VARCHAR(100) NOT NULL,
    lastname    VARCHAR(100) NOT NULL,
    phone       VARCHAR(30)  NOT NULL,
    email       VARCHAR(180) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX uk_customers_phone ON customers (phone) WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX uk_customers_email ON customers (email) WHERE is_deleted = FALSE;

CREATE TABLE transactions (
    id           UUID PRIMARY KEY,
    reference    VARCHAR(40)    NOT NULL,
    amount       NUMERIC(18, 2) NOT NULL CHECK (amount > 0),
    currency     VARCHAR(3)     NOT NULL DEFAULT 'XOF',
    provider     VARCHAR(32)    NOT NULL,
    status       VARCHAR(20)    NOT NULL,
    error_code   VARCHAR(32),
    customer_id  UUID           NOT NULL REFERENCES customers (id),
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    is_deleted   BOOLEAN        NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX uk_transactions_reference
    ON transactions (reference) WHERE is_deleted = FALSE;

CREATE INDEX idx_transactions_status ON transactions (status);
CREATE INDEX idx_transactions_provider ON transactions (provider);
CREATE INDEX idx_transactions_created_at ON transactions (created_at);
CREATE INDEX idx_transactions_customer_id ON transactions (customer_id);
CREATE INDEX idx_transactions_status_created
    ON transactions (status, created_at);
