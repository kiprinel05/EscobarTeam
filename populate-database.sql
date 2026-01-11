-- ============================================
-- EVENT SERVICE - Stages și Events
-- ============================================

INSERT INTO stages (name, location, max_capacity) VALUES
('Main Stage', 'Central Park - Sector A', 50000),
('Secondary Stage', 'Central Park - Sector B', 30000),
('Electronic Stage', 'Central Park - Sector C', 20000),
('Hip-Hop Stage', 'Central Park - Sector D', 25000),
('Pop Stage', 'Central Park - Sector E', 15000)
ON CONFLICT (name) DO NOTHING;

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

-- ============================================
-- TICKET SERVICE - Tickets
-- ============================================

INSERT INTO tickets (event_name, ticket_type, price, quantity, buyer_name, buyer_email, purchase_date, is_active, created_at)
SELECT * FROM (VALUES
-- VIP Tickets
('Travis Scott Live Concert', 'VIP', 500.00, 2, 'John Doe', 'john.doe@example.com', NOW() - INTERVAL '5 days', TRUE, NOW() - INTERVAL '5 days'),
('The Weeknd Performance', 'VIP', 450.00, 1, 'Jane Smith', 'jane.smith@example.com', NOW() - INTERVAL '4 days', TRUE, NOW() - INTERVAL '4 days'),
('Drake Headline Show', 'VIP', 600.00, 3, 'Mike Johnson', 'mike.johnson@example.com', NOW() - INTERVAL '3 days', TRUE, NOW() - INTERVAL '3 days'),

-- General Admission Tickets
('Travis Scott Live Concert', 'GENERAL', 150.00, 4, 'Sarah Williams', 'sarah.williams@example.com', NOW() - INTERVAL '6 days', TRUE, NOW() - INTERVAL '6 days'),
('Hip-Hop Night - Romanian Artists', 'GENERAL', 80.00, 2, 'Alex Popescu', 'alex.popescu@example.com', NOW() - INTERVAL '5 days', TRUE, NOW() - INTERVAL '5 days'),
('Electronic Music Festival', 'GENERAL', 100.00, 1, 'Maria Ionescu', 'maria.ionescu@example.com', NOW() - INTERVAL '4 days', TRUE, NOW() - INTERVAL '4 days'),
('Pop Stars Showcase', 'GENERAL', 120.00, 2, 'David Brown', 'david.brown@example.com', NOW() - INTERVAL '3 days', TRUE, NOW() - INTERVAL '3 days'),
('Post Malone Concert', 'GENERAL', 180.00, 3, 'Emma Davis', 'emma.davis@example.com', NOW() - INTERVAL '2 days', TRUE, NOW() - INTERVAL '2 days'),

-- Early Bird Tickets
('Eminem Special Performance', 'EARLY_BIRD', 200.00, 2, 'Robert Taylor', 'robert.taylor@example.com', NOW() - INTERVAL '7 days', TRUE, NOW() - INTERVAL '7 days'),
('Ariana Grande Live', 'EARLY_BIRD', 250.00, 1, 'Lisa Anderson', 'lisa.anderson@example.com', NOW() - INTERVAL '6 days', TRUE, NOW() - INTERVAL '6 days'),
('Kendrick Lamar Exclusive', 'EARLY_BIRD', 300.00, 2, 'Chris Wilson', 'chris.wilson@example.com', NOW() - INTERVAL '5 days', TRUE, NOW() - INTERVAL '5 days'),

-- More General Tickets
('The Weeknd Performance', 'GENERAL', 160.00, 2, 'Anna Martinez', 'anna.martinez@example.com', NOW() - INTERVAL '4 days', TRUE, NOW() - INTERVAL '4 days'),
('Drake Headline Show', 'GENERAL', 220.00, 4, 'Tom Garcia', 'tom.garcia@example.com', NOW() - INTERVAL '3 days', TRUE, NOW() - INTERVAL '3 days'),
('Electronic Music Festival', 'GENERAL', 100.00, 2, 'Sophie Lee', 'sophie.lee@example.com', NOW() - INTERVAL '2 days', TRUE, NOW() - INTERVAL '2 days'),

-- Some cancelled/inactive tickets
('Travis Scott Live Concert', 'GENERAL', 150.00, 1, 'Canceled User', 'canceled@example.com', NOW() - INTERVAL '10 days', FALSE, NOW() - INTERVAL '10 days'),
('Post Malone Concert', 'VIP', 400.00, 1, 'Refunded User', 'refunded@example.com', NOW() - INTERVAL '8 days', FALSE, NOW() - INTERVAL '8 days')
) AS v(event_name, ticket_type, price, quantity, buyer_name, buyer_email, purchase_date, is_active, created_at)
WHERE NOT EXISTS (
    SELECT 1 FROM tickets 
    WHERE tickets.event_name = v.event_name 
    AND tickets.buyer_email = v.buyer_email 
    AND tickets.ticket_type = v.ticket_type
    AND tickets.purchase_date = v.purchase_date
);

SELECT 'Stages count: ' || COUNT(*) FROM stages;
SELECT 'Events count: ' || COUNT(*) FROM event;
SELECT 'Tickets count: ' || COUNT(*) FROM tickets;
