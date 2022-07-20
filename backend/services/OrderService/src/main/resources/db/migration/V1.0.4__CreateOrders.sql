CREATE TYPE order_status AS ENUM (
    'REQUESTED',
    'VALIDATED',
    'OUT_OF_STOCK',
    'PAID',
    'SHIPPED',
    'COMPLETED',
    'PAYMENT_FAILED',
    'CANCELLED',
    'REFUNDED'
);

CREATE TABLE orders
(
    order_id   UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
--     we set it unique to avoid duplicated orders from same basket
    basket_id  UUID         NOT NULL UNIQUE REFERENCES baskets (basket_id) ON DELETE CASCADE,
    status     order_status NOT NULL,
    amount     NUMERIC      NOT NULL,
    currency   VARCHAR      NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
