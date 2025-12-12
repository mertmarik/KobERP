# KobERP Sales Service

Spring Boot backend service for managing sales with Auth0 authentication and PostgreSQL database.

## Features

- ✅ Auth0 JWT Authentication & Authorization
- ✅ Sales CRUD Operations
- ✅ Automatic Stock Quantity Management
- ✅ PostgreSQL Database Integration
- ✅ Swagger/OpenAPI Documentation
- ✅ RESTful API Design
- ✅ Exception Handling

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Security with OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- Maven

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Auth0 Account

## Configuration

### 1. Database Setup

PostgreSQL database configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/koberpDB?currentSchema=erp
spring.datasource.username=mert
spring.datasource.password=admin
```

Create the required tables:

```sql
CREATE TABLE stock_items (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    item_name VARCHAR(255),
    unit_price DECIMAL(10, 2),
    description VARCHAR(500)
);

CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
    profit DECIMAL(10, 2),
    last_sale_date DATE,
    sale_quantity VARCHAR(255),
    document_uploaded BOOLEAN NOT NULL DEFAULT FALSE,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(255),
    uploaded_at TIMESTAMP NOT NULL,
    FOREIGN KEY (stock_id) REFERENCES stock_items(id)
);
```

### 2. Auth0 Configuration

1. Create an Auth0 account at https://auth0.com
2. Create a new API in Auth0 Dashboard
3. Note down:
   - Domain (e.g., `your-tenant.auth0.com`)
   - API Identifier (Audience)
   - Client ID (for testing)

### 3. Application Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/koberpDB?currentSchema=erp
spring.datasource.username=mert
spring.datasource.password=admin

# Auth0 Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://dev-0fctiydjurknv8h4.us.auth0.com/
spring.security.oauth2.resourceserver.jwt.audiences=https://koberp

# Server Configuration
server.port=8086
```

Configuration is already set in `application.properties` file.

## Running the Application

### Using Maven:

```bash
mvn spring-boot:run
```

### Using JAR:

```bash
mvn clean package
java -jar target/sales-service-1.0.0.jar
```

The application will start on `http://localhost:8086`

## API Endpoints

### Sales Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/sales` | Create new sale | Yes |
| GET | `/api/sales` | Get all sales | Yes |
| GET | `/api/sales/{id}` | Get sale by ID | Yes |
| PUT | `/api/sales/{id}` | Update sale | Yes |
| DELETE | `/api/sales/{id}` | Delete sale | Yes |

### Swagger Documentation

Access the interactive API documentation:

- Swagger UI: `http://localhost:8086/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8086/v3/api-docs`

## Authentication

All API endpoints (except Swagger docs) require a valid JWT token from Auth0.

### Getting a Token (for testing):

1. Use Auth0's test application or your frontend
2. Obtain JWT token from Auth0
3. Include in requests:

```bash
curl -X GET http://localhost:8086/api/sales \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Swagger UI:

1. Go to `http://localhost:8086/swagger-ui.html`
2. Click "Authorize" button
3. Enter: `Bearer YOUR_JWT_TOKEN`
4. Test endpoints directly

## API Request Examples

### Create Sale

```bash
POST http://localhost:8086/api/sales
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "stockId": 1,
  "salePrice": 150.00,
  "profit": 50.00,
  "lastSaleDate": "2025-12-08",
  "saleQuantity": "5",
  "documentUploaded": true,
  "customerName": "John Doe",
  "customerPhone": "+1234567890"
}
```

### Update Sale

```bash
PUT http://localhost:8086/api/sales/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "stockId": 1,
  "salePrice": 200.00,
  "profit": 75.00,
  "saleQuantity": "3",
  "customerName": "Jane Smith",
  "customerPhone": "+0987654321"
}
```

### Delete Sale

```bash
DELETE http://localhost:8086/api/sales/1
Authorization: Bearer YOUR_JWT_TOKEN
```

## Business Logic

### Sale Creation:
1. Validates stock exists
2. Checks sufficient quantity available
3. Creates sale record with `uploaded_at` timestamp
4. Decreases stock quantity by sale amount

### Sale Update:
1. Restores original stock quantity
2. Validates new stock availability
3. Updates sale record
4. Adjusts stock quantity based on new sale amount

### Sale Deletion:
1. Finds sale record
2. Restores stock quantity
3. Deletes sale record

## Error Handling

The API returns appropriate HTTP status codes:

- `200 OK` - Successful GET/PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation errors, insufficient stock
- `401 Unauthorized` - Missing/invalid JWT token
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors

## Project Structure

```
src/main/java/com/koberp/sales/
├── config/
│   ├── SecurityConfig.java
│   ├── AudienceValidator.java
│   └── OpenApiConfig.java
├── controller/
│   └── SaleController.java
├── dto/
│   ├── SaleRequest.java
│   └── SaleResponse.java
├── entity/
│   ├── Sale.java
│   └── StockItem.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── InsufficientStockException.java
├── repository/
│   ├── SaleRepository.java
│   └── StockItemRepository.java
├── service/
│   └── SaleService.java
└── SalesServiceApplication.java
```

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -DskipTests
```

## Troubleshooting

### JWT Token Validation Fails
- Verify Auth0 issuer URI is correct
- Ensure audience matches Auth0 API identifier
- Check token expiration

### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials
- Ensure database exists

### Stock Quantity Issues
- Verify `stock_items` table has data
- Check foreign key relationship
- Ensure quantity is sufficient for sale

## License

MIT License

## Support

For issues and questions, contact: support@koberp.com
