# Vaadin + Kotlin Migration Implementation Guide

## Step 1: Create the API Client Layer

This is the foundation. All Vaadin views will consume APIs through this layer.

### 1.1 Base API Client

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/api/client/ApiClient.kt`

```kotlin
package io.cheeta.api.client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class ApiClient(
    private val baseUrl: String = "http://localhost:8080/api/v1",
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
) {
    private val client = HttpClient.newBuilder().build()

    inline fun <reified T> get(path: String): T {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl$path"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw ApiException("GET $path failed: ${response.statusCode()}")
        }
        return objectMapper.readValue(response.body(), T::class.java)
    }

    inline fun <reified T> post(path: String, body: Any): T {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl$path"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw ApiException("POST $path failed: ${response.statusCode()}")
        }
        return objectMapper.readValue(response.body(), T::class.java)
    }

    // Similar for PUT, DELETE, PATCH
}

class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
```

### 1.2 Issue API Client

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/api/client/IssueApiClient.kt`

```kotlin
package io.cheeta.api.client

data class IssueDto(
    val id: Long,
    val number: Int,
    val title: String,
    val description: String,
    val state: String,
    val priority: Int,
    val assignee: UserDto?,
    val milestone: String?,
    val labels: List<String>,
    val createdAt: Long,
    val updatedAt: Long
)

data class CreateIssueRequest(
    val title: String,
    val description: String,
    val assigneeId: Long? = null,
    val milestoneId: Long? = null,
    val labelNames: List<String> = emptyList()
)

class IssueApiClient(
    private val api: ApiClient = ApiClient()
) {
    fun getIssues(
        projectId: Long,
        state: String? = null,
        assignee: String? = null,
        limit: Int = 50,
        offset: Int = 0
    ): List<IssueDto> {
        var path = "/projects/$projectId/issues?limit=$limit&offset=$offset"
        if (state != null) path += "&state=$state"
        if (assignee != null) path += "&assignee=$assignee"
        
        val response: Map<String, Any> = api.get(path)
        // Parse response (adjust based on actual API response format)
        return response["data"] as List<IssueDto>
    }

    fun getIssue(projectId: Long, issueNumber: Int): IssueDto {
        return api.get("/projects/$projectId/issues/$issueNumber")
    }

    fun createIssue(projectId: Long, request: CreateIssueRequest): IssueDto {
        return api.post("/projects/$projectId/issues", request)
    }

    fun updateIssue(projectId: Long, issueNumber: Int, request: Map<String, Any>): IssueDto {
        return api.put("/projects/$projectId/issues/$issueNumber", request)
    }

    fun deleteIssue(projectId: Long, issueNumber: Int) {
        api.delete("/projects/$projectId/issues/$issueNumber")
    }
}

// Similar for ProjectApiClient, PullRequestApiClient, BuildApiClient, etc.
```

---

## Step 2: Create Reusable Components

### 2.1 Common Card Component

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/components/cards/IssueCard.kt`

```kotlin
package io.cheeta.components.cards

import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.router.RouterLink
import io.cheeta.api.client.IssueDto

class IssueCard(val issue: IssueDto) : VerticalLayout() {
    init {
        addClassName("issue-card")
        
        val header = HorizontalLayout()
        header.add(
            H4(issue.title).apply { addClassName("issue-title") },
            Span("#${issue.number}").apply { addClassName("issue-number") }
        )
        
        val metadata = HorizontalLayout()
        metadata.add(
            Span("State: ${issue.state}").apply { addClassName("state-${issue.state.lowercase()}") },
            Span("Priority: ${issue.priority}").apply { addClassName("priority-${issue.priority}") }
        )
        
        val description = Paragraph(issue.description).apply { 
            addClassName("issue-description") 
        }
        
        val actions = HorizontalLayout()
        actions.add(
            RouterLink("View", IssueDetailView::class.java, issue.id),
            Button("Edit").apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
        )
        
        add(header, metadata, description, actions)
    }
}
```

### 2.2 Filters Component

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/components/forms/IssueFilters.kt`

