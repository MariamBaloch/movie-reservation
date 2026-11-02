# MovieReservation

## Project Overview

MovieReservation is a full-stack cinema seat reservation platform developed as part of the General Assembly Java Development Bootcamp Hackathon.

The application allows users to browse movies, view available showtimes, select cinema seats, and make reservations.

The system uses Microservices Architecture to separate authentication, movie management, and booking operations into independent Spring Boot applications.

A key feature of the project is preventing double bookings when multiple users attempt to reserve the same seat simultaneously.

## Problem Statement

Cinema reservation systems must manage seat availability accurately, especially when multiple customers attempt to reserve seats at the same time.

Without proper concurrency control, multiple users could reserve the same seat, resulting in inconsistent booking records.

MovieReservation addresses this problem through concurrency control, locking mechanisms, and MongoDB atomic operations.

## Team Members

| Team Member | Responsibilities |
|-------------|------------------|
| Shaika Mohamed | Authentication Service, Login and Registration |
| IsraaHusain | Movie and Screen Management |
| Fatima Abdulla | Showtime Management, Seat Generation, Showtime UI |
| Mariam Baloch | Booking Service, Reservations, Concurrency |

## Technologies Used

### Backend
- Java 17
- Spring Boot 4.1.2
- Spring Security
- Spring Data MongoDB
- REST APIs
- Lombok
- MapStruct
- Maven

### Frontend
- React
- Vite
- JavaScript

### Database
- MongoDB Atlas

### DevOps and Collaboration
- Docker
- Docker Compose
- Git
- GitHub
- Postman

## Microservices Architecture

The application consists of three independent microservices.

### 1. Authentication Service

Port: 8081

Responsibilities:
- User registration
- User login
- JWT authentication
- Role-based authorization
- User management

Database: `moviereserve_auth`

### 2. Movie Service

Port: 8082

Responsibilities:
- Movie management
- Screen management
- Showtime management
- Ticket pricing
- Showtime scheduling
- Seat-number generation

Database: `moviereserve_movies`

### 3. Booking Service

Port: 8083

Responsibilities:
- Booking creation
- Booking cancellation
- Seat inventory management
- Seat availability
- Concurrency control
- Double-booking prevention

Database: `moviereserve_bookings`

## Architecture Diagram

```mermaid
flowchart TD
    A[React Frontend] --> B[Auth Service]
    A --> C[Movie Service]
    A --> D[Booking Service]

    B --> E[(Auth Database)]
    C --> F[(Movie Database)]
    D --> G[(Booking Database)]

    C -->|Initialize Seats| D
    D -->|Get Showtime Details| C
```

## Project Structure

```text
MovieReservation/
│
├── backend/
│   ├── auth-service/
│   ├── booking-service/
│   └── movie-service/
│
├── frontend/
│
├── docs/
│   ├── showtime-service.md
│   ├── database-design.md
│   └── api-documentation.md
│
├── docker-compose.yml
└── README.md
```


## Database Architecture

The application uses MongoDB Atlas as its primary database.

Each microservice owns a separate logical database within
the same MongoDB Atlas cluster.

| Microservice | Database |
|--------------|----------|
| Auth Service | moviereserve_auth |
| Movie Service | moviereserve_movies |
| Booking Service | moviereserve_bookings |

Microservices communicate through REST APIs rather than accessing
each other's databases directly.

## Installation and Setup

### Prerequisites

- Java 17
- Maven
- Node.js and npm
- MongoDB Atlas account
- Git
- Docker Desktop (for containerized execution)

### 1. Clone the Repository

```bash
git clone https://github.com/MariamBaloch/movie-reservation.git
cd MovieReservation
```

### 2. Configure MongoDB Atlas

1. Create a MongoDB Atlas cluster.
2. Create database users.
3. Configure network access.
4. Obtain the MongoDB connection strings.
5. Set the appropriate environment variables.

### 3. Configure Environment Variables

Each Spring Boot microservice requires its own MongoDB connection.

For Spring Boot 4:

```properties
spring.mongodb.uri=${MONGODB_URI}
```

Configure `MONGODB_URI` in the IDE or runtime environment.

Example format:

```text
mongodb+srv://USERNAME:PASSWORD@CLUSTER.mongodb.net/DATABASE
```

Do not commit database credentials to GitHub.

Additional authentication and inter-service environment variables
will be documented during integration.

### 4. Run Backend Services

Run each Spring Boot application separately.

| Service | Port |
|---------|------|
| Auth Service | 8081 |
| Movie Service | 8082 |
| Booking Service | 8083 |

For example:

```bash
cd backend/movie-service
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 5. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Open the local URL displayed by Vite.

## Docker Setup

TODO: Add the finalized Docker Compose instructions once
container integration is complete.

Planned services:
- auth-service
- movie-service
- booking-service
- frontend

MongoDB Atlas is hosted remotely and does not require a local
MongoDB container.

## API Documentation

### Authentication Service

TODO: Add verified authentication endpoints.

### Movie Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/showtimes | Retrieve all showtimes |
| GET | /api/showtimes/{id} | Retrieve showtime details |
| GET | /api/showtimes/movie/{movieId} | Showtimes for a movie |
| POST | /api/showtimes | Create a showtime |
| PUT | /api/showtimes/{id} | Update a showtime |
| DELETE | /api/showtimes/{id} | Delete a showtime |

Additional movie and screen endpoints will be documented
by their feature owners.

### Booking Service

TODO: Add verified booking and seat endpoints.

## Concurrency and Multithreading

The application demonstrates concurrency through simultaneous
seat reservation requests.

### Problem

Multiple users may attempt to reserve the same seat for the same
showtime at the same time.

### Expected Behavior

- One user successfully reserves the seat.
- Competing requests receive a conflict response.
- The seat cannot be reserved twice.
- The database remains consistent.

### Implementation

TODO: Document the final locking mechanism, MongoDB atomic
operations, ExecutorService implementation, and test results.

## Security

The final application will use:

- JWT Authentication
- Protected REST endpoints
- Role-based authorization
- Secure password storage
- Environment variables for sensitive configuration

Temporary development-only security settings must be removed
before the final demonstration.

## Screenshots

TODO: Add screenshots after frontend integration.

Suggested screenshots:

1. Login page
2. Movie listing page
3. Showtime selection
4. Cinema seat selection
5. Booking confirmation
6. Concurrency demonstration

## Challenges and Solutions

TODO: Document challenges faced during the hackathon.

Potential topics:
- Microservice communication
- MongoDB Atlas connectivity
- JWT integration
- Concurrent seat reservations
- Docker configuration
- Git merge conflicts

## Future Enhancements

- Online payment integration
- Real-time seat availability using WebSockets
- Booking confirmation emails
- Reservation expiration
- Advanced cinema management
- Cloud deployment
