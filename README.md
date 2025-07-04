# 🛍️ Spring Boot E-commerce API

A robust and scalable RESTful API for e-commerce operations built with Spring Boot and MongoDB.

[![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-6.x-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📑 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Best Practices](#-best-practices)
- [Contributing](#-contributing)

## ✨ Features

- **Product Management**
  - CRUD operations for products
  - Advanced search with multiple criteria
  - Category management
  - Pagination and sorting support
- **Data Validation**
  - Input validation for all endpoints
  - Proper error handling and responses
- **Performance**
  - MongoDB indexing for optimized queries
  - Efficient pagination implementation
- **Security**
  - Environment-based configuration
  - Secure credential management

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data MongoDB**
- **Maven**
- **MongoDB**
- **JUnit 5 & AssertJ**
- **SLF4J & Logback**

## 🚀 Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6+
- MongoDB 6.x
- Your favorite IDE (IntelliJ IDEA recommended)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ecommerce.git
   cd ecommerce
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Configuration

1. Create `application.properties` in `src/main/resources`:
   ```properties
   # MongoDB Configuration
   spring.data.mongodb.host=${MONGODB_HOST:localhost}
   spring.data.mongodb.port=${MONGODB_PORT:27017}
   spring.data.mongodb.database=${MONGODB_DATABASE:ecommerce}
   spring.data.mongodb.username=${MONGODB_USERNAME}
   spring.data.mongodb.password=${MONGODB_PASSWORD}

   # Logging
   logging.level.dac.sushanth.ecommerce=DEBUG
   ```

2. Set environment variables or update properties directly

## 📚 API Documentation

### Product Endpoints

#### Get Products
```http
GET /api/v1/products
```
Query Parameters:
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sort` (optional, default: "productName,asc"): Sort criteria
- `name` (optional): Filter by product name
- `category` (optional): Filter by category
- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price

Response:
```json
{
  "content": [
    {
      "productId": 1,
      "productName": "Example Product",
      "productDescription": "Description",
      "productPrice": 99.99,
      "productCategory": "Electronics",
      "productStock": 10,
      "productImageUrl": "http://example.com/image.jpg"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "number": 0,
  "size": 10
}
```

#### Get Product by ID
```http
GET /api/v1/products/{id}
```

#### Create Product
```http
POST /api/v1/products
```
Request Body:
```json
{
  "productName": "New Product",
  "productDescription": "Description",
  "productPrice": 99.99,
  "productCategory": "Electronics",
  "productStock": 10,
  "productImageUrl": "http://example.com/image.jpg"
}
```

#### Update Product
```http
PUT /api/v1/products/{id}
```
Request Body: Same as Create Product

#### Delete Product
```http
DELETE /api/v1/products/{id}
```

#### Get Categories
```http
GET /api/v1/products/categories
```
Returns a list of unique product categories.

## 🧪 Testing

The project includes comprehensive tests:

- Unit Tests
- Integration Tests
- Repository Tests

Run tests with:
```bash
mvn test
```

## ✅ Best Practices

This project follows several best practices:

1. **Clean Code**
   - SOLID principles
   - Clear naming conventions
   - Proper package structure

2. **Error Handling**
   - Global exception handling
   - Meaningful error messages
   - Proper HTTP status codes

3. **Testing**
   - Unit tests for business logic
   - Integration tests for repositories
   - Comprehensive test coverage

4. **Security**
   - Environment-based configuration
   - No hardcoded credentials
   - Input validation

5. **Performance**
   - Pagination for large datasets
   - MongoDB indexing
   - Efficient queries

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ by Sushanth Kalagi 