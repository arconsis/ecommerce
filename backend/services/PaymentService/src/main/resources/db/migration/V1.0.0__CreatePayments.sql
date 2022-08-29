CREATE TYPE checkout_status AS ENUM (
    'PAYMENT_IN_PROGRESS',
    'PAYMENT_SUCCEED',
    'PAYMENT_FAILED'
    );

CREATE TYPE supported_currencies AS ENUM (
    'USD'
    );

CREATE TABLE checkouts
(
    checkout_id UUID PRIMARY KEY,
    user_id     UUID                 NOT NULL,
    order_id    UUID                 NOT NULL,
    status      checkout_status      NOT NULL,
    amount      NUMERIC              NOT NULL,
    currency    supported_currencies NOT NULL,
    psp_token   TEXT UNIQUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);