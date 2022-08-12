CREATE TABLE baskets
(
    basket_id        UUID PRIMARY KEY,
    user_id          UUID    NOT NULL,
    total_price      NUMERIC NOT NULL,
    price_before_tax NUMERIC NOT NULL,
    tax              VARCHAR NOT NULL,
    currency         VARCHAR NOT NULL,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
)
