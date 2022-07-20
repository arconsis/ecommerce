CREATE TABLE baskets
(
    basket_id  UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
    amount     NUMERIC      NOT NULL,
    currency   VARCHAR      NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
