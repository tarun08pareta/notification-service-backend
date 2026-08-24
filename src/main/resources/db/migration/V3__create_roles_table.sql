CREATE TABLE roles
(
    id   UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Initial platform roles.
INSERT INTO roles (id, name)
VALUES (gen_random_uuid(), 'ADMIN'),
       (gen_random_uuid(), 'USER');