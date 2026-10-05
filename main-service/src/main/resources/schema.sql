CREATE TABLE IF NOT EXISTS users (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(254) NOT NULL UNIQUE,
    name varchar(250) NOT NULL,
    CONSTRAINT valid_email CHECK (LENGTH(email) >= 6),
    CONSTRAINT valid_username CHECK (LENGTH(name) >= 2)
);

CREATE TABLE IF NOT EXISTS categories (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE,
    CONSTRAINT valid_category_name CHECK (LENGTH(name) >= 1)
);

CREATE TABLE IF NOT EXISTS locations (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat NUMERIC(10, 7) NOT NULL,
    lon NUMERIC(10, 7) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id INT REFERENCES categories(id) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date timestamp NOT NULL,
    location_id INT REFERENCES locations(id) NOT NULL,
    paid boolean NOT NULL DEFAULT FALSE,
    participant_limit INT NOT NULL DEFAULT 0,
    request_moderation boolean NOT NULL DEFAULT TRUE,
    title VARCHAR(120) NOT NULL,
    created_on timestamp NOT NULL,
    initiator_id INT REFERENCES users(id) NOT NULL,
    published_on timestamp,
    state VARCHAR NOT NULL,
    confirmed_requests INT NOT NULL DEFAULT 0,
    CONSTRAINT valid_annotation CHECK (LENGTH(annotation) >= 20),
    CONSTRAINT valid_description CHECK (LENGTH(description) >= 20),
    CONSTRAINT valid_event_title CHECK (LENGTH(title) >= 3)
);

CREATE TABLE IF NOT EXISTS all_compilations (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned boolean NOT NULL DEFAULT FALSE,
    title VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT valid_compilation_title CHECK (LENGTH(title) >= 1)
);

CREATE TABLE IF NOT EXISTS compilations_with_events (
    compilation_id INT NOT NULL REFERENCES all_compilations(id) ON DELETE CASCADE,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS requests (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id INT REFERENCES events(id) NOT NULL,
    requester_id INT REFERENCES users(id) NOT NULL,
    created timestamp NOT NULL,
    status varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id INT REFERENCES users(id) NOT NULL,
    event_id INT REFERENCES events(id) NOT NULL,
    text varchar(254) NOT NULL,
    created timestamp
);