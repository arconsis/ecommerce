CREATE TABLE inventory
(
    inventory_id    UUID PRIMARY KEY,
    product_id      UUID NOT NULL UNIQUE REFERENCES products (product_id) ON DELETE CASCADE,
    stock           INTEGER NOT NULL CHECK ( stock >= 0 ),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
)
