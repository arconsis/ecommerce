ALTER TABLE payments
    DROP column transaction_id,
    add column psp_reference_id VARCHAR NOT NULL UNIQUE;

