# Cheeta Webapp Development Guide

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Project Setup](#project-setup)
3. [Running the Application](#running-the-application)
4. [Development Workflow](#development-workflow)
5. [Adding New Features](#adding-new-features)
6. [API Integration](#api-integration)
7. [UI Development](#ui-development)
8. [Testing](#testing)
9. [Deployment](#deployment)
10. [Troubleshooting](#troubleshooting)

## Architecture Overview

### High-Level Design

```
┌─────────────────────────────────────────────┐
│         Cheeta Webapp (This Project)        │
│  Vaadin 24 + Kotlin + Spring Boot 3.2       │
├─────────────────────────────────────────────┤
│ UI Layer (Vaadin Components & Views)        │
│ ├── HomeView, ProjectsView, IssuesView...   │
│ └── Reusable Components (Cards, Grids...)   │
├─────────────────────────────────────────────┤
│ Service Layer (API Clients)                 │
│ ├── ProjectApiClient                        │
│ ├── IssueApiClient                          │
│ ├── PullRequestApiClient                    │
│ └── BuildApiClient                          │
├─────────────────────────────────────────────┤
│ HTTP Client (OkHttp + Jackson)              │
└─────────────────────────────────────────────┘
            ↓ REST API Calls ↓
┌─────────────────────────────────────────────┐
│      Server-Core (Backend API)              │
│  Java + Jersey + Hibernate + PostgreSQL     │
│  ├── Issues Service & REST Resource         │
│  ├── Projects Service & REST Resource       │
│  ├── Pull Requests Service & REST Resource  │
│  └── Build Service & REST Resource          │
└─────────────────────────────────────────────┘
```

### Design Principles

1. **API-First**: All business logic stays in server-core
2. **Thin Views**: UI layer is presentation-only
3. **Type-Safe**: Kotlin provides compile-time safety
4. **Reusable**: Component architecture for DRY code
5. **Testable**: Each layer can be tested independently

## Project Setup

### Prerequisites

- **JDK 17+** - Required for Spring Boot 3.2
- **Git** - For version control
- **~2GB RAM** - For Gradle build
- **~500MB disk** - For dependencies

### Initial Setup

1. **Navigate to project**:
```bash
cd /workspaces/cheeta/cheeta-webapp
```

2. **Run setup script**:
```bash
./setup.sh
```

This will:
- Check Java version
- Download Gradle wrapper
- Create directory structure
- Link to server-core assets (when available)

3. **Verify setup**:
```bash
./gradlew --version
```

## Running the Application

### Development Mode

```bash
./gradlew bootRun
```

Access at: `http://localhost:8080`

**Features**:
- H2 in-memory database (no setup needed)
- Hot reload on code changes
- Full debug output
- Development mode enabled in Vaadin

### Production Build

```bash
./gradlew clean build
```

Produces: `build/libs/cheeta-webapp-0.1.0-SNAPSHOT.jar`

Run with:
```bash
java -jar build/libs/cheeta-webapp-0.1.0-SNAPSHOT.jar
```

Or with PostgreSQL:
```bash
java -jar build/libs/cheeta-webapp-0.1.0-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/cheeta \
  --spring.datasource.username=cheeta \
  --spring.datasource.password=cheeta_password
```

### Running on Different Port

```bash
./gradlew bootRun --args='--server.port=8081'
```

### Running with Logging

```bash
./gradlew bootRun --info
```

Or very verbose:
```bash
./gradlew bootRun --debug
```

## Development Workflow

### Daily Workflow

1. **Start the app**:
```bash
./gradlew bootRun
```

2. **Open in browser**:
```bash
open http://localhost:8080
```

3. **Make code changes** in `src/main/kotlin`

4. **Refresh browser** (Vaadin reloads automatically in dev mode)

5. **Check logs** for any errors

### File Organization

```
src/
├── main/
│   ├── kotlin/io/cheeta/webapp/
│   │   ├── Application.kt          # Main entry point
│   │   ├── api/
│   │   │   ├── ApiClient.kt        # Base HTTP client
│   │   │   ├── ProjectApiClient.kt
│   │   │   ├── IssueApiClient.kt
│   │   │   ├── PullRequestApiClient.kt
│   │   │   └── BuildApiClient.kt
│   │   └── ui/
│   │       ├── layout/
│   │       │   └── MainLayout.kt   # Main app layout with nav
│   │       ├── view/
│   │       │   ├── HomeView.kt
│   │       │   ├── ProjectsView.kt
│   │       │   ├── IssuesView.kt
│   │       │   ├── PullRequestsView.kt
│   │       │   └── BuildsView.kt
│   │       ├── component/          # Reusable components
│   │       └── theme/              # Custom Vaadin theme
│   └── resources/
│       ├── application.yml         # Dev config
│       ├── application-prod.yml    # Prod config
│       └── assets/                 # Icons, images, fonts
└── test/
    └── kotlin/io/cheeta/webapp/    # Unit & integration tests
```

### Code Style

- **Language**: Kotlin 1.9.21
- **Convention**: Google Kotlin Style Guide
- **Formatting**: Spring Boot conventions

### Making Changes

1. **Edit Kotlin files** in `src/main/kotlin`
2. **App auto-reloads** in development mode
3. **Refresh browser** to see changes
4. **Check console** for errors

## Adding New Features

### 1. Create API Client

**File**: `src/main/kotlin/io/cheeta/webapp/api/YourApiClient.kt`

```kotlin
@Service
class YourApiClient(
    @Value("\${cheeta.api.base-url}") baseUrl: String
) : ApiClient(baseUrl) {

    fun getItems(): List<ItemDto> {
        return try {
            get("/rest/items")
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getItem(id: String): ItemDto? {
        return try {
            get("/rest/items/$id")
        } catch (e: Exception) {
            null
        }
    }

    fun createItem(request: CreateItemRequest): ItemDto {
        return post("/rest/items", request)
    }

    fun updateItem(id: String, request: UpdateItemRequest): ItemDto {
        return put("/rest/items/$id", request)
    }

    fun deleteItem(id: String) {
        delete<Unit>("/rest/items/$id")
    }
}

data class ItemDto(
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val createdAt: String = ""
)

data class CreateItemRequest(
    val name: String,
    val description: String = ""
)

data class UpdateItemRequest(
    val name: String? = null,
    val description: String? = null
)
```

### 2. Create View

**File**: `src/main/kotlin/io/cheeta/webapp/ui/view/YourView.kt`

```kotlin
@Route("items", layout = MainLayout::class)
class ItemsView(
    @Autowired private val itemApi: YourApiClient
) : Div() {
    
    init {
        setSizeFull()
        style.set("padding", "20px")
        
        add(
            H1("Items").apply {
                style.set("color", "#F59E0B")
            },
            createGrid()
        )
        
        loadItems()
    }

    private fun createGrid(): Grid<ItemDto> {
        return Grid<ItemDto>().apply {
            addColumn { it.name }.setHeader("Name").setFlexGrow(1)
            addColumn { it.description }.setHeader("Description").setFlexGrow(2)
            
            setItems(emptyList())
            setSizeFull()
        }
    }

    private fun loadItems() {
        try {
            val items = itemApi.getItems()
            // Update grid
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

### 3. Add Navigation

**File**: `src/main/kotlin/io/cheeta/webapp/ui/layout/MainLayout.kt`

Edit the navigation list:
```kotlin
listOf(
    "Home" to "/",
    "Items" to "/items",  // ← Add this
    "Projects" to "/projects",
    ...
)
```

### 4. Test the Feature

1. Restart the app: `./gradlew bootRun`
2. Browse to: `http://localhost:8080/items`
3. Check browser console for errors (F12)
4. Check app logs for API errors

## API Integration

### Connecting to Server-Core

The app expects server-core running on `http://localhost:8081` (configurable in `application.yml`).

### Configuration

**Development** (`application.yml`):
```yaml
cheeta:
  api:
    base-url: http://localhost:8081
    timeout: 30s
```

**Production** (`application-prod.yml`):
```yaml
cheeta:
  api:
    base-url: https://api.cheeta.dev
    timeout: 30s
```

### Error Handling

All API clients gracefully handle failures:

```kotlin
return try {
    get("/rest/items")
} catch (e: Exception) {
    // Log error, return empty list
    emptyList()
}
```

This prevents the UI from crashing if the API is unavailable.

### Type Safety

Using Kotlin data classes ensures compile-time safety:

```kotlin
data class ItemDto(
    val id: String? = null,
    val name: String = "",
    val description: String = ""
)

// Using it:
val items: List<ItemDto> = itemApi.getItems()  // Type-safe!
```

## UI Development

### Using Vaadin

Cheeta uses **Vaadin 24** with server-side rendering:

```kotlin
Div().apply {
    add(
        H1("Title"),
        Button("Click me") { 
            // Server-side event handling
            Notification.show("Clicked!")
        },
        Grid<ItemDto>().apply {
            addColumn { it.name }.setHeader("Name")
            setItems(items)
        }
    )
}
```

### Common Components

```kotlin
// Layouts
Div()           // Container
HorizontalLayout()
VerticalLayout()

// Text
H1("Title")
Paragraph("Text")
Span("Inline")

// Input
TextField("Label")
TextArea("Label")
Checkbox("Option")
ComboBox<String>()

// Display
Grid<T>()       // Table
Image("path")

// Actions
Button("Click") { }
Anchor("url", "Link")
Link("Label", "path")

// Feedback
Notification.show("Message")
Dialog()
```

### Styling

```kotlin
Button("Click").apply {
    style.set("background-color", "#F59E0B")
    style.set("color", "white")
    style.set("padding", "10px 20px")
    addThemeName("primary")  // Use Vaadin theme
}
```

### Layout Example

```kotlin
@Route("dashboard", layout = MainLayout::class)
class DashboardView : Div() {
    init {
        setSizeFull()
        style.set("padding", "20px")
        
        add(
            H1("Dashboard"),
            HorizontalLayout().apply {
                add(
                    createStatsCard("Issues", "42"),
                    createStatsCard("PRs", "8"),
                    createStatsCard("Builds", "128")
                )
            },
            createChart()
        )
    }

    private fun createStatsCard(title: String, value: String): Div {
        return Div().apply {
            style.set("padding", "20px")
            style.set("background", "#F3F4F6")
            style.set("border-radius", "8px")
            style.set("min-width", "150px")
            add(
                Paragraph(title).apply {
                    style.set("margin", "0")
                    style.set("color", "#6B7280")
                },
                H2(value).apply {
                    style.set("margin", "0")
                }
            )
        }
    }

    private fun createChart(): Div {
        return Div().apply {
            style.set("height", "400px")
            style.set("margin-top", "20px")
            add(Paragraph("Chart placeholder"))
        }
    }
}
```

## Testing

### Unit Tests

**File**: `src/test/kotlin/io/cheeta/webapp/api/ProjectApiClientTest.kt`

```kotlin
@SpringBootTest
class ProjectApiClientTest {

    @Autowired
    lateinit var projectApi: ProjectApiClient

    @Test
    fun testGetProjects() {
        val projects = projectApi.getProjects()
        assertNotNull(projects)
    }
}
```

### Running Tests

```bash
./gradlew test
```

Run specific test:
```bash
./gradlew test --tests ProjectApiClientTest
```

## Deployment

### Docker

Build Docker image:
```bash
./gradlew bootBuildImage
docker run -p 8080:8080 cheeta-webapp:0.1.0-SNAPSHOT
```

### Cloud Platforms

#### Heroku
```bash
./gradlew build
git push heroku main
```

#### AWS
```bash
./gradlew build
aws s3 cp build/libs/cheeta-webapp.jar s3://bucket/
```

#### GCP
```bash
./gradlew bootBuildImage
gcloud run deploy cheeta-webapp --image gcr.io/project/cheeta-webapp
```

## Troubleshooting

### Port 8080 Already in Use

```bash
# Kill the process
lsof -i :8080
kill -9 <PID>

# Or use different port
./gradlew bootRun --args='--server.port=8081'
```

### API Connection Errors

Check:
1. Server-core is running: `curl http://localhost:8081/rest/projects`
2. API base URL in `application.yml`
3. Network connectivity

### Build Failures

Clean and rebuild:
```bash
./gradlew clean build -x test
```

With verbose output:
```bash
./gradlew clean build --stacktrace
```

### Out of Memory

Increase heap size:
```bash
export GRADLE_OPTS="-Xmx2048m"
./gradlew bootRun
```

### Hot Reload Not Working

Restart the app:
```bash
# Stop the running process (Ctrl+C)
./gradlew bootRun
```

### Browser Shows Blank Page

1. Check browser console (F12)
2. Check server logs
3. Clear browser cache (Ctrl+Shift+Delete)
4. Try different browser

### Vaadin Not Loading

```bash
./gradlew vaadin
./gradlew bootRun
```

---

**Happy coding!** 🐆

For more help, check:
- [QUICK_START.md](QUICK_START.md) - Quick reference
- [Vaadin Docs](https://vaadin.com/docs/)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Kotlin Docs](https://kotlinlang.org/docs/)
