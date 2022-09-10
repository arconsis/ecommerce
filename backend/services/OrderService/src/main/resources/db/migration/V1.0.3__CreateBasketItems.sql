CREATE TABLE basket_items
(
    item_id     UUID PRIMARY KEY,
    product_id  UUID                 NOT NULL,
    basket_id   UUID                 NOT NULL REFERENCES baskets (basket_id) on delete cascade,
    price       NUMERIC              NOT NULL,
    currency    supported_currencies NOT NULL,
    quantity    INTEGER              NOT NULL,
    name        VARCHAR              NOT NULL,
    description VARCHAR              NOT NULL,
    thumbnail   VARCHAR              NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
)
