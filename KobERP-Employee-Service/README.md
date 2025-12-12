# KobERP Employee Service

Spring Boot backend service for managing employees in the KobERP system.

## Features

- ✅ Full CRUD operations for employees
- ✅ Search and filtering (by department, position, status)
- ✅ Employee statistics and analytics
- ✅ Bulk operations (import/delete)
- ✅ Auth0 authentication (JWT)
- ✅ Soft delete support
- ✅ Comprehensive validation

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security + OAuth2 Resource Server**
- **Auth0 JWT Authentication**
- **PostgreSQL / H2 (dev)**
- **Lombok**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (or use H2 for development)
- Auth0 account with configured API

## Getting Started

### 1. Clone the repository

```bash
cd KobERP-Employee-Service
```

### 2. Configure Auth0

Create an API in your Auth0 dashboard and update `src/main/resources/application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://YOUR_AUTH0_DOMAIN/
spring.security.oauth2.resourceserver.jwt.audiences=YOUR_AUTH0_API_IDENTIFIER
```

### 3. Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/koberp_employees
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Or use the dev profile with H2:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Build and Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

## API Documentation

### Authentication

This service uses Auth0 for authentication. Your frontend should:

1. Authenticate users with Auth0
2. Get an access token from Auth0
3. Send the token with each request:

```
Authorization: Bearer {auth0-access-token}
```

All API endpoints (except H2 console in dev mode) require authentication.

### Employee Endpoints

#### CRUD Operations
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create new employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee (soft delete)

#### Search & Filter
- `GET /api/employees/search?q={query}` - Search employees
- `GET /api/employees/department/{dept}` - Filter by department
- `GET /api/employees/position/{pos}` - Filter by position
- `GET /api/employees/status/{status}` - Filter by status

#### Statistics
- `GET /api/employees/stats` - Get employee statistics
- `GET /api/employees/count` - Get total count
- `GET /api/employees/departments` - List all departments
- `GET /api/employees/positions` - List all positions

#### Status Management
- `PATCH /api/employees/{id}/status` - Update status
- `PATCH /api/employees/{id}/activate` - Activate employee
- `PATCH /api/employees/{id}/deactivate` - Deactivate employee

#### Bulk Operations
- `POST /api/employees/bulk` - Create multiple employees
- `DELETE /api/employees/bulk` - Delete multiple employees

### Request Examples

#### Create Employee
```bash
POST /api/employees
Authorization: Bearer {auth0-token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+905551234567",
  "position": "Software Engineer",
  "department": "IT",
  "salary": 75000,
  "hireDate": "2024-01-15",
  "dateOfBirth": "1990-05-20",
  "address": "123 Main St, Istanbul",
  "emergencyContact": {
    "name": "Jane Doe",
    "phone": "+905559876543",
    "relation": "Spouse"
  },
  "status": "ACTIVE",
  "managerId": 1,
  "skills": ["Java", "Spring Boot", "PostgreSQL"],
  "notes": "Experienced developer"
}
```

#### Update Status
```bash
PATCH /api/employees/1/status
Authorization: Bearer {auth0-token}
Content-Type: application/json

{
  "status": "ON_LEAVE"
}
```

## Employee Status Types

- `ACTIVE` - Currently employed and working
- `INACTIVE` - No longer with the company
- `ON_LEAVE` - Temporarily on leave

## Security

All endpoints require JWT authentication via Auth0. The backend validates:
- JWT signature
- Token expiration
- Issuer (Auth0 domain)
- Audience (API identifier)

## Auth0 Configuration

1. Create an Auth0 account at https://auth0.com
2. Create a new API in your Auth0 dashboard
3. Note the API Identifier (audience)
4. Note your Auth0 domain (issuer)
5. Update `application.properties` with these values

## Database Schema

The application uses the following main entities:

- **Employee** - Main employee data
- **EmergencyContact** - Embedded emergency contact info
- **EmployeeStatus** - Enum for employee status

## Error Handling

The API returns standardized error responses:

```json
{
  "status": 404,
  "message": "Employee not found with id: 123",
  "timestamp": "2024-11-24T10:30:00",
  "path": "/api/employees/123"
}
```

## Validation

All requests are validated. Validation errors return:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "Invalid email format",
    "firstName": "First name is required"
  },
  "timestamp": "2024-11-24T10:30:00",
  "path": "/api/employees"
}
```

## Development

### Using H2 Console

When running with `dev` profile:

1. Access H2 Console: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: (leave empty)

## Project Structure

```
src/main/java/com/koberp/employeeservice/
├── controller/          # REST Controllers
├── service/            # Business Logic
├── repository/         # Data Access Layer
├── model/              # Entity Classes
├── dto/                # Data Transfer Objects
├── mapper/             # Entity-DTO Mappers
├── security/           # Security Configuration (Auth0)
├── exception/          # Exception Handling
└── EmployeeServiceApplication.java
```

## License

© 2024 KobERP. All rights reserved.
