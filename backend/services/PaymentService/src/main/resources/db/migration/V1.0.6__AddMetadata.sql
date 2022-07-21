BEGIN;

alter table checkout_events
    drop column payload,
    add column metadata text not null,
    add column type varchar not null,
--     idempotency_key: we use this constraint to avoid write duplicated checkout events
    ADD CONSTRAINT unique_checkout_type_per_payment UNIQUE(type, payment_id);

COMMIT;