# Mobile App with React Native and Spring Boot

This project consists of a React Native mobile app frontend and a Spring Boot backend with PostgreSQL database.

## Backend Setup

### Prerequisites
- Java 11 or higher
- Maven
- PostgreSQL

### Database Setup
1. Install PostgreSQL if you haven't already
2. Create a new database named "mobileapp"
3. Update the database credentials in `src/main/resources/application.properties` if needed

### Running the Backend
1. Navigate to the project root directory
2. Run the following command:
```bash
mvn spring-boot:run
```
The backend will start on http://localhost:8080

## Frontend Setup

### Prerequisites
- Node.js and npm
- React Native CLI
- Android Studio (for Android development)
- Android SDK

### Running the Frontend
1. Navigate to the mobile-app directory:
```bash
cd mobile-app
```

2. Install dependencies:
```bash
npm install
```

3. Start the Metro bundler:
```bash
npx react-native start
```

4. In a new terminal, run the Android app:
```bash
npx react-native run-android
```

## Features

### Authentication
- User registration with username, email, password, phone number, and birthday
- Login with username/email and password
- JWT-based authentication
- Password visibility toggle
- Dark/light mode support

### User Interface
- Modern and clean design
- Responsive layout
- Dark/light mode toggle
- Form validation
- Error handling

## API Endpoints

### Authentication
- POST /api/auth/register - Register a new user
- POST /api/auth/login - Login and get JWT token

## Security
- Password encryption using BCrypt
- JWT token-based authentication
- CORS enabled for mobile app
- Input validation
- SQL injection prevention through JPA 