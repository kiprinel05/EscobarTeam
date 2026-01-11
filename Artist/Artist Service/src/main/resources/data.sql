-- Data SQL pentru Artist Service
-- Inserează date de test pentru Artists
-- Rulează automat după ce Hibernate creează tabelele

-- Insert artisti (doar dacă nu există deja)
INSERT INTO artists (name, genre, age, nationality, email, biography, rating, is_active, created_at, updated_at) VALUES
-- Artisti ceruti
('Travis Scott', 'Hip-Hop', 32, 'American', 'travis.scott@example.com', 'Travis Scott este un rapper, cantaret si producator american cunoscut pentru stilul sau inovator si spectacolele live impresionante.', 9.2, TRUE, NOW(), NOW()),
('The Weeknd', 'R&B', 34, 'Canadian', 'theweeknd@example.com', 'The Weeknd este un cantaret, compozitor si producator canadian de origine etiopiana, cunoscut pentru vocea sa distincta si stilul R&B modern.', 9.5, TRUE, NOW(), NOW()),
('Oscar', 'Hip-Hop', 28, 'Romanian', 'oscar@example.com', 'Oscar este un rapper roman cunoscut pentru versurile sale si stilul sau autentic.', 8.5, TRUE, NOW(), NOW()),
('Ian', 'Hip-Hop', 27, 'Romanian', 'ian@example.com', 'Ian este un rapper roman, membru al echipei Rapperii de pe Strazi, cunoscut pentru flow-ul sau rapid.', 8.7, TRUE, NOW(), NOW()),
('Rava', 'Hip-Hop', 26, 'Romanian', 'rava@example.com', 'Rava este un rapper roman talentat, parte a scenei hip-hop romanesti moderne.', 8.6, TRUE, NOW(), NOW()),
('Inna', 'Pop', 38, 'Romanian', 'inna@example.com', 'Inna este o cantareata romana de muzica pop si dance, cunoscuta international pentru hit-urile sale.', 8.8, TRUE, NOW(), NOW()),
('Ken Carson', 'Hip-Hop', 25, 'American', 'ken.carson@example.com', 'Ken Carson este un rapper american, parte a miscarii underground hip-hop, cunoscut pentru stilul sau experimental.', 8.3, TRUE, NOW(), NOW()),
('Don Toliver', 'Hip-Hop', 30, 'American', 'dontoliver@example.com', 'Don Toliver este un rapper si cantaret american cunoscut pentru melodiile sale catchy si colaborarile cu artisti de top.', 8.9, TRUE, NOW(), NOW()),

-- Artisti aditionali
('Drake', 'Hip-Hop', 37, 'Canadian', 'drake@example.com', 'Drake este unul dintre cei mai de succes rapperi si cantareti din lume, cunoscut pentru numeroasele sale hit-uri si stilul sau versatil.', 9.8, TRUE, NOW(), NOW()),
('Post Malone', 'Hip-Hop', 29, 'American', 'postmalone@example.com', 'Post Malone este un rapper, cantaret si compozitor american cunoscut pentru stilul sau unic care combina hip-hop, pop si rock.', 9.4, TRUE, NOW(), NOW()),
('Billie Eilish', 'Pop', 22, 'American', 'billie.eilish@example.com', 'Billie Eilish este o cantareata si compozitoare americana cunoscuta pentru stilul sau alternativ si vocea sa distincta.', 9.6, TRUE, NOW(), NOW()),
('Dua Lipa', 'Pop', 29, 'British', 'dualipa@example.com', 'Dua Lipa este o cantareata si compozitoare britanico-albaneza, cunoscuta pentru hit-urile sale de muzica pop si dans.', 9.3, TRUE, NOW(), NOW()),
('Eminem', 'Hip-Hop', 51, 'American', 'eminem@example.com', 'Eminem este unul dintre cei mai mari rapperi din istorie, cunoscut pentru tehnica sa si versurile sale profunde.', 9.7, TRUE, NOW(), NOW()),
('Ariana Grande', 'Pop', 31, 'American', 'ariana.grande@example.com', 'Ariana Grande este o cantareata si actrita americana cunoscuta pentru vocea sa puternica si hit-urile sale pop.', 9.5, TRUE, NOW(), NOW()),
('Kendrick Lamar', 'Hip-Hop', 37, 'American', 'kendrick.lamar@example.com', 'Kendrick Lamar este un rapper si compozitor american, cunoscut pentru versurile sale profunde si stilul sau artistic.', 9.9, TRUE, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
