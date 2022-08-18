BEGIN;
CREATE TYPE order_payment_method_type AS ENUM (
    'STRIPE',
    'CASH_ON_DELIVERY'
    );

ALTER TABLE baskets
    ADD COLUMN psp_token           VARCHAR UNIQUE,
    ADD COLUMN payment_method_type order_payment_method_type;

ALTER TABLE orders
    ADD COLUMN psp_token           VARCHAR NOT NULL UNIQUE,
    ADD COLUMN payment_method_type order_payment_method_type NOT NULL;

COMMIT;