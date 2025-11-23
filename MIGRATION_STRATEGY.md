# Cheeta UI Migration Strategy: Apache Wicket → Vaadin + Kotlin

## Executive Summary

The server-core already has:
- **Wicket-based UI** (Java components in `io.cheeta.server.web`)
- **Comprehensive REST APIs** (Jersey-based in `io.cheeta.server.rest`)
- **Complete backend services** (business logic)
- **67+ Wicket components** and 16+ page modules

**Strategy**: Leverage the existing REST APIs rather than rewriting. Create a lightweight Vaadin + Kotlin frontend that consumes these APIs.

---

## Current Architecture

### 1. Server-Core (Backend)
```
server-core/
├── src/main/java/io/cheeta/server/
│   ├── web/              # Wicket UI (can be deprecated)
│   │   ├── component/    # 67+ Wicket components
│   │   ├── page/         # 16+ page modules
│   │   ├── asset/        # 46 asset modules
│   │   └── behavior/     # 8+ behavior modules
│   ├── rest/             # Jersey REST APIs ✓ REUSE THESE
│   │   ├── resource/     # Issue, Project, PR, Build, etc.
│   │   └── JerseyApplication.java
│   ├── service/          # Business logic ✓ REUSE THIS
│   │   ├── IssueService.java
│   │   ├── ProjectService.java
│   │   ├── PullRequestService.java
│   │   └── ... (40+ services)
│   └── model/            # Data entities ✓ REUSE THIS
│       ├── Issue.java
│       ├── Project.java
│       └── ... (100+ entities)
```

### 2. Cheeta-Webapp (Frontend)
```
cheeta-webapp/
├── src/main/kotlin/io/cheeta/
│   ├── views/           # NEW Vaadin views
│   ├── components/      # NEW Vaadin components
│   └── services/        # API client services (consuming REST)
└── frontend/            # Styling & assets
```

---

## Migration Strategy: 3-Phase Approach

### Phase 1: API-First Consumption (IMMEDIATE)

**Goal**: Make cheeta-webapp independent of server-core Wicket UI

**Steps**:

1. **Create Vaadin API Client Layer** (`io.cheeta.api.client`)
   - Use existing REST endpoints from server-core
   - Create Kotlin data classes (DTOs) from REST responses
   - Build type-safe HTTP client using Ktor/OkHttp

2. **Map Wicket Components → Vaadin Components**
   ```kotlin
   // Instead of rewriting, consume REST APIs:
   // GET  /api/v1/issues
   // POST /api/v1/issues
   // GET  /api/v1/issues/{id}
   // PUT  /api/v1/issues/{id}
   ```

3. **Keep Server-Core Wicket Running as Legacy** (optional)
   - Don't migrate Wicket UI yet
   - Focus on making REST APIs bullet-proof
   - Server can still serve `/wicket/*` routes while Vaadin serves `/`

**Estimated Time**: 2-3 weeks

---

### Phase 2: Component-by-Component Migration (PARALLEL)

**Goal**: Replace Wicket pages with Vaadin views without rewriting logic

**High-Level Map** (Wicket → Vaadin):

| Wicket Page Module | Vaadin View | API Endpoint(s) | Notes |
|---|---|---|---|
| `page.project.ProjectPage` | `views.repository.RepositoryView` | `/api/v1/projects/{id}` | ✓ Already in scaffolding |
| `page.issues.*` | `views.issues.*` | `/api/v1/issues`, `/api/v1/projects/{id}/issues` | ✓ Already in scaffolding |
| `page.pullrequests.*` | `views.pullrequests.*` | `/api/v1/projects/{id}/pulls` | ✓ Already in scaffolding |
| `page.builds.*` | `views.cicd.builds.*` | `/api/v1/projects/{id}/builds` | ✓ Already in scaffolding |
| `page.admin.*` | `views.admin.*` | `/api/v1/admin/*` | ✓ Already in scaffolding |

