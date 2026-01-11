-- Data SQL pentru Event Service
-- Inserează date de test pentru Stages și Events
-- Rulează automat după ce Hibernate creează tabelele

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
