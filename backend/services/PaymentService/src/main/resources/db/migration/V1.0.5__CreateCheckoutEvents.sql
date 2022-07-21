CREATE TABLE checkout_events
(
    event_id   UUID PRIMARY KEY,
    payment_id UUID NOT NULL REFERENCES payments (payment_id) on delete cascade,
    payload    TEXT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
