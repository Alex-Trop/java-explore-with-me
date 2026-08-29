CREATE TABLE IF NOT EXISTS hits (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app varchar NOT NULL,
    uri varchar NOT NULL,
    ip varchar NOT NULL,
    created timestamp NOT NULL
);