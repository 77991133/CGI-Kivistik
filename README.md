
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
   ```
Create database:
```
CREATE DATABASE restaurant;
```

Configure PostgreSQL database in application.properties, set your port accordingly:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant 
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=false
```
Build and run backend:
```
./mvnw spring-boot:run
```

### Frontend Setup

Navigate to the frontend directory:

```
cd ../frontend
```
Install dependencies:
```
npm install
```
Run frontend:
```
npm run dev
```
Open browser at http://localhost:5173

### Database Seeding (First Run)

The application requires table data in the database to function correctly.

1. Open `frontend/src/components/TableSelector.vue`.
2. Uncomment the `importTables()` function call inside the `onMounted` hook (around line 128).
3. Run the application and open the booking page once.  
   This will parse the SVG and populate the database with table data.
4. After the tables have been imported, comment out `importTables()` again to prevent duplicate entries on subsequent reloads.

## Project Reflection

The estimated time spent on this project was approximately **20–30 hours**. Looking back, the most challenging part was integrating the database with the backend. At the time it felt quite complex, but in retrospect the solution is clear and the process helped me learn a lot about how the different parts of the system interact.

A significant amount of time was spent updating the project to the latest **Java LTS version**. After trying multiple configuration changes without success, I eventually found a solution in an online forum suggesting that the issue could be related to the IDE version. Reinstalling **IntelliJ IDEA** to the latest version resolved the problem.

Originally, I planned to use **Flyway** for database migrations, but due to time constraints and the need to move the project forward, I decided to postpone implementing it.

All code comments in the project were generated with the assistance of AI tools such as ChatGPT and Gemini. AI was also used for consulting on project structure and for answering technical questions about implementation details.

Working with **SVG files to create clickable elements** was a new and exciting experience for me. It introduced a different way of thinking about interactive UI components and was one of the more interesting parts of the frontend development process.

I had previous experience working with Vue and databases, but Spring Boot was new to me. While working on this project, many concepts that I had learned earlier started to come together in practice. Some topics that had not fully settled during the courses became clearer while implementing them in my own project. Revisiting these concepts and applying them again helped reinforce my understanding and made the overall architecture and workflow much clearer.