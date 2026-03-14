
# Restaurant Reservation System 

A full-stack restaurant reservation system built with **Spring Boot**, **PostgreSQL**, and **Vue.js**.  
The system allows customers to request reservations, recommends the best available tables, and handles combined table scenarios.

---


## Features

- Search for available tables by date, time, and preferences (window, accessibility, kids, location).  
- Recommend the best table(s) based on customer request.  
- Combine neighbouring tables if necessary.  
- Generate random test reservations if the database is empty.  

---

## Technologies

- **Backend:** Spring Boot, Java, Spring Data JPA  
- **Database:** PostgreSQL  
- **Frontend:** Vue.js 3, Vite  
- **Build Tool:** Maven  

---

## Setup & Installation


### Backend Setup

Clone the repository:  
   ```bash
   git clone https://github.com/77991133/CGI-Kivistik.git
   cd restaurant-reservation/backend
   ´´´
 Configure PostgreSQL database in application.properties or application.yml:
 ```
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
´´´
Build and run backend:
 ```
./mvnw spring-boot:run
´´´

### Frontend Setup

Navigate to the frontend directory:

Frontend Setup

Navigate to the frontend directory:
```
cd ../frontend
´´´
Install dependencies:
```
npm install
´´´
Run frontend:
```
npm run dev
´´´
Open browser at http://localhost:5173