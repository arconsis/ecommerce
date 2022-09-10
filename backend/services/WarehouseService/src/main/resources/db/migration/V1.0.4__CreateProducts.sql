CREATE TYPE product_width_enum AS ENUM (
    'cm',
    'm'
    );

CREATE TYPE product_weight_enum AS ENUM (
    'lb',
    'kg'
    );

CREATE TABLE products
(
    product_id  UUID PRIMARY KEY,
    price       NUMERIC        NOT NULL,
    thumbnail   VARCHAR        NOT NULL,
    slug        VARCHAR        NOT NULL,
    sku         VARCHAR(8) UNIQUE NOT NULL,
    tags        VARCHAR        NOT NULL,
    name        VARCHAR        NOT NULL,
    description VARCHAR        NOT NULL,
    height      NUMERIC        NULL,
    width       NUMERIC        NULL,
    length      NUMERIC        NULL,
    width_unit   product_width_enum,
    weight      NUMERIC        NOT NULL,
    weight_unit  product_weight_enum,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
)
