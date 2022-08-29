BEGIN;

alter table products
    add column currency supported_currencies not null;

COMMIT;