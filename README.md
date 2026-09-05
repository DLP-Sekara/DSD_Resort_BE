# DSD Resort - Backend (Spring Boot)

This repository contains the Backend API for the DSD Resort Management System, built with Spring Boot and Java. It serves as the core backend, handling everything from reservations, kitchen operations (KDS), and inventory to AI-based demand forecasting integration.

## 🚀 Tech Stack
- **Framework:** Java 17, Spring Boot 3
- **Database:** MySQL
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security + JWT Authentication
- **External Integrations:** 
  - Python ML Microservices (Demand Forecasting, NLP Sentiment Analysis)
  - OpenWeather API
  - JavaMailSender (OTP / Notifications)

## 📦 Key Modules & Features
* **Auth & Users:** Role-based access control, JWT tokens, and OTP email flows.
* **Reservations & Guests:** Room booking, meal plan allocation, and guest profiles.
* **Restaurant & KDS (Kitchen Display System):** Restaurant order management, real-time ticket tracking, dish BOM (Bill of Materials) processing.
* **Inventory & BOM Management:** Raw material stock, recipe templates, auto-deduction of inventory when cooking.
* **AI Demand Forecasting:** Connects to a Python microservice to predict required food quantities based on weather and occupancy.
* **Guest Reviews & NLP:** Connects to a Python NLP service to analyze the sentiment of guest reviews.

## ⚙️ Setup & Configuration

### Prerequisites
- Java 17+
- Maven 
- MySQL Server

### Environment Variables
Configure the following environment variables (or set them in `application.properties` / `.env`):

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/dsd_resort
DB_USERNAME=root
DB_PASSWORD=your_password

# Authentication
JWT_SECRET=your_jwt_secret_key_here

# Email Setup (for OTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Python Microservices URLs
FORECAST_SERVICE_URL=http://127.0.0.1:8000/api/v1/forecast/
NLP_SERVICE_URL=http://127.0.0.1:8000/api/v1/nlp/analyze

# OpenWeather API (for Forecasting)
OPENWEATHER_API_KEY=your_api_key_here
OPENWEATHER_CITY=Colombo,LK
```

## 🛠️ Running the Application

1. **Clone the repository.**
2. **Setup the Database:** Ensure MySQL is running and the database specified in `DB_URL` is created. Spring JPA will automatically create the tables (`spring.jpa.hibernate.ddl-auto=update`).
3. **Run via Maven:**
   ```bash
   mvn spring-boot:run
   ```
4. The API will be available at `http://localhost:8080`.

## 📂 Project Structure
- `controller/`: REST API endpoints.
- `services/`: Business logic and external API communication.
- `repository/`: Spring Data JPA Interfaces.
- `entity/`: Database entities (Rooms, Orders, Reservations, etc.).
- `dto/`: Data Transfer Objects for request/response bodies.
- `config/`: Application configuration (CORS, Security, WebSockets).
- `advisor/`: Global exception handlers.
