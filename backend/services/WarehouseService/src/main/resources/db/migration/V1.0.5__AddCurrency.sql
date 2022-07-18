BEGIN;

alter table products
    add column currency varchar not null;

COMMIT;