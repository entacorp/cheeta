# Cheeta Webapp - Quick Start Guide

## Overview

Cheeta Webapp is a modern Vaadin + Kotlin frontend for the Cheeta DevOps platform. It uses a thin API-first architecture to consume the existing server-core REST APIs.

## Project Structure

```
cheeta-webapp/
├── src/main/kotlin/io/cheeta/webapp/
│   ├── Application.kt              # Spring Boot entry point
│   ├── api/
│   │   ├── ApiClient.kt           # Base HTTP client
│   │   └── IssueApiClient.kt      # Issue API implementation
│   └── ui/
│       ├── view/
│       │   ├── HomeView.kt        # Home page
│       │   └── IssuesView.kt      # Issues list view
│       └── layout/
│           └── MainLayout.kt      # Main app layout
├── src/main/resources/
│   ├── application.yml            # Dev configuration (H2 DB)
│   └── application-prod.yml       # Prod configuration (PostgreSQL)
├── build.gradle.kts               # Gradle build configuration
└── gradle.properties              # Gradle properties
```

## Technologies

- **Framework**: Spring Boot 3.2.0
- **UI**: Vaadin 24.2.1
- **Language**: Kotlin 1.9.21
- **HTTP Client**: OkHttp 4.11.0
- **JSON**: Jackson 2.16.1
- **Database**: H2 (dev), PostgreSQL (prod)
- **Java**: JDK 17+

## Getting Started

### Prerequisites

- JDK 17 or newer
- Git

### Development Setup

1. **Clone the repository** (already done)

2. **Navigate to the webapp directory**:
```bash
cd /workspaces/cheeta/cheeta-webapp
```

3. **Run the development server**:
```bash
./gradlew bootRun
```

4. **Access the app**:
```
http://localhost:8080
```

The app uses an embedded H2 database for development, so no external database is needed.

### Build for Production

```bash
./gradlew clean build
```

This creates an executable JAR in `build/libs/cheeta-webapp-0.1.0-SNAPSHOT.jar`

## Configuration

### Development (application.yml)

```yaml
# Uses H2 in-memory database
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect

cheeta:
  api:
    base-url: http://localhost:8081  # Server-core URL
```

### Production (application-prod.yml)

```yaml
# Uses PostgreSQL
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cheeta
    username: cheeta
    password: cheeta_password
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

Run with: `java -jar cheeta-webapp.jar --spring.profiles.active=prod`

## API Integration

### ApiClient

Base class for all API communication. Provides methods:

- `get<T>(path, token)` - GET request
- `post<T>(path, body, token)` - POST request
- `put<T>(path, body, token)` - PUT request
- `delete<T>(path, token)` - DELETE request

All methods handle JSON serialization/deserialization automatically.

### Example: IssueApiClient

```kotlin
@Service
class IssueApiClient(
    @Value("\${cheeta.api.base-url}") baseUrl: String
) : ApiClient(baseUrl) {
    
    fun getIssues(projectId: String): List<IssueDto> {
        return get("/rest/projects/$projectId/issues")
    }
}
```

Usage in views:

```kotlin
@Route("issues", layout = MainLayout::class)
class IssuesView(
    @Autowired private val issueApi: IssueApiClient
) : Div() {
    // Use issueApi.getIssues() to fetch data
}
```

## Views & Navigation

### Current Views

1. **HomeView** (`/`)
   - Welcome page with quick links
   - Starting point for the application

2. **IssuesView** (`/issues`)
   - Issue list with Vaadin Grid
   - Shows sample data (API integration ready)

### Planned Views

- Pull Requests View
- Builds/CI-CD View
- Projects View
- User Dashboard
- Settings View

## Adding New Features

### Step 1: Create API Client

```kotlin
@Service
class ProjectApiClient(
    @Value("\${cheeta.api.base-url}") baseUrl: String
) : ApiClient(baseUrl) {
    fun getProjects(): List<ProjectDto> = get("/rest/projects")
}
```

### Step 2: Create View

```kotlin
@Route("projects", layout = MainLayout::class)
class ProjectsView(
    @Autowired private val projectApi: ProjectApiClient
) : Div() {
    init {
        add(H1("Projects"))
        // Use projectApi to fetch and display data
    }
}
```

### Step 3: Add Navigation Link

Edit `MainLayout.kt` and add to the menu list.

## Common Tasks

### Add a new dependency

Edit `build.gradle.kts`:

```kotlin
dependencies {
    implementation("group:artifact:version")
}
```

Then run: `./gradlew clean build`

### View Gradle tasks

```bash
./gradlew tasks
```

### Run tests

```bash
./gradlew test
```

### Skip tests during build

```bash
./gradlew build -x test
```

### Clean build artifacts

```bash
./gradlew clean
```

## Troubleshooting

### Port 8080 already in use

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use a different port
./gradlew bootRun --args='--server.port=8081'
```

### Gradle build hangs

```bash
# Kill the process and try with more verbose output
pkill -f gradlew
./gradlew bootRun --debug
```

### API connection errors

Check that:
1. Server-core is running on `http://localhost:8081`
2. The API endpoints exist
3. Check logs for detailed error messages

The app gracefully handles API unavailability and shows empty data.

### H2 console

Access at: `http://localhost:8080/h2-console`

Default credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Next Steps

1. ✅ Project scaffolding complete
2. ✅ Basic views created (HomeView, IssuesView)
3. ✅ API client framework in place
4. 🔄 Integrate with server-core APIs (in progress)
5. 🔄 Build more views (PRs, Builds, Projects)
6. 🔄 Add authentication
7. 🔄 Style with Cheeta theme
8. 🔄 Add real-time updates (WebSockets)

## Resources

- **Vaadin Documentation**: https://vaadin.com/docs/latest/
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Kotlin Documentation**: https://kotlinlang.org/docs/home.html
- **API Audit Checklist**: See `API_AUDIT_CHECKLIST.md` in project root

## Support

For issues or questions:

1. Check the troubleshooting section above
2. Review server-core logs
3. Check browser console (F12 in DevTools)
4. Check application logs: `./gradlew bootRun | grep -i error`

---

**Happy Coding!** 🐆
