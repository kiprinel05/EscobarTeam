CREATE DATABASE festival_db;

CREATE TABLE IF NOT EXISTS artists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    age INTEGER CHECK (age >= 18 AND age <= 100),
    nationality VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    biography TEXT,
    rating DECIMAL(3,2) CHECK (rating >= 0.0 AND rating <= 10.0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_artist_name ON artists(name);
CREATE INDEX IF NOT EXISTS idx_artist_genre ON artists(genre);
CREATE INDEX IF NOT EXISTS idx_artist_nationality ON artists(nationality);
CREATE INDEX IF NOT EXISTS idx_artist_rating ON artists(rating);
CREATE INDEX IF NOT EXISTS idx_artist_active ON artists(is_active);