**Strategy**:
1. Identify which Wicket components are UI-only (no business logic)
2. Copy UI structure, replace with Vaadin components
3. Swap data binding from Wicket models to REST API calls
4. Test each view independently

**Key Insight**: Most Wicket components are **view layer only**. Business logic is in services.

**Example Migration**:

```java
// WICKET (OLD)
public class IssueListPanel extends WebMarkupContainer {
    public IssueListPanel(String id, PageParameters params) {
        var issues = issueService.queryIssues(criteria);  // Calls service
        add(new ListView<>("issues", issues) { ... });    // Renders in markup
    }
}
```

```kotlin
// VAADIN (NEW)
class IssueListView : VerticalLayout() {
    init {
        val issueService = IssueApiClient()  // Calls REST API
        issueService.getIssues().thenAccept { issues ->
            val grid = Grid<IssueDto>()
            grid.setItems(issues)
            add(grid)
        }
    }
}
```

**Estimated Time**: 4-6 weeks (parallelizable)

---

### Phase 3: Advanced Features & Optimizations (ITERATIVE)

**Goal**: Add new Cheeta features (Jobs, Sponsors, Templates, etc.)

**Strategy**:
1. Create new REST APIs for new features (if not exist)
2. Build Vaadin views that consume them
3. Use the scaffolding we already created

**No Wicket Migration Needed** - these are pure new features.

---

## Smart Porting Techniques

### Technique 1: Extract Component Logic to Services

```java
// WICKET COMPONENT (Existing)
public class IssueCard extends WebMarkupContainer {
    public IssueCard(String id, Issue issue) {
        add(new Label("title", issue.getTitle()));
        add(new Label("status", issue.getStatus()));
        // ... more markup
    }
}
```

**Convert to**:

```kotlin
// Kotlin data class (reuse or create from REST)
data class IssueDto(
    val id: Long,
    val title: String,
    val status: String,
    val description: String
)

// Vaadin component (pure UI)
class IssueCard(val issue: IssueDto) : VerticalLayout() {
    init {
        add(H3(issue.title))
        add(Badge(issue.status))
        // ... more Vaadin components
    }
}
```

### Technique 2: Batch API Calls

Instead of N+1 requests, batch them:

```kotlin
// GOOD: Single REST call
val issues = api.getIssues(filter, sort, limit)

// BAD: Cascading calls
val issues = api.getIssues()
issues.forEach { issue ->
    issue.assignee = api.getUser(issue.assigneeId)  // N+1 problem!
}
```

### Technique 3: WebSocket for Real-Time Updates

Wicket had WebSocket integration. Vaadin + Kotlin can too:

```kotlin
class IssueDetailView(issueId: Long) : VerticalLayout() {
    init {
        val ws = WebSocketClient("ws://localhost:8080/api/ws/issues/$issueId")
        ws.subscribe { update ->
            updateUI(update)  // Real-time updates
        }
    }
}
```

### Technique 4: Reuse JavaScript Assets

Some Wicket components have JS dependencies:
- CodeMirror (syntax highlighting)
- Mermaid (diagrams)
- Chart.js (charts)

**Migrate by**:
1. Extract the JS library usage
2. Keep the library, drop the Wicket component
3. Use in Vaadin view via `Page.executeJs()` or Vaadin's JS interop

```kotlin
// Vaadin + JS interop
val editor = PreFilledHtmlEditor()
Page.getCurrent().executeJs("""
    CodeMirror.fromTextArea(document.getElementById('code'), {
        mode: 'kotlin',
        theme: 'dracula'
    });
""")
```

---

## Implementation Roadmap

### Week 1-2: Foundation
- [ ] Set up Vaadin 24 + Kotlin in cheeta-webapp
- [ ] Create API client layer consuming server-core REST
- [ ] Build basic layout (nav, sidebar, main content)
- [ ] Create 3 example views (Dashboard, Projects, Issues list)

### Week 3-4: Core Pages
- [ ] Repository view (with file browser)
- [ ] Issues list & detail
- [ ] Pull requests list & detail
- [ ] CI/CD builds & pipelines

