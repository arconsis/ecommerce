CREATE TABLE order_items
(
    item_id    UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    order_id   UUID NOT NULL REFERENCES orders (order_id) on delete cascade,
    price      NUMERIC NOT NULL,
    currency   VARCHAR NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
