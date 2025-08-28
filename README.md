# OVERVIEW OF THE ATTENDANCE SYSTEM

This Attendance System allows employees to check-in, check-out, and track working hours. The backend is built with Kotlin, Dropwizard, JDBI, and PostgreSQL, exposing REST APIs for client interaction.

When a client/user makes a request like GET/POST, this request comes to the Jetty HTTP server.
Dropwizard uses Jetty, listens for the HTTP request on the port (e.g., `localhost:6080`), and handles connections, request/response. This request is forwarded to Jersey.

Dropwizard uses Jersey on top of Jetty. The HTTP requests are routed to the resource class, which selects the correct method (GET/POST) and parses the query.

Dropwizard uses Jackson to deserialize/serialize the JSON objects to Kotlin objects (DTO) and vice versa.
This is then passed to the service and then DAO.

**Request flow:**

```
Client (curl/postman/browser)
   -> Jetty fetches it
   -> Jersey (Dropwizard REST)
   -> Resource (your HTTP endpoints)
   -> Service (business logic)
   -> DAO (database queries via JDBI)
   -> Postgres
```

---

# MAIN COMPONENTS

### AttendanceApplication.kt

1. `main()` starts the application.
2. `initialize()` sets up JSON handling for Kotlin and Java Time.
3. `run()` sets up the database, services, and REST resources.
4. Resources handle HTTP requests, call services, which use DAOs to access the database.
5. Health checks are configured, and the server starts listening for requests.

### AttendanceConfiguration.kt

* Loads the YAML configuration.
* Wires up JSON modules (Kotlin, Java Time) and Dropwizard bundles.
* Initializes database connections, DAOs, services, resources, and health checks.
* Starts Jetty with an application port and an admin port.

### DAOs

* The system uses DAOs (Data Access Objects) to interact with the PostgreSQL database.
* `AttendanceDAO` manages check-ins, check-outs, and fetching attendance records.
* `EmployeeDAO` manages creating, fetching, listing, and deleting employee records.

### DTOs

* These are Data Transfer Objects and are used between the client and the application.

### Resource

* These are for giving HTTP responses.
* There is a specific template called `ApiResponse.kt` used by both attendance and employee to provide responses in a standardized format.

### Service

* Contains the core logic of the application.
* The resource class calls functions defined in the service class, which execute the business logic.

### Models

* Represent how information is stored in the database and how it is passed through the application.

  * Attendance: `attendanceId`, `employeeId`, `checkInTime`, `checkOutTime`, `workingTime`
  * Employee: `employeeId`, `firstName`, `lastName`, `role`, `department`, `reportingTo`
  * ApiResponse: Standardized response object for all API endpoints

### Requests

* Defines how clients send requests (`AttendanceRequest`, `EmployeeRequest`).

---

# Health Check

The application exposes a health check endpoint to verify the system status:

* **URL:** `http://localhost:6081/healthcheck`
* **Port:** Admin port
* **Purpose:** Checks the health of the application, database, and other critical components.

**Example response:**

```json
{
  "basic": { "healthy": true, "duration": 0, "timestamp": "2025-08-25T18:48:27.010+05:30" },
  "deadlocks": { "healthy": true, "duration": 0, "timestamp": "2025-08-25T18:48:27.011+05:30" },
  "postgresql": { "healthy": true, "duration": 20, "timestamp": "2025-08-25T18:48:27.010+05:30" }
}
```

* `healthy: true` → Everything is fine
* `duration` → Time it took to perform the check
* `timestamp` → Time of the check

---

# API Endpoints

## Attendance

* `POST /api/v1/attendance/checkin` → Check in an employee
* `PUT /api/v1/attendance/{employeeId}/checkout` → Check out an employee
* `GET /api/v1/attendance` → Get all attendance records
* `GET /api/v1/attendance/employee/{employeeId}` → Get attendance for a specific employee
* `GET /api/v1/attendance/employee/{employeeId}/summary` → Get working hours summary
* `GET /api/v1/attendance/employee/{employeeId}/summary-range?start=<start>&end=<end>` → Get working hours summary for a date range
* `DELETE /api/v1/attendance/{attendanceId}` → Delete an attendance record

## Employees

* `POST /api/v1/employees` → Create a new employee
* `GET /api/v1/employees` → List employees
* `GET /api/v1/employees/{id}` → Get employee by ID
* `DELETE /api/v1/employees/{id}` → Delete employee

---

# Running the application

1. Start PostgreSQL / pgAdmin.
2. Run the application: `./gradlew run`

---

# Final Flow

Basically, this is what happens behind the scenes:

* Client sends HTTP request
* Resource Layer receives request and deserializes JSON
* Service Layer validates and applies business rules
* DAO Layer executes queries against the database
* Service Layer returns result to Resource
* Finally, Resource Layer returns a JSON response to the client


