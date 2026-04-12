-- H2-compatible DDL (no PostgreSQL ENUM types; use VARCHAR instead)

CREATE TABLE IF NOT EXISTS channels
(
    name                   VARCHAR(32) PRIMARY KEY,
    type                   VARCHAR(10)  NOT NULL,
    created_at             BIGINT       NOT NULL,
    is_archived            BOOLEAN      NOT NULL DEFAULT FALSE,
    max_message_length     INT          NOT NULL CHECK (max_message_length > 0),
    max_members            INT          NOT NULL CHECK (max_members > 0),
    is_read_only           BOOLEAN      NOT NULL DEFAULT FALSE,
    last_message_timestamp BIGINT
);

-- Seed data for benchmarking
INSERT INTO channels (name, type, created_at, is_archived, max_message_length, max_members, is_read_only, last_message_timestamp)
VALUES ('General',            'PUBLIC',  1707720000, FALSE, 500,  100, FALSE, 1707722600),
       ('Development',        'PRIVATE', 1707720001, FALSE, 1000,  50, FALSE, 1707722500),
       ('Support',            'PUBLIC',  1707720002, FALSE, 500,   20, TRUE,  1707722000),
       ('Gaming Chat',        'PUBLIC',  1707720003, FALSE, 500,  200, FALSE, 1707722200),
       ('Esports Discussion', 'PRIVATE', 1707720004, FALSE, 800,   30, FALSE, 1707722700);

