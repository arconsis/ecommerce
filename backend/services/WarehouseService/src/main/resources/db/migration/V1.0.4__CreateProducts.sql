CREATE TABLE products
(
    product_id  UUID PRIMARY KEY,
    price       NUMERIC   NOT NULL,
    thumbnail   VARCHAR   NOT NULL,
    category    VARCHAR NOT NULL,
    size        VARCHAR   NOT NULL,
    productName VARCHAR   NOT NULL,
    description VARCHAR   NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
)
