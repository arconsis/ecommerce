CREATE TYPE supported_currencies AS ENUM (
    'USD'
    );

CREATE TABLE baskets
(
    basket_id                     UUID PRIMARY KEY,
    user_id                       UUID                 NOT NULL,
    total_price                   NUMERIC              NOT NULL,
    price_before_tax              NUMERIC              NOT NULL,
    shipping_price                NUMERIC              NOT NULL,
    product_price                 NUMERIC              NOT NULL,
    tax                           VARCHAR              NOT NULL,
    currency                      supported_currencies NOT NULL,
    external_shipping_provider_id VARCHAR              NULL,
    carrier_account               VARCHAR              NULL,
    shipping_provider_name        VARCHAR              NULL,
    created_at                    TIMESTAMP,
    updated_at                    TIMESTAMP
)