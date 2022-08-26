BEGIN;

alter table checkouts
    rename column status to payment_status;

alter table checkouts
    add column payment_error_message varchar null;

COMMIT;