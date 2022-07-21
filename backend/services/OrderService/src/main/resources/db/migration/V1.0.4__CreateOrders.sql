CREATE TYPE order_status AS ENUM (
    'REQUESTED',
    'VALIDATED',
    'OUT_OF_STOCK',
    'PAYMENT_IN_PROGRESS',
    'PAID',
    'SHIPPED',
    'COMPLETED',
    'PAYMENT_FAILED',
    'CANCELLED',
    'REFUNDED'
);

CREATE TABLE orders
(
    order_id            UUID PRIMARY KEY,
    user_id             UUID         NOT NULL,
--     we set it unique to avoid duplicated orders from same basket
    basket_id           UUID         NOT NULL UNIQUE REFERENCES baskets (basket_id) ON DELETE CASCADE,
    status              order_status NOT NULL,
    amount              NUMERIC      NOT NULL,
    currency            VARCHAR      NOT NULL,
    checkout_session_id TEXT         UNIQUE,
    checkout_url        TEXT         UNIQUE,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

ALTER TABLE orders
    ADD CONSTRAINT checkout_not_null_payment_in_progress
        CHECK ( status != 'PAYMENT_IN_PROGRESS'::order_status OR checkout_session_id IS NOT NULL OR checkout_url IS NOT NULL);
