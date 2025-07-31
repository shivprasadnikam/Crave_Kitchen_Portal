# Crave Kitchen Portal API

A Spring Boot REST API for managing kitchen recipes, meal planning, and culinary tools.

## Features

- **Recipe Management**: CRUD operations for recipes with search and filtering
- **User Management**: User registration and profile management
- **Advanced Search**: Search recipes by title, cuisine, difficulty, time, and servings
- **RESTful API**: Complete REST API with proper HTTP status codes
- **Security**: Spring Security with stateless authentication
- **Database**: JPA/Hibernate with H2 in-memory database

## Technology Stack

- **Backend**: Spring Boot 3.5.4
- **Database**: H2 (Development), JPA/Hibernate
- **Security**: Spring Security (Stateless)
- **API**: RESTful JSON API
- **Build Tool**: Maven
- **Java Version**: 17

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/crave/kitchen/portal/
│   │       ├── controller/          # REST API controllers
│   │       ├── service/             # Service interfaces
│   │       ├── impl/                # Service implementations
│   │       ├── repository/          # Data access layer
│   │       ├── entity/              # JPA entity classes (database tables)
│   │       ├── dto/                 # Data Transfer Objects
│   │       ├── config/              # Configuration classes
│   │       ├── exception/           # Exception handlers
│   │       └── util/                # Utility classes
│   └── resources/
│       └── application.properties   # Application configuration
└── test/
    └── java/
        └── com/example/crave/kitchen/portal/
            └── controller/          # API test classes
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd Crave_Kitchen_Portal
   ```

2. Build the project:

   ```bash
   mvn clean install
   ```

3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

4. Access the API:
   - API Base URL: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

### H2 Database Console

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## API Endpoints

### Public Endpoints

- `GET /` - Health check and API info
- `GET /api/v1/health` - Health check endpoint
- `GET /api/v1/info` - API information and available endpoints
- `GET /h2-console` - H2 database console

### Authentication Endpoints

- `POST /api/auth/register` - Register new vendor (comprehensive registration with validation)
- `POST /api/auth/register/validate` - Validate registration data before submission
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/profile/{id}` - Get vendorsEntity profile

## Vendor Registration API

The vendor registration API provides comprehensive functionality for restaurant owners to register their businesses on the platform.

### Features

- **Complete Validation**: Email, password, phone, address, and business hours validation
- **Business Hours Management**: Support for flexible operating hours per day
- **Email Verification**: Automatic email verification token generation
- **JWT Token Generation**: Access and refresh tokens for immediate authentication
- **Admin Approval Workflow**: New vendors require admin approval before activation

### Validation Rules

- **Email**: Must be unique and valid format
- **Password**: Minimum 8 characters with uppercase, lowercase, number, and special character
- **Phone**: International format required (+1234567890)
- **Address**: All fields required with valid ZIP code format
- **Business Hours**: Opening time must be before closing time
- **Terms**: Must accept terms and conditions

### Response Structure

The API returns a comprehensive response including:

- User information
- Vendor profile details
- Business hours configuration
- JWT access and refresh tokens
- Next steps for account activation

## Configuration

The application can be configured through `application.properties`:

- **Server Port**: `server.port=8080`
- **Database**: H2 in-memory database for development
- **JPA**: Auto-create tables, show SQL queries
- **API**: JSON responses with proper HTTP status codes

## Development

### Adding New Features

1. Create entity classes in the `entity` package (database tables)
2. Create repository interfaces in the `repository` package
3. Create service interfaces in the `service` package
4. Implement services in the `impl` package
5. Create controllers in the `controller` package
6. Add corresponding tests in the `test` package

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add proper documentation and comments
- Write unit tests for all business logic

## Testing

Run tests using Maven:

```bash
mvn test
```

## Deployment

### Production Configuration

For production deployment, update `application.properties`:

1. Change database configuration to production database
2. Configure proper logging levels
3. Set up security configurations
4. Enable API rate limiting if needed

### Build JAR

```bash
mvn clean package
```

The executable JAR will be created in the `target` directory.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team.
