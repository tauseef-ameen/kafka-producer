# Kafka Producer Application - Order Event

This is a Java Spring Boot-based Kafka producer application that publishes order events to a Kafka topic. The application is containerized using Docker and supports **Spring Boot 3.4.2's built-in Docker Compose integration**.

## Features
- On application startup, a Kafka topic **`order-event`** is created (configurable via `application.properties`).
- Provides **4 endpoints**: 3 `POST` and 1 `PUT` for order event operations.
- Integrated with **Swagger UI** for API documentation.
- Uses **Spring Kafka** for producing messages.
- Supports **Docker Compose** to start Kafka and the application together.
- Option to start the application using **Fabric8 Docker Plugin**.

---

## API Endpoints

### 1. Create New Order Event
- **URL:** `http://localhost:8080/v1/order`  
- **Method:** `POST`  
- **Request Body:**
```json
{
  "orderId": 0,
  "orderType": "NEW",
  "orderDetails": {
    "quantity": 21,
    "customerName": "Manning",
    "price": 545.25
  }
}
```
**Description:** This endpoint sends order details to Kafka on the topic **`order-event`**.

### 2. Update Order Event
- **URL:** `http://localhost:8080/v1/order`  
- **Method:** `POST`  
- **Request Body:**
```json
{
  "orderId": 1,
  "orderType": "UPDATE",
  "orderDetails": {
    "quantity": 21,
    "customerName": "Manning",
    "price": 545.25
  }
}
```
- **Mandatory Field:** `"orderType": "UPDATE"`  
- **Description:** This endpoint sends an **update order event** to Kafka on the topic **`order-event`**.

### 3. API Documentation
The application provides a **Swagger UI** for easy testing and interaction with the API.  
- **URL:** [Swagger UI](http://localhost:8080/swagger-ui.html)

---

## Running the Application

### 1. Using Spring Boot's Built-in Docker Compose Support
Since the project uses **Spring Boot 3.4.2**, it has built-in support for managing Docker Compose files. The property:
```properties
spring.docker.compose.file=src/main/docker/KRaft-multi-broker.yml
```
ensures that **Kafka and the application start together** with a single command.

To start the application and Kafka in one go:
```sh
mvn spring-boot:run
```
This command:
- Starts the Kafka container.
- Starts the Spring Boot application **only after Kafka is up**, ensuring proper communication.

### 2. Running the Application in the Traditional Way
If you prefer a **manual approach**, follow these steps:

#### Step 1: Start Kafka using Docker Compose
```sh
docker compose -f src/main/docker/KRaft-multi-broker.yml up
```

#### Step 2: Build the Docker Image
```sh
mvn clean install -Pcreateimage
```

#### Step 3: Run the Application
```sh
mvn clean install -Pruncontainer
```
This will:
- Start your application inside a Docker container.
- Ensure communication with the Kafka cluster.

---

## Configuration

The Kafka topic name and other properties are configurable via `application.properties`:
```properties
spring.topic.name=${TOPIC_NAME:order-event}
spring.docker.compose.file=src/main/docker/KRaft-multi-broker.yml
```

---

## License
This project is open-source and available under the **MIT License**.
```
