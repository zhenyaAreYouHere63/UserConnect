CREATE TABLE "users" (

    id       SERIAL PRIMARY KEY,
    uuid     UUID         NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL UNIQUE,
    is_email_verified BOOLEAN,
    role VARCHAR(32) NOT NULL
);
