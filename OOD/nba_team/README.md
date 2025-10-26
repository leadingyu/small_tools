# NBA HTTP Client Application

A Java application that demonstrates HTTP client functionality for fetching NBA team information from a REST API.

## Features

- HTTP client for NBA team API with pagination support
- Team information model with name and revenue fields
- Pagination response handling
- Revenue calculation across multiple pages
- Comprehensive test suite with mock data

## Project Structure

```
src/
├── main/java/com/example/nba/
│   ├── NbaApplication.java          # Main application class
│   ├── NbaApiClient.java           # HTTP client service
│   ├── TeamInfo.java               # Team information model
│   └── PaginationResponse.java     # Pagination response wrapper
└── test/java/com/example/nba/
    └── NbaApiClientTest.java       # Test suite with mock data
```

## API Endpoint

- **Base URL**: `http://localhost:3000/hello`
- **Method**: GET
- **Pagination**: Cursor-based pagination with 20 teams per page
- **Response Format**: JSON with pagination metadata

## Models

### TeamInfo
- `name` (String): Team name
- `revenue` (Long): Team revenue in dollars

### PaginationResponse
- `data` (List<TeamInfo>): List of teams for current page
- `page` (int): Current page number
- `totalPages` (int): Total number of pages
- `totalElements` (int): Total number of teams
- `hasNext` (boolean): Whether there are more pages

## Usage

### Running the Application

```bash
# Compile the project
mvn compile

# Run the main application
mvn exec:java -Dexec.mainClass="com.example.nba.NbaApplication"

# Run tests
mvn test
```

### Example Usage

```java
NbaApiClient client = new NbaApiClient();

// Fetch teams from a specific page
PaginationResponse response = client.getTeams(0);

// Fetch all teams across all pages
List<TeamInfo> allTeams = client.getAllTeams();

// Calculate total revenue
Long totalRevenue = client.calculateTotalRevenue();
```

## Test Data

The test suite includes comprehensive tests with mock data for 2 pages:
- **Page 1**: 20 teams with revenue ranging from $100M to $195M
- **Page 2**: 18 teams with revenue ranging from $80M to $131M
- **Total**: 38 teams with combined revenue of $4,849,000,000

### Test Coverage
- **Mock Data Tests**: Tests business logic with static mock data
- **JSON Serialization**: Tests API response serialization/deserialization
- **Revenue Calculation**: Tests total revenue calculation across multiple pages
- **Object Creation**: Tests TeamInfo and PaginationResponse object creation

## Dependencies

- Apache HttpClient 4.5.14
- Jackson Databind 2.15.2
- SLF4J Simple 2.0.7
- JUnit Jupiter 5.9.3
- Mockito 5.3.1

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Build and Run

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package

# Run the application
java -cp target/classes com.example.nba.NbaApplication
```
