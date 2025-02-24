# 🏎 RaceApp - Racing Management System

**RaceApp** is a **Spring Boot** REST API designed for managing races, cars, and pilots.  
It allows users to register races, pilots, and cars while tracking their participation.

## 🚀 Features  
✅ Manage races (`Race`)  
✅ Manage pilots (`Pilot`)  
✅ Manage cars (`Car`)  
✅ `@ManyToMany` relationships between races, pilots, and cars  
✅ `@OneToMany` relationship between pilots and cars  
✅ RESTful API for CRUD operations  

## 🛠️ Technologies  
- **Java 17**  
- **Spring Boot 3**  
- **Spring Data JPA (Hibernate)**  
- **Spring Web**  
- **H2/PostgreSQL** (configurable database)  
- **Jackson (JSON processing)**  

## 📌 Project Structure  
```
RaceApp/
│── src/main/java/com/example/raceapp
│   ├── model/           # Entity classes (Race, Pilot, Car)
│   ├── dao/             # Data access layer (DAO interfaces)
│   ├── dao/impl/        # DAO implementations using JPA
│   ├── service/         # Business logic layer
│   ├── controller/      # REST API endpoints
│   ├── RaceAppApplication.java  # Main application entry point
│── src/main/resources/
│   ├── application.properties  # Spring Boot configuration
│── pom.xml  # Maven dependencies
```

## 🔧 Installation & Running  
1. **Clone the repository:**  
   ```sh
   git clone https://github.com/yourusername/RaceApp.git
   cd RaceApp
   ```
2. **Configure the database:**  
   Modify `src/main/resources/application.properties` to use your preferred database.  

3. **Build and run the application:**  
   ```sh
   mvn spring-boot:run
   ```

4. **API is available at:**  
   ```
   http://localhost:8080
   ```

## 📡 API Endpoints  
### 🎯 Race API (`/races`)  
- `POST /races` - Create a new race  
- `GET /races/{id}` - Get a race by ID  
- `GET /races/all` - Get all races  
- `PUT /races/{id}` - Update a race  
- `PATCH /races/{id}` - Partially update a race  
- `DELETE /races/{id}` - Delete a race  

### 🏁 Pilot API (`/pilots`)  
- `POST /pilots` - Create a new pilot  
- `GET /pilots/{id}` - Get a pilot by ID  
- `GET /pilots/all` - Get all pilots  
- `PUT /pilots/{id}` - Update a pilot  
- `PATCH /pilots/{id}` - Partially update a pilot  
- `DELETE /pilots/{id}` - Delete a pilot  

### 🚗 Car API (`/cars`)  
- `POST /cars` - Create a new car  
- `GET /cars/{id}` - Get a car by ID  
- `GET /cars/all` - Get all cars  
- `GET /cars?brand=Ferrari` - Get cars by brand  
- `PUT /cars/{id}` - Update a car  
- `PATCH /cars/{id}` - Partially update a car  
- `DELETE /cars/{id}` - Delete a car  

## 🏗️ Future Improvements  
- 🏆 Implement race results tracking  
- 📊 Add statistics and leaderboards  
- 📌 Support for multiple race types  
