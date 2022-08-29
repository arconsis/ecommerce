CREATE TYPE shipment_status AS ENUM (
    'PREPARING_SHIPMENT',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED',
    'CREATING_SHIPMENT_LABEL_FAILED',
    'DELIVERY_FAILED'
    );

CREATE TYPE supported_currencies AS ENUM (
    'USD'
    );

CREATE TABLE shipments
(
    shipment_id                   UUID PRIMARY KEY,
    external_shipment_id          VARCHAR              NULL,
    shipment_failure_reason       VARCHAR              NULL,
    external_shipment_provider_id VARCHAR              NOT NULL,
    shipment_provider_name        VARCHAR              NOT NULL,
    price                         NUMERIC              NOT NULL,
    currency                      supported_currencies NOT NULL,
    order_id                      UUID                 NOT NULL UNIQUE,
    user_id                       UUID                 NOT NULL,
    status                        shipment_status      NOT NULL,
    created_at                    TIMESTAMP,
    updated_at                    TIMESTAMP
)
