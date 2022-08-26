BEGIN;

alter table checkouts
    add column payment_error_code varchar null;

COMMIT;