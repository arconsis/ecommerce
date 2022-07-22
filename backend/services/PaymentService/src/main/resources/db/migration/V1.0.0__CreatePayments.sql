CREATE TYPE checkout_status AS ENUM (
    'PAYMENT_IN_PROGRESS',
    'PAYMENT_SUCCEED',
    'PAYMENT_FAILED'
);

CREATE TABLE checkouts
(
    checkout_id         UUID PRIMARY KEY,
    user_id             UUID            NOT NULL,
    order_id            UUID            NOT NULL,
    status              checkout_status NOT NULL,
    amount              NUMERIC         NOT NULL,
    currency            VARCHAR         NOT NULL,
    checkout_session_id TEXT UNIQUE,
    checkout_url        TEXT UNIQUE,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

CREATE TABLE payments
(
    payment_id     UUID PRIMARY KEY,
    transaction_id UUID,
    checkout_id    UUID NOT NULL REFERENCES checkouts (checkout_id) on delete cascade,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
)
