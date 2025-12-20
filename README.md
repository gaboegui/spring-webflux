# Spring WebFlux with MongoDB Reactive

A Spring Boot application demonstrating reactive programming with Spring WebFlux, Thymeleaf, and MongoDB Reactive. This project showcases various reactive patterns including data streaming, backpressure handling, and reactive templates.

## Features

- **Reactive REST API**: Fully non-blocking REST endpoints using Spring WebFlux
- **Reactive MongoDB**: Uses Spring Data MongoDB Reactive for database operations
- **Reactive Thymeleaf Templates**: Server-side rendering with reactive data streaming
- **Service Layer Architecture**: Clean separation with service interface and implementation
- **Multiple Data Streaming Patterns**:
  - Standard reactive streaming
  - Chunked response handling with backpressure
  - Reactive Data Driver for controlled data flow
  - Delayed element streaming for demonstration
- **Automatic Test Data**: Populates MongoDB with sample products on application startup

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB 4.0+ (running locally on default port 27017)
- IDE with Spring Boot support (recommended: IntelliJ IDEA or VS Code)

## Project Structure

```
src/main/java/com/egui/gabo/webflux/app/
├── SpringWebfluxApplication.java      # Main application class with test data setup
├── controller/
│   ├── ProductController.java         # Thymeleaf web controller
│   └── ProductRestController.java     # REST API controller
├── service/
│   ├── ProductService.java           # Service interface
│   └── impl/
│       └── ProductServiceImpl.java   # Service implementation
├── models/
│   ├── document/
│   │   └── Product.java              # MongoDB document entity
│   └── repository/
│       └── ProductRepository.java    # Reactive MongoDB repository
src/main/resources/
├── application.properties            # Application configuration
└── templates/
    ├── listProducts.html            # Main product listing template
    └── list-chunked.html            # Chunked response template
```

## Getting Started

### 1. Clone and Setup

```bash
git clone <repository-url>
cd spring-webflux
```

### 2. Start MongoDB

Ensure MongoDB is running locally on port 27017:

```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Or start your local MongoDB service
mongod
```

### 3. Build and Run

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or build and run
./mvnw clean package
java -jar target/spring-webflux-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### 4. Verify Setup

Once running, the application will:
- Create a MongoDB database named `product_db`
- Drop and recreate the `products` collection
- Insert 7 sample products with prices
- Start serving on port 8080

## API Endpoints

### Web Interface (Thymeleaf)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Standard product listing with uppercase names |
| `/list` | GET | Reactive Data Driver with 2-element chunks and 1-second delays |
| `/list-huge` | GET | Large dataset (500x repeated) for performance testing |
| `/list-chunked` | GET | Chunked response with backpressure (1024-byte chunks) |

### REST API

| Endpoint | Method | Description | Response |
|----------|--------|-------------|----------|
| `/api/products` | GET | Get all products | `Flux<Product>` |
| `/api/products/{id}` | GET | Get product by ID | `Mono<Product>` |

### Product Model
```json
{
  "id": "string",
  "name": "string",
  "price": "number",
  "createAt": "date"
}
```

## Reactive Patterns Demonstrated

### 1. Standard Reactive Streaming
```java
@GetMapping("/")
public String listarProductos(Model model) {
    Flux<Product> products = productService.findAll();
    model.addAttribute("products", products);
    return "listProducts";
}
```

### 2. Reactive Data Driver with Backpressure
```java
@GetMapping("/list")
public String listarReactiveDataDriver(Model model) {
    Flux<Product> products = productService.findAll();
        .delayElements(Duration.ofSeconds(1));
    
    model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
    return "listProducts";
}
```

### 3. Chunked Response Handling
Configured in `application.properties`:
```properties
spring.thymeleaf.reactive.max-chunk-size=1024
spring.thymeleaf.reactive.chunked-mode-view-names=*chunked*
```

### 4. Large Dataset Handling
```java
@GetMapping("/list-huge")
public String listarProductosFull(Model model) {
    Flux<Product> products = productService.findAll()
        .repeat(500);  // Repeat 500 times for large dataset

    model.addAttribute("products", products);
    return "listProducts";
}
```

## Service Layer Architecture

The application follows a layered architecture with a clear separation of concerns:

### Service Interface (`ProductService.java`)

### Service Implementation (`ProductServiceImpl.java`)


### Key Benefits
1. **Separation of Concerns**: Controllers handle HTTP requests/responses, services contain business logic
2. **Testability**: Services can be easily mocked and tested independently
3. **Reusability**: Service methods can be reused across different controllers
4. **Maintainability**: Business logic changes are isolated to the service layer

## Configuration

### application.properties
```properties
spring.application.name=spring-webflux
spring.data.mongodb.uri=mongodb://localhost:27017/product_db

# Thymeleaf reactive configuration
spring.thymeleaf.reactive.max-chunk-size=1024
spring.thymeleaf.reactive.chunked-mode-view-names=*chunked*
```

### Dependencies (pom.xml)
- Spring Boot 3.5.9
- Spring WebFlux (reactive web)
- Spring Data MongoDB Reactive
- Thymeleaf (with reactive support)
- Reactor Test (for testing)
- Spring Boot DevTools

## Development

### Running Tests
```bash
./mvnw test
```

### Code Style
The project follows standard Java conventions and Spring Boot best practices.

### Database Operations
- The application uses reactive MongoDB operations
- All database calls return `Mono` or `Flux` types
- Automatic test data insertion on startup (see `SpringWebfluxApplication.run()`)

## Sample Data

On application startup, the following products are inserted:

| Name | Price |
|------|-------|
| TV LG 4k 52in | $500.99 |
| Camara Sony | $500.99 |
| Apple watch | $200.99 |
| Laptop Lenovo | $700.99 |
| Webcam Logitech | $199.99 |
| Camara Sony | $500.99 |
| TV Haisen 4k 52 | $600.99 |

## Performance Considerations

1. **Backpressure Handling**: The application demonstrates proper backpressure handling through chunked responses
2. **Memory Efficiency**: Reactive streams process data as it becomes available, reducing memory footprint
3. **Non-blocking I/O**: All database and network operations are non-blocking
4. **Connection Pooling**: Reactive MongoDB driver manages connections efficiently

## Troubleshooting

### Common Issues

1. **MongoDB Connection Failed**
   ```
   Error: Cannot connect to MongoDB at localhost:27017
   ```
   **Solution**: Ensure MongoDB is running: `mongod` or `docker start mongodb`

2. **Port Already in Use**
   ```
   WebServerException: Port 8080 already in use
   ```
   **Solution**: Change port in `application.properties` or kill the process using port 8080

3. **Java Version Error**
   ```
   UnsupportedClassVersionError
   ```
   **Solution**: Ensure Java 17 or higher is installed: `java -version`

### Logs
Check application logs for detailed error information. The application uses SLF4J with Logback.

## Learning Resources

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor Guide](https://projectreactor.io/docs/core/release/reference/)
- [Reactive MongoDB with Spring](https://spring.io/guides/gs/accessing-data-mongodb/)
- [Thymeleaf + WebFlux](https://www.thymeleaf.org/doc/articles/thymeleafspringwebflux.html)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot team for excellent reactive support
- MongoDB for reactive driver implementation
- Project Reactor for reactive streams implementation

---

**Note**: This is a demonstration project for learning Spring WebFlux and reactive programming patterns. Not recommended for production use without additional security, validation, and error handling.
