CREATE TABLE checkout_events
(
    event_id         UUID PRIMARY KEY,
    checkout_id      UUID    NOT NULL REFERENCES checkouts (checkout_id) on delete cascade,
    psp_reference_id VARCHAR NOT NULL,
    psp_data         TEXT    NOT NULL,
    type             VARCHAR NOT NULL,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

ALTER TABLE checkout_events
    --     idempotency_key: we use this constraint to avoid write duplicated checkout events
    ADD CONSTRAINT unique_checkout_type_per_payment UNIQUE (type, checkout_id);

