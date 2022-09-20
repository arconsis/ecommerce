BEGIN;

alter table users
    DROP column password;

COMMIT;