### Week 5-6: Advanced Pages
- [ ] Code review UI
- [ ] Admin panel
- [ ] User profiles
- [ ] Settings pages

### Week 7-8: New Features
- [ ] Job board
- [ ] Sponsors dashboard
- [ ] Package registry
- [ ] Analytics & DORA metrics

### Week 9+: Polish
- [ ] Theming
- [ ] Responsive design
- [ ] Performance optimization
- [ ] WebSocket integration

---

## Code Organization Best Practice

```
cheeta-webapp/src/main/kotlin/io/cheeta/
├── api/
│   ├── client/
│   │   ├── ApiClient.kt              # Base HTTP client
│   │   ├── IssueApiClient.kt         # Issue API calls
│   │   ├── ProjectApiClient.kt       # Project API calls
│   │   └── ...
│   └── dto/
│       ├── IssueDto.kt               # Data transfer objects
│       ├── ProjectDto.kt
│       └── ...
├── views/
│   ├── dashboard/                    # Dashboard views
│   ├── issues/                       # Issue-related views
│   ├── repository/                   # Repository views
│   └── ...
├── components/
│   ├── cards/
│   │   └── IssueCard.kt              # Reusable cards
│   ├── forms/
│   │   └── IssueForm.kt              # Reusable forms
│   └── ...
└── utils/
    └── ApiUtils.kt                   # Utility functions
```

---

## Testing Strategy

### Unit Tests
```kotlin
// Test API client
@Test
fun testGetIssue() {
    val api = IssueApiClient()
    val issue = api.getIssue(1)
    assertEquals("Bug: login fails", issue.title)
}
```

### Integration Tests
```kotlin
// Test full workflow
@Test
fun testCreateAndViewIssue() {
    val api = IssueApiClient()
    val created = api.createIssue(IssueInput(...))
    val retrieved = api.getIssue(created.id)
    assertEquals(created.title, retrieved.title)
}
```

### UI Tests (Vaadin)
```kotlin
// Test Vaadin view rendering
@Test
fun testIssueListViewLoads() {
    val view = IssueListView()
    // Assert grid is populated
    // Assert filters work
}
```

---

## Key Benefits of This Approach

✅ **No Rewriting Business Logic** - Services stay the same
✅ **REST APIs are Language-Agnostic** - Can add other clients later
✅ **Parallel Development** - Frontend & backend teams work independently
✅ **Incremental Migration** - Can run old & new UI simultaneously
✅ **Type Safety** - Kotlin's strong typing catches errors early
✅ **Modern Stack** - Vaadin 24 + Kotlin is modern & maintainable
✅ **Testability** - API-driven design is easier to test
✅ **Future-Proof** - Can add mobile apps, CLI, etc. using same APIs

---

## Risks & Mitigations

| Risk | Mitigation |
|---|---|
| REST API gaps | Audit APIs thoroughly; add missing endpoints early |
| Performance (N+1 queries) | Implement API batching; use DataLoader pattern |
| Real-time sync issues | Add WebSocket support; use event sourcing if needed |
| Large data sets | Implement pagination; lazy loading in views |
| Browser compatibility | Vaadin handles this; test in major browsers |

---

## Next Steps

1. **Audit REST APIs** - Ensure they cover all Wicket page functionality
2. **Create API Client** - Build the foundation for all view services
3. **Pick 1 View to Migrate** - Dashboard or Projects to prove concept
4. **Create Reusable Component Library** - Build Vaadin components once, use everywhere
5. **Establish CI/CD** - Ensure tests run for both server & webapp

---

## Conclusion

**Don't rewrite, refactor**. The business logic and APIs already exist. We're just creating a better frontend that consumes them. This approach is:
- **Faster** than rewriting everything
- **Safer** because existing services are battle-tested
- **Cleaner** because concerns are separated (backend APIs vs frontend UI)
- **Scalable** because other clients can use the same APIs

Let's build this! 🚀
