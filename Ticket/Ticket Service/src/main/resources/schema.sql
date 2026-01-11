-- Schema SQL pentru Ticket Service
-- Creează tabela și inserează date de test pentru Tickets

-- Tabela Tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    event_name VARCHAR(200) NOT NULL,
    ticket_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0.0),
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    buyer_name VARCHAR(100),
    buyer_email VARCHAR(100),
    purchase_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ticket_event_name ON tickets(event_name);
CREATE INDEX IF NOT EXISTS idx_ticket_type ON tickets(ticket_type);
CREATE INDEX IF NOT EXISTS idx_ticket_buyer_email ON tickets(buyer_email);
CREATE INDEX IF NOT EXISTS idx_ticket_purchase_date ON tickets(purchase_date);
CREATE INDEX IF NOT EXISTS idx_ticket_active ON tickets(is_active);

-- Insert Tickets (doar dacă nu există deja)
-- Notă: Folosim o verificare simplă pentru a evita duplicatele
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