```kotlin
package io.cheeta.components.forms

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode

class IssueFilters(
    onFilterChange: (filters: Map<String, String>) -> Unit
) : HorizontalLayout() {
    private val stateSelect = Select<String>()
    private val assigneeField = TextField()
    private val searchField = TextField()
    
    init {
        stateSelect.apply {
            setLabel("State")
            setItems("open", "closed", "all")
            value = "open"
            addValueChangeListener {
                notifyFiltersChanged(onFilterChange)
            }
        }
        
        assigneeField.apply {
            placeholder = "Filter by assignee..."
            valueChangeMode = ValueChangeMode.LAZY
            addValueChangeListener {
                notifyFiltersChanged(onFilterChange)
            }
        }
        
        searchField.apply {
            placeholder = "Search issues..."
            valueChangeMode = ValueChangeMode.LAZY
            addValueChangeListener {
                notifyFiltersChanged(onFilterChange)
            }
        }
        
        val resetButton = Button("Reset") {
            stateSelect.value = "open"
            assigneeField.value = ""
            searchField.value = ""
            notifyFiltersChanged(onFilterChange)
        }
        
        add(stateSelect, assigneeField, searchField, resetButton)
    }
    
    private fun notifyFiltersChanged(onFilterChange: (filters: Map<String, String>) -> Unit) {
        val filters = mapOf(
            "state" to stateSelect.value,
            "assignee" to assigneeField.value,
            "search" to searchField.value
        ).filterValues { it.isNotEmpty() }
        onFilterChange(filters)
    }
}
```

---

## Step 3: Create Views Using the API

### 3.1 Issue List View

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/views/issues/IssueListView.kt`

```kotlin
package io.cheeta.views.issues

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import io.cheeta.api.client.IssueApiClient
import io.cheeta.api.client.IssueDto
import io.cheeta.components.forms.IssueFilters
import io.cheeta.components.layout.AppLayout

@PageTitle("Issues")
@Route(value = "projects/:projectId/issues", layout = AppLayout::class)
class IssueListView(
    private val issueApi: IssueApiClient = IssueApiClient()
) : VerticalLayout(), BeforeEnterObserver {
    
    private var projectId: Long = 0
    private val grid = Grid(IssueDto::class.java)
    private var filters = mapOf<String, String>()
    
    init {
        setSizeFull()
        
        add(H2("Issues"))
        
        // Add filters
        add(IssueFilters { newFilters ->
            filters = newFilters
            loadIssues()
        })
        
        // Configure grid
        grid.apply {
            setSizeFull()
            setColumns("number", "title", "state", "priority")
            getColumnByKey("number").setHeader("#")
            getColumnByKey("state").setHeader("State")
            
            addItemClickListener { event ->
                // Navigate to issue detail
                ui.ifPresent { 
                    it.navigate("issues/${event.item.number}")
                }
            }
        }
        
        add(grid)
        
        // Add button
        val createButton = Button("Create Issue") {
            ui.ifPresent {
                it.navigate("issues/new")
            }
        }
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        add(createButton)
    }
    
    override fun beforeEnter(event: BeforeEnterEvent) {
        projectId = event.routeParameters.get("projectId")
            .orElse("1")
            .toLongOrNull() ?: 1
        loadIssues()
    }
    
    private fun loadIssues() {
        try {
            val issues = issueApi.getIssues(
                projectId = projectId,
                state = filters["state"],
                assignee = filters["assignee"]
            )
            grid.setItems(issues)
        } catch (e: Exception) {
            Notification.show("Error loading issues: ${e.message}", 3000, Notification.Position.TOP_END)
        }
    }
}
```

### 3.2 Issue Detail View

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/views/issues/IssueDetailView.kt`

```kotlin
package io.cheeta.views.issues

import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import io.cheeta.api.client.IssueApiClient
import io.cheeta.api.client.IssueDto
import io.cheeta.components.layout.AppLayout

@PageTitle("Issue Details")
@Route(value = "issues/:issueId", layout = AppLayout::class)
class IssueDetailView(
    private val issueApi: IssueApiClient = IssueApiClient()
) : VerticalLayout(), BeforeEnterObserver {
    
    private var projectId: Long = 0
    private var issueNumber: Int = 0
    private lateinit var issue: IssueDto
    
    init {
        setSizeFull()
    }
    
    override fun beforeEnter(event: BeforeEnterEvent) {
        projectId = event.routeParameters.get("projectId").orElse("1").toLongOrNull() ?: 1
        issueNumber = event.routeParameters.get("issueId").orElse("1").toIntOrNull() ?: 1
        
        loadIssue()
    }
    
    private fun loadIssue() {
        try {
            issue = issueApi.getIssue(projectId, issueNumber)
            
            removeAll()
            add(
                H2(issue.title),
                Paragraph("State: ${issue.state}"),
                Paragraph("Priority: ${issue.priority}"),
                Paragraph(issue.description),
                Button("Edit") {
                    // Open edit dialog
                },
                Button("Close") {
                    // Call API to close issue
                }
            )
        } catch (e: Exception) {
            Notification.show("Error loading issue: ${e.message}")
        }
    }
}
```

---

## Step 4: Wire It Into Main Application

**File**: `cheeta-webapp/src/main/kotlin/io/cheeta/Application.kt`

```kotlin
package io.cheeta

import com.vaadin.flow.spring.annotation.EnableVaadin
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import io.cheeta.api.client.ApiClient

@SpringBootApplication
@EnableVaadin("io.cheeta")
class CheetsApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CheetsApplication::class.java, *args)
        }
    }
    
    @Bean
    fun apiClient(): ApiClient = ApiClient(
        baseUrl = "http://localhost:8080/api/v1"
    )
}
```

---

## Step 5: Add Gradle Dependencies

**File**: `cheeta-webapp/build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.vaadin") version "24.0.0"
}

dependencies {
    // Vaadin
    implementation("com.vaadin:vaadin-spring-boot-starter:24.0.0")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // JSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")
    
    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.vaadin:vaadin-testbench:24.0.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
```

---

## Step 6: Application Properties

**File**: `cheeta-webapp/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: cheeta-webapp
  jpa:
    hibernate:
      ddl-auto: validate

server:
  port: 8081

vaadin:
  theme: cheeta
  url-mapping: /app/*
  
api:
  baseUrl: http://localhost:8080
  timeout: 30s
```

---

## Testing Example

**File**: `cheeta-webapp/src/test/kotlin/io/cheeta/api/IssueApiClientTest.kt`

```kotlin
package io.cheeta.api

import io.cheeta.api.client.IssueApiClient
import io.cheeta.api.client.IssueDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals

class IssueApiClientTest {
    
    private lateinit var issueApi: IssueApiClient
    
    @BeforeEach
    fun setUp() {
        issueApi = IssueApiClient()
    }
    
    @Test
    fun testGetIssuesReturnsNonEmptyList() {
        val issues = issueApi.getIssues(projectId = 1)
        assert(issues.isNotEmpty())
    }
    
    @Test
    fun testGetIssueReturnsCorrectIssue() {
        val issue = issueApi.getIssue(projectId = 1, issueNumber = 1)
        assertEquals(1, issue.number)
    }
    
    @Test
    fun testCreateIssueReturnsIdGreaterThanZero() {
        val request = CreateIssueRequest(
            title = "Test Issue",
            description = "Test description"
        )
        val created = issueApi.createIssue(projectId = 1, request)
        assert(created.id > 0)
    }
}
```

---

## Quick Start Commands

```bash
# 1. Create cheeta-webapp as Spring Boot + Vaadin project
cd cheeta-webapp
./gradlew build

# 2. Start server-core (backend)
cd ../server-core
./mvn clean spring-boot:run

# 3. Start cheeta-webapp (frontend)
cd ../cheeta-webapp
./gradlew bootRun

# 4. Open browser
open http://localhost:8081
```

---

## Benefits of This Approach

✅ **No duplication** - Business logic stays in server-core
✅ **Clear separation** - API contract is explicit
✅ **Easy testing** - Mock APIs in tests
✅ **Type-safe** - Kotlin's strong typing
✅ **Reusable** - Other clients (mobile, CLI) can use same APIs
✅ **Maintainable** - Views are thin, focused on UI only
✅ **Scalable** - Add features by adding APIs & views

---

## Next: Advanced Patterns

Once you have the basics working, consider:
- **WebSocket** for real-time updates
- **Caching** with Spring Cache or Redux-style state management
- **Pagination** & lazy loading for large datasets
- **Offline support** with local storage
- **Service Workers** for PWA features

Let me know when you're ready for the next phase! 🚀
