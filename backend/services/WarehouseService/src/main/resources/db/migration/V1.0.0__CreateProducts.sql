BEGIN;

CREATE TYPE product_width_enum AS ENUM ('cm', 'm');

CREATE TYPE product_weight_enum AS ENUM ('lb', 'kg');

CREATE TABLE products
(
    product_id  UUID PRIMARY KEY,
    price       NUMERIC           NOT NULL,
    slug        VARCHAR           NOT NULL,
    sku         VARCHAR(8) UNIQUE NOT NULL,
    tags        VARCHAR           NOT NULL,
    name        VARCHAR           NOT NULL,
    description VARCHAR           NOT NULL,
    height      NUMERIC           NULL,
    width       NUMERIC           NULL,
    length      NUMERIC           NULL,
    width_unit  product_width_enum,
    weight      NUMERIC           NOT NULL,
    weight_unit product_weight_enum,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TYPE product_media_type AS ENUM ('image', 'video');

CREATE TABLE product_media
(
    media_id   UUID PRIMARY KEY,
    product_id UUID               NOT NULL REFERENCES products (product_id) ON DELETE CASCADE,
    original   VARCHAR            NOT NULL,
    thumbnail  VARCHAR            NOT NULL,
    type       product_media_type NOT NULL,
    is_primary BOOLEAN            NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

COMMIT;

