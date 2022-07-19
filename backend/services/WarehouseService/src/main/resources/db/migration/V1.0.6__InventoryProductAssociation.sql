BEGIN;

alter table inventory
    DROP column product_id,
    add column product_id UUID NOT NULL UNIQUE references products(product_id) on delete cascade;

COMMIT;