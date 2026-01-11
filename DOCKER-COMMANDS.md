# 🐳 Comenzi Docker pentru Pornire

## 📋 Comenzi de Bază

### 1. Construirea imaginilor și pornirea tuturor serviciilor

```bash
# Construiește imaginile și pornește toate serviciile (fără Gateway)
docker-compose up --build

# Sau în background (detached mode)
docker-compose up --build -d
```

### 2. Pornire cu Gateway Security (port 8072)

```bash
# Construiește și pornește cu profilul "security"
docker-compose --profile security up --build

# Sau în background
docker-compose --profile security up --build -d
```

### 3. Pornire cu Gateway API (port 8073)

```bash
# Construiește și pornește cu profilul "api"
docker-compose --profile api up --build

# Sau în background
docker-compose --profile api up --build -d
```

### 4. Pornire cu ambele Gateway-uri

```bash
# Construiește și pornește cu ambele profile
docker-compose --profile security --profile api up --build

# Sau în background
docker-compose --profile security --profile api up --build -d
```

## 🔧 Comenzi Utile

### Verificare Status

```bash
# Vezi statusul tuturor containerelor
docker-compose ps

# Vezi logurile tuturor serviciilor
docker-compose logs

# Vezi logurile unui serviciu specific
docker-compose logs artist-service
docker-compose logs event-service
docker-compose logs ticket-service
docker-compose logs gateway-security
docker-compose logs eureka-server
docker-compose logs postgres
```

### Oprire Servicii

```bash
# Oprește toate serviciile (păstrează containerele)
docker-compose stop

# Oprește și șterge containerele (NU șterge volume-urile)
docker-compose down

# Oprește și șterge tot (inclusiv volume-urile - ATENȚIE!)
docker-compose down -v
```

### Reconstruire Servicii

```bash
# Reconstruiește un serviciu specific
docker-compose build artist-service

# Reconstruiește și repornește un serviciu
docker-compose up --build -d artist-service
```

### Restart Servicii

```bash
# Restart un serviciu specific
docker-compose restart artist-service

# Restart toate serviciile
docker-compose restart
```

## 🚀 Workflow Recomandat

### Prima dată (sau după modificări)

```bash
# 1. Oprește toate serviciile existente
docker-compose down

# 2. Construiește și pornește totul cu Gateway Security
docker-compose --profile security up --build -d

# 3. Verifică statusul
docker-compose ps

# 4. Verifică logurile dacă ceva nu merge
docker-compose logs -f gateway-security
```

### După modificări de cod

```bash
# Reconstruiește și repornește un serviciu specific
docker-compose up --build -d artist-service

# Sau reconstruiește totul
docker-compose --profile security up --build -d
```

## 📊 Verificare Rapidă

```bash
# Verifică health checks
docker-compose ps

# Testează endpoint-urile
curl http://localhost:8072/api/artists
curl http://localhost:8072/api/events
curl http://localhost:8072/api/tickets

# Verifică Eureka Dashboard
# Deschide în browser: http://localhost:8761

# Verifică Zipkin
# Deschide în browser: http://localhost:9411
```

## 🔍 Debugging

```bash
# Intră într-un container
docker exec -it artist-service sh
docker exec -it gateway-security sh

# Vezi logurile în timp real
docker-compose logs -f

# Vezi logurile unui serviciu specific în timp real
docker-compose logs -f gateway-security

# Verifică rețeaua Docker
docker network ls
docker network inspect escobarteam_festival-network
```

## ⚠️ Probleme Comune

### Port deja folosit

```bash
# Vezi ce folosește portul
netstat -ano | findstr :8080

# Sau oprește toate containerele
docker-compose down
```

### Serviciu nu pornește

```bash
# Verifică logurile
docker-compose logs serviciu-nume

# Verifică health check
docker-compose ps
```

### Database nu se conectează

```bash
# Verifică dacă PostgreSQL rulează
docker-compose ps postgres

# Verifică logurile PostgreSQL
docker-compose logs postgres

# Conectează-te la PostgreSQL
docker exec -it festival-postgres psql -U postgres -d festival_db
```

## 📝 Exemple Complete

### Pornire completă cu Gateway Security

```bash
cd c:\Users\Asus\Desktop\EscobarTeam
docker-compose --profile security up --build -d
docker-compose ps
```

### Pornire completă cu Gateway API

```bash
cd c:\Users\Asus\Desktop\EscobarTeam
docker-compose --profile api up --build -d
docker-compose ps
```

### Pornire completă cu ambele Gateway-uri

```bash
cd c:\Users\Asus\Desktop\EscobarTeam
docker-compose --profile security --profile api up --build -d
docker-compose ps
```
