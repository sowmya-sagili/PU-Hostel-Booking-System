# PU Hostel Booking System

A secure, enterprise-grade Hostel Booking System migrated from Python Flask to a modern Java Spring Boot backend. The application features robust role-based access control, real-time bed availability tracking, digital receipt generation, and SMTP email notifications.

---
live demo: https://www.linkedin.com/posts/sowmya-sagili-6b634130b_paruluniversity-webdevelopment-javascript-activity-7407837275159629825-IFmj?utm_source=share&utm_medium=member_desktop&rcm=ACoAAE7k4qoBmfrigsbVPpIn2O3vjy_JRVgCTNU
## Features
- **Student Portal**:
  - Secure registration and login.
  - Browse available hostels (AC/Non-AC, room capacity, and washroom options).
  - Select floors, rooms, and dynamically filter available beds.
  - Book beds and upload payment proof/receipts.
- **Admin Portal**:
  - Secure dashboard for booking management.
  - View pending student bookings and uploaded payment documents.
  - Approve or cancel student bookings in real-time.
  - Automatically generate PDF receipts and send SMTP email notifications.
- **Security & Session Persistence**:
  - Backed by Spring Security 6 session-based authentication (`JSESSIONID`).
  - Strict role-based URL protection (`ROLE_STUDENT` and `ROLE_ADMIN`).
  - Hashed user passwords in database using BCrypt.

---

## Tech Stack
- **Frontend**: HTML5, CSS3 (Vanilla), JavaScript (ES6), Fetch API (Same-Origin session-integrated)
- **Backend**: Spring Boot 3.3.0, Spring Security 6, Spring Data JPA
- **Database**: MySQL 8.x
- **Utilities**: OpenPDF (PDF Generation), JavaMailSender (Email notifications), Lombok

---

## Directory Structure
```text
PU-Hostel-Booking-System/
├── src/
│   ├── main/
│   │   ├── java/com/parul/hostel/
│   │   │   ├── config/              # General app configuration & MVC setups
│   │   │   ├── controller/          # REST Endpoints (Auth, Booking, Admin)
│   │   │   ├── dto/                 # Request & Response Data Transfer Objects
│   │   │   ├── entity/              # JPA Database Entities (Student, Bed, Room, etc.)
│   │   │   ├── repository/          # Spring Data JPA Repository Interfaces
│   │   │   ├── security/            # Spring Security 6 Configuration
│   │   │   ├── service/             # Business Logic & Helpers (PDF, Email, Upload)
│   │   │   └── HostelBookingApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/              # Static Frontend Web Assets (served at /)
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── assets/
│   │       └── application.properties # Server, Data Source & SMTP configs
│
├── uploads/                         # Persistent storage folder for uploaded proof & PDFs
├── pom.xml                          # Maven build specifications
├── mvnw / mvnw.cmd                  # Maven wrapper executables
└── README.md                        # Documentation
```

---

## Setup & Installation

### 1. Prerequisites
- **Java JDK 17** or higher
- **MySQL Server** 8.x
- **Maven** (optional, wrapper is provided in the repository)

### 2. Database Configuration
1. Open your MySQL client and run the following command to create the database:
   ```sql
   CREATE DATABASE hostel_db;
   ```
2. Open `src/main/resources/application.properties` and update the connection details:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hostel_db
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### 3. SMTP Mail Configuration (Optional)
To send email notifications, update the SMTP details in `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
```

---

## How to Run
Use the Maven Wrapper script in the root directory:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
chmod +x mvnw
./mvnw spring-boot:run
```
The server will start on port `5000` (e.g. [http://localhost:5000](http://localhost:5000)).

---

## Seeding & Credentials
On the first application run, the database will be automatically seeded with default hostel rooms, beds, and a default admin user:

### Admin Credentials:
- **Email**: `admin@parul.com`
- **Password**: `admin123`
- **Role**: `ADMIN`

---

## API Overview

### 1. Authentication (`/api/auth`)
- `POST /api/auth/register` - Create a student account.
- `POST /api/auth/login` - Authenticate credentials and establish an HTTP session.
- `POST /api/auth/logout` - Invalidate session and clear `JSESSIONID` cookie.

### 2. Booking (`/api`)
- `GET /api/hostels` - Fetch all hostel types.
- `GET /api/available_beds` - Fetch available beds inside a specific hostel/floor/room.
- `POST /api/book` - Create a booking reservation (Status: `PENDING`).
- `POST /api/payment/proof` - Upload receipt image/PDF for payment validation.

### 3. Admin Panel Management
- `GET /pending_bookings` - List all bookings awaiting validation.
- `POST /approve_booking/{id}` - Approve the booking, generate PDF, and send notification.
- `POST /cancel_booking/{id}` - Cancel the booking, release the bed, and send cancellation notice.

---


