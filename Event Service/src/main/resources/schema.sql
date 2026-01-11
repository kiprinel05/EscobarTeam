-- Schema SQL pentru Event Service
-- Creează tabelele și inserează date de test pentru Stages și Events

-- Tabela Stages
CREATE TABLE IF NOT EXISTS stages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(200) NOT NULL,
    max_capacity INTEGER NOT NULL CHECK (max_capacity > 0)
);

CREATE INDEX IF NOT EXISTS idx_stage_name ON stages(name);
CREATE INDEX IF NOT EXISTS idx_stage_location ON stages(location);

-- Tabela Events
CREATE TABLE IF NOT EXISTS event (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    date TIMESTAMP NOT NULL,
    stage_id BIGINT NOT NULL,
    associated_artist VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    created_at TIMESTAMP,
    CONSTRAINT fk_event_stage FOREIGN KEY (stage_id) REFERENCES stages(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_event_name ON event(name);
CREATE INDEX IF NOT EXISTS idx_event_date ON event(date);
CREATE INDEX IF NOT EXISTS idx_event_stage_id ON event(stage_id);
CREATE INDEX IF NOT EXISTS idx_event_artist ON event(associated_artist);

-- Insert Stages (doar dacă nu există deja)
INSERT INTO stages (name, location, max_capacity) VALUES
('Main Stage', 'Central Park - Sector A', 50000),
('Secondary Stage', 'Central Park - Sector B', 30000),
('Electronic Stage', 'Central Park - Sector C', 20000),
('Hip-Hop Stage', 'Central Park - Sector D', 25000),
('Pop Stage', 'Central Park - Sector E', 15000)
ON CONFLICT (name) DO NOTHING;

-- Insert Events (doar dacă nu există deja)
-- Folosim stage_id = 1 pentru Main Stage (trebuie să existe)
INSERT INTO event (name, date, stage_id, associated_artist, capacity, created_at)
SELECT * FROM (VALUES
('Travis Scott Live Concert', '2024-07-15 20:00:00'::TIMESTAMP, 1, 'Travis Scott', 50000, NOW()),
('The Weeknd Performance', '2024-07-16 21:00:00'::TIMESTAMP, 1, 'The Weeknd', 50000, NOW()),
('Hip-Hop Night - Romanian Artists', '2024-07-17 19:00:00'::TIMESTAMP, 4, 'Oscar, Ian, Rava', 25000, NOW()),
('Electronic Music Festival', '2024-07-18 22:00:00'::TIMESTAMP, 3, 'Various DJs', 20000, NOW()),
('Pop Stars Showcase', '2024-07-19 20:30:00'::TIMESTAMP, 5, 'Inna, Dua Lipa, Billie Eilish', 15000, NOW()),
('Drake Headline Show', '2024-07-20 21:00:00'::TIMESTAMP, 1, 'Drake', 50000, NOW()),
('Post Malone Concert', '2024-07-21 20:00:00'::TIMESTAMP, 2, 'Post Malone', 30000, NOW()),
('Eminem Special Performance', '2024-07-22 19:30:00'::TIMESTAMP, 1, 'Eminem', 50000, NOW()),
('Ariana Grande Live', '2024-07-23 20:00:00'::TIMESTAMP, 5, 'Ariana Grande', 15000, NOW()),
('Kendrick Lamar Exclusive', '2024-07-24 21:00:00'::TIMESTAMP, 1, 'Kendrick Lamar', 50000, NOW())
) AS v(name, date, stage_id, associated_artist, capacity, created_at)
WHERE NOT EXISTS (SELECT 1 FROM event WHERE event.name = v.name AND event.date = v.date);
