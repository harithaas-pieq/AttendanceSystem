# OVERVIEW OF THE ATTENDANCE SYSTEM

When client/user makes a request like get/post, this request comes to the Jetty HTTP server.
Dropwizard uses Jetty, listens for the HTTP request on the port (e.g., localhost:6080), and handles connections, request/response. This request is forwarded to Jersey.
Dropwizard uses this Jersey on top of Jetty. The HTTP requests are made to come to the resource class, and it gets the correct method like get/post and parses the query.
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

# MAIN COMPONENTS

**AttendanceApplication.kt**

1. `main()` starts the app, Dropwizard loads configuration.
2. `initialize()` sets up JSON handling for Kotlin and Java Time.
3. `run()` sets up the database, services, and REST resources.
4. Resources handle HTTP requests, call services, which use DAOs to access the database.
5. health checks are configured, and the server starts listening for requests.

**AttendanceConfiguration.kt**

* YAML config file is loaded into AttendanceConfiguration.
* It calls `initialize()` to wire up JSON (Jackson) handling, bundles, etc.
* It calls `run()` to build app like DB, DAO, Service, Resource, health checks, filters.
* Starts Jetty webserver with two ports: application port and admin port.

**DAOs**

* These are Data Access Objects, they communicate with the DB and act as connectors between the backend and the database.

**DTOs**

* These are Data Transfer Objects and are used between the client and the application.

**Resource**

* These are for giving HTTP responses. There is a specific template called `ApiResponse.kt` which is used by both attendance and employee to give response in a proper defined format.

**Service**

* This contains the services or the core logic of the application. The resource class calls the classes defined in the service class, and the functions are executed.

**Models**
* This is how it gets stored in the DB

**Requests**
* This is how the client sends the request
  
**HealthCheck**

* To see healthcheck, use: `http://localhost:6081/healthcheck`
* Healthcheck must use the admin port.
  `{
  "basic": {
    "healthy": true,
    "duration": 0,
    "timestamp": "2025-08-25T18:48:27.010+05:30"
  },
  "deadlocks": {
    "healthy": true,
    "duration": 0,
    "timestamp": "2025-08-25T18:48:27.011+05:30"
  },
  "postgresql": {
    "healthy": true,
    "duration": 20,
    "timestamp": "2025-08-25T18:48:27.010+05:30"
  }
}`



# Running the application

1. Run Podman Desktop
2. Make sure pgAdmin is executing
3. Execute the command: `./gradlew run` in the terminal 



# Final Flow

Basically, this is what happens behind the scene:

* Client sends HTTP request
* Resource Layer receives request and deserializes JSON
* Service Layer validates and applies business rules
* DAO Layer executes queries against the database
* Service Layer returns result to Resource
* Finally, Resource Layer returns a JSON response to the client

---
