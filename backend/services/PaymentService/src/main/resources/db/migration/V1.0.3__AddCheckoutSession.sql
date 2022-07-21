BEGIN;

alter table payments
    add column checkout_session_id text not null unique,
    add column checkout_url text not null unique;

COMMIT;