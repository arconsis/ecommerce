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
    'REFUNDED',
    'CREATING_SHIPMENT_LABEL_FAILED',
    'SHIPMENT_DELIVERY_FAILED',
    'PREPARING_SHIPMENT'
    );

CREATE TABLE orders
(
    order_id                      UUID PRIMARY KEY,
    user_id                       UUID                 NOT NULL,
--     we set it unique to avoid duplicated orders from same basket
    basket_id                     UUID                 NOT NULL UNIQUE REFERENCES baskets (basket_id) ON DELETE CASCADE,
    status                        order_status         NOT NULL,
    total_price                   NUMERIC              NOT NULL,
    shipping_price                NUMERIC              NOT NULL,
    product_price                 NUMERIC              NOT NULL,
    price_before_tax              NUMERIC              NOT NULL,
    tax                           VARCHAR              NOT NULL,
    external_shipping_provider_id VARCHAR              NOT NULL,
    carrier_account               VARCHAR              NOT NULL,
    shipping_provider_name        VARCHAR              NOT NULL,
    currency                      supported_currencies NOT NULL,
    created_at                    TIMESTAMP,
    updated_at                    TIMESTAMP
);
