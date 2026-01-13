# 🎵 Festival Management System

Sistem de management pentru festivaluri muzicale construit cu arhitectură microservicii, folosind Spring Boot și Spring Cloud.

## 📋 Cuprins

- [Descriere](#descriere)
- [Arhitectură](#arhitectură)
- [Tehnologii](#tehnologii)
- [Structura Proiectului](#structura-proiectului)
- [Cerințe](#cerințe)
- [Instalare și Configurare](#instalare-și-configurare)
- [Rulare](#rulare)
- [API Endpoints](#api-endpoints)
- [Flow-ul Aplicației](#flow-ul-aplicației)
- [Securitate](#securitate)
- [Observabilitate](#observabilitate)
- [Testare](#testare)
- [Deployment](#deployment)

## 🎯 Descriere

Festival Management System este o aplicație distribuită pentru gestionarea unui festival muzical, oferind funcționalități complete pentru:

- **Gestionare Artiști**: CRUD operații, căutare, filtrare și sortare artiști
- **Gestionare Evenimente**: Creare evenimente, programare pe scene, verificare disponibilitate
- **Gestionare Scene**: Managementul scenelor și capcăților acestora
- **Gestionare Bilete**: Vânzare bilete, calcul venituri, validare evenimente

Aplicația folosește o arhitectură microservicii cu service discovery, load balancing, distributed tracing și securitate OAuth2.

## 🏗️ Arhitectură

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (Browser/Postman)                 │
└────────────────────┬──────────────────────────────────────┘
                     │ HTTP Request
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              GATEWAY SERVICE (Port 8072/8073)              │
│  • Spring Cloud Gateway                                    │
│  • OAuth2 Security (Google)                               │
│  • Custom Filters                                          │
│  • Load Balancing                                          │
└────────────────────┬──────────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   EUREKA SERVER       │
         │   (Port 8761)         │
         │   Service Discovery   │
         └───────────────────────┘
                     │
         ┌───────────┼───────────┐
         │           │           │
         ▼           ▼           ▼
    ┌────────┐  ┌────────┐  ┌────────┐
    │ ARTIST │  │ EVENT  │  │ TICKET │
    │ SERVICE│  │ SERVICE│  │ SERVICE│
    │ :8080  │  │ :8081  │  │ :8083  │
    └───┬────┘  └───┬────┘  └───┬────┘
        │           │           │
        └───────────┼───────────┘
                    │
                    ▼
         ┌───────────────────────┐
         │   POSTGRESQL DB       │
         │   (Port 5432)         │
         │   festival_db         │
         └───────────────────────┘
```

### Componente

- **Eureka Server**: Service discovery și load balancing
- **Gateway Service**: API Gateway cu routing, securitate și filtre custom
- **Artist Service**: Gestionare artiști și evenimente asociate
- **Event Service**: Gestionare evenimente și scene (2 instanțe pentru load balancing)
- **Ticket Service**: Gestionare bilete și calcul venituri
- **PostgreSQL**: Baza de date comună pentru toate serviciile
- **Zipkin**: Distributed tracing pentru monitorizare

## 🛠️ Tehnologii

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
  - Spring Cloud Gateway
  - Netflix Eureka (Service Discovery)
  - OpenFeign (Inter-Service Communication)
- **Spring Security** cu OAuth2 (Google)
- **Spring Data JPA**
- **PostgreSQL 15**
- **Lombok**
- **Micrometer Tracing** (Zipkin)

### DevOps & Tools
- **Docker & Docker Compose**
- **Maven**
- **JaCoCo** (Code Coverage)
- **Swagger/OpenAPI** (Documentație API)

## 📁 Structura Proiectului

```
EscobarTeam/
├── Eureka Server/          # Service Discovery Server
├── Gateway Service/        # API Gateway
├── Artist Service/         # Microserviciu pentru artiști
├── Event Service/          # Microserviciu pentru evenimente și scene
├── Ticket Service/        # Microserviciu pentru bilete
├── docker-compose.yml     # Configurare Docker pentru toate serviciile
├── populate-database.sql  # Script pentru popularea bazei de date
└── README.md              # Acest fișier
```

## 📦 Cerințe

### Pentru rulare locală:
- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 15+**
- **Docker & Docker Compose** (opțional, pentru rulare completă)

### Pentru rulare cu Docker:
- **Docker Desktop** sau **Docker Engine**
- **Docker Compose**

## 🚀 Instalare și Configurare

### 1. Clonare Repository

```bash
git clone <repository-url>
cd EscobarTeam
```

### 2. Configurare Baza de Date

Creează baza de date PostgreSQL:

```sql
CREATE DATABASE festival_db;
```

Sau folosește Docker Compose (va crea automat baza de date).

### 3. Configurare Variabile de Mediu

Pentru fiecare serviciu, verifică configurația în `application.properties`:

- **Database URL**: `jdbc:postgresql://localhost:5432/festival_db`
- **Database Username**: `postgres`
- **Database Password**: `1q2w3e`
- **Eureka Server URL**: `http://localhost:8761/eureka/`
- **Zipkin URL**: `http://localhost:9411`

### 4. Configurare OAuth2 (Gateway)

Pentru autentificare Google OAuth2, actualizează în `Gateway Service/src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_CLIENT_ID
            client-secret: YOUR_CLIENT_SECRET
```

## ▶️ Rulare

### Opțiunea 1: Rulare cu Docker Compose (Recomandat)

#### Pornire completă (fără Gateway)

```bash
docker-compose up --build
```

#### Pornire cu Gateway Security (Port 8072)

```bash
docker-compose --profile security up --build
```

#### Pornire cu Gateway API (Port 8073)

```bash
docker-compose --profile api up --build
```

#### Pornire în background

```bash
docker-compose --profile security up --build -d
```

#### Verificare status

```bash
docker-compose ps
```

#### Oprire servicii

```bash
docker-compose down
```

### Opțiunea 2: Rulare Locală (Fără Docker)

#### 1. Pornește PostgreSQL

```bash
# Windows
pg_ctl start

# Linux/Mac
sudo systemctl start postgresql
```

#### 2. Pornește Eureka Server

```bash
cd "Eureka Server"
mvn spring-boot:run
```

Eureka Dashboard: http://localhost:8761

#### 3. Pornește Microserviciile

În terminale separate:

```bash
# Artist Service
cd "Artist Service"
mvn spring-boot:run

# Event Service
cd "Event Service"
mvn spring-boot:run

# Ticket Service
cd "Ticket Service"
mvn spring-boot:run
```

#### 4. Pornește Gateway Service

```bash
cd "Gateway Service"
mvn spring-boot:run
```

### Verificare Servicii

- **Eureka Dashboard**: http://localhost:8761
- **Zipkin Dashboard**: http://localhost:9411
- **Gateway Swagger**: http://localhost:8072/swagger-ui.html
- **Artist Service Swagger**: http://localhost:8080/swagger-ui.html
- **Event Service Swagger**: http://localhost:8081/swagger-ui.html
- **Ticket Service Swagger**: http://localhost:8083/swagger-ui.html

## 📡 API Endpoints

### Gateway Service (Port 8072/8073)

Toate endpoint-urile sunt accesibile prin Gateway:

```
http://localhost:8072/api/{service}/{endpoint}
```

### Artist Service

```
GET    /api/artists                    # Lista toți artiștii
GET    /api/artists/{id}              # Detalii artist
POST   /api/artists                    # Creare artist
PUT    /api/artists/{id}              # Actualizare artist
DELETE /api/artists/{id}              # Ștergere artist
GET    /api/artists/search?name=...   # Căutare artiști
GET    /api/artists/filter/genre=...  # Filtrare după gen
GET    /api/artists/{id}/events       # Artiști cu evenimente
POST   /api/artists/{id}/schedule-event # Programare eveniment
```

### Event Service

```
GET    /api/events                    # Lista toate evenimentele
GET    /api/events/{id}               # Detalii eveniment
POST   /api/events                    # Creare eveniment
PUT    /api/events/{id}               # Actualizare eveniment
DELETE /api/events/{id}               # Ștergere eveniment
GET    /api/events/search?name=...     # Căutare evenimente
GET    /api/events/filter/stage?stageId=... # Filtrare după scenă
GET    /api/events/{id}/ticket-info   # Eveniment cu informații bilete
GET    /api/events/statistics          # Statistici evenimente
```

### Stage Service

```
GET    /api/stages                    # Lista toate scenele
GET    /api/stages/{id}               # Detalii scenă
POST   /api/stages                    # Creare scenă
PUT    /api/stages/{id}               # Actualizare scenă
DELETE /api/stages/{id}               # Ștergere scenă
GET    /api/stages/search?name=...     # Căutare scene
```

### Ticket Service

```
GET    /api/tickets                   # Lista toate biletele
GET    /api/tickets/{id}              # Detalii bilet
POST   /api/tickets                   # Creare bilet
PUT    /api/tickets/{id}              # Actualizare bilet
DELETE /api/tickets/{id}              # Ștergere bilet
GET    /api/tickets/festival/{eventName} # Bilete pentru eveniment
GET    /api/tickets/revenue/by-festival # Venituri pe eveniment
GET    /api/tickets/revenue/total      # Venit total
POST   /api/tickets/purchase-with-validation # Cumpărare bilet cu validare
```

## 🔄 Flow-ul Aplicației

### Exemplu: Obținere Artist cu Evenimente

```
1. CLIENT
   └─> GET http://localhost:8072/api/artists/1/events
       Headers: X-Region: EU-RO, X-Content-Language: ro-RO

2. GATEWAY SERVICE
   ├─> Security Check (OAuth2 - GET endpoints sunt publice)
   ├─> Route Matching: /api/artists/** → ARTIST-SERVICE
   ├─> Custom Filters:
   │   ├─> CustomRegionFilter: Adaugă header-e regionale
   │   └─> CustomHeaderFilter: Adaugă tracking headers
   └─> Service Discovery: Caută ARTIST-SERVICE în Eureka

3. ARTIST SERVICE
   ├─> Controller: ArtistController.getArtistWithEvents()
   ├─> Service: ArtistServiceImpl.getArtistWithEvents()
   │   ├─> 1. Caută artist în DB
   │   ├─> 2. Feign Client → EVENT-SERVICE
   │   │   └─> GET /api/events/filter/artist?artist=Travis Scott
   │   ├─> 3. Procesare: Filtrare evenimente viitoare, sortare
   │   └─> 4. Return ArtistWithEventsDTO
   └─> Response JSON

4. GATEWAY SERVICE
   ├─> Response Filters: Adaugă CORS headers
   └─> Return Response către CLIENT
```

### Comunicare Inter-Service

Serviciile comunică între ele folosind **OpenFeign** și **Eureka**:

- **Artist Service** → **Event Service** (pentru evenimente asociate)
- **Event Service** → **Ticket Service** (pentru informații bilete)
- **Ticket Service** → **Event Service** (pentru validare evenimente)

## 🔐 Securitate

### OAuth2 Authentication (Google)

Gateway-ul folosește OAuth2 pentru autentificare:

1. **Login**: http://localhost:8072/login
2. Redirect către Google pentru autentificare
3. După autentificare, utilizatorul primește token-uri

### Role-Based Access Control

- **ADMIN**: Acces complet (CRUD pentru toate resursele)
- **ARTIST_MANAGER**: Poate crea/actualiza artiști și evenimente
- **TICKET_MANAGER**: Poate gestiona bilete

### Endpoint Permissions

- **GET endpoints**: Publice (nu necesită autentificare)
- **POST/PUT endpoints**: Necesită roluri specifice
- **DELETE endpoints**: Doar ADMIN

## 📊 Observabilitate

### Eureka Dashboard

Monitorizează serviciile înregistrate:

```
http://localhost:8761
```

### Zipkin Tracing

Vizualizează distributed tracing pentru toate cererile:

```
http://localhost:9411
```

### Spring Actuator

Health checks și metrics pentru fiecare serviciu:

```
http://localhost:8080/actuator/health
http://localhost:8081/actuator/health
http://localhost:8083/actuator/health
http://localhost:8072/actuator/health
```

## 🧪 Testare

### Rulare Teste

Pentru fiecare serviciu separat:

```bash
# Artist Service
cd "Artist Service"
mvn clean test

# Event Service
cd "Event Service"
mvn clean test

# Ticket Service
cd "Ticket Service"
mvn clean test
```

### Code Coverage

JaCoCo generează rapoarte de code coverage:

```bash
mvn clean test jacoco:report
```

Rapoarte disponibile în: `target/site/jacoco/index.html`

## 🐳 Deployment

### Docker Compose

Aplicația este pregătită pentru deployment cu Docker Compose:

```bash
# Build și start
docker-compose --profile security up --build -d

# Verificare logs
docker-compose logs -f gateway-security

# Stop
docker-compose down
```

### Health Checks

Toate containerele au health checks configurate pentru monitorizare automată.

## 📝 Exemple de Utilizare

### Creare Artist

```bash
curl -X POST http://localhost:8072/api/artists \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Travis Scott",
    "genre": "Hip-Hop",
    "age": 32,
    "nationality": "American",
    "email": "travis@example.com",
    "biography": "Rapper and producer",
    "rating": 9.2
  }'
```

### Creare Eveniment

```bash
curl -X POST http://localhost:8072/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Travis Scott Live Concert",
    "date": "2024-07-15T20:00:00",
    "stageId": 1,
    "associatedArtist": "Travis Scott",
    "capacity": 50000
  }'
```

### Cumpărare Bilet

```bash
curl -X POST http://localhost:8072/api/tickets/purchase-with-validation \
  -H "Content-Type: application/json" \
  -H "X-Region: EU-RO" \
  -H "X-Content-Language: ro-RO" \
  -d '{
    "eventName": "Travis Scott Live Concert",
    "ticketType": "VIP",
    "price": 500.00,
    "quantity": 2,
    "buyerName": "John Doe",
    "buyerEmail": "john@example.com"
  }'
```

## 🤝 Contribuții

Proiect realizat de **EscobarTeam**.

## 📄 Licență

Acest proiect este realizat în scop educațional.

## 🔗 Resurse Utile

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Eureka Documentation](https://github.com/Netflix/eureka)
- [Zipkin Documentation](https://zipkin.io/)

## 📞 Suport

Pentru întrebări sau probleme, deschide un issue în repository.

---

**Versiune**: 1.0.0  
**Ultima actualizare**: 2024
