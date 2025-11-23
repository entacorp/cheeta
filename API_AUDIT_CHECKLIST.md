# REST API Audit Checklist

This document helps you verify that all necessary REST APIs exist before starting Vaadin frontend development.

## Core APIs

### Projects
```
✓/✗ GET    /api/v1/projects              - List all projects
✓/✗ POST   /api/v1/projects              - Create project
✓/✗ GET    /api/v1/projects/:id          - Get project details
✓/✗ PUT    /api/v1/projects/:id          - Update project
✓/✗ DELETE /api/v1/projects/:id          - Delete project
✓/✗ GET    /api/v1/projects/:id/settings - Get project settings
```

### Issues
```
✓/✗ GET    /api/v1/projects/:id/issues                  - List issues
✓/✗ POST   /api/v1/projects/:id/issues                  - Create issue
✓/✗ GET    /api/v1/projects/:id/issues/:number          - Get issue
✓/✗ PUT    /api/v1/projects/:id/issues/:number          - Update issue
✓/✗ DELETE /api/v1/projects/:id/issues/:number          - Delete issue
✓/✗ POST   /api/v1/projects/:id/issues/:number/comments - Add comment
✓/✗ GET    /api/v1/projects/:id/issues/:number/comments - List comments
✓/✗ PUT    /api/v1/projects/:id/issues/:number/state    - Change state
✓/✗ POST   /api/v1/projects/:id/issues/:number/close    - Close issue
✓/✗ POST   /api/v1/projects/:id/issues/:number/reopen   - Reopen issue
```

### Pull Requests
```
✓/✗ GET    /api/v1/projects/:id/pulls                  - List pull requests
✓/✗ POST   /api/v1/projects/:id/pulls                  - Create pull request
✓/✗ GET    /api/v1/projects/:id/pulls/:number          - Get PR details
✓/✗ PUT    /api/v1/projects/:id/pulls/:number          - Update PR
✓/✗ DELETE /api/v1/projects/:id/pulls/:number          - Delete PR
✓/✗ POST   /api/v1/projects/:id/pulls/:number/reviews  - Create review
✓/✗ GET    /api/v1/projects/:id/pulls/:number/reviews  - List reviews
✓/✗ POST   /api/v1/projects/:id/pulls/:number/merge    - Merge PR
✓/✗ POST   /api/v1/projects/:id/pulls/:number/comments - Add comment
```

### Commits
```
✓/✗ GET    /api/v1/projects/:id/commits              - List commits
✓/✗ GET    /api/v1/projects/:id/commits/:sha         - Get commit details
✓/✗ GET    /api/v1/projects/:id/commits/:sha/diff    - Get commit diff
✓/✗ GET    /api/v1/projects/:id/commits/:sha/files   - Get commit files
```

### Repository Files
```
✓/✗ GET    /api/v1/projects/:id/files                - List files in repo
✓/✗ GET    /api/v1/projects/:id/files/:path          - Get file content
✓/✗ POST   /api/v1/projects/:id/files/:path          - Create file
✓/✗ PUT    /api/v1/projects/:id/files/:path          - Update file
✓/✗ DELETE /api/v1/projects/:id/files/:path          - Delete file
✓/✗ GET    /api/v1/projects/:id/files/:path/blame    - Get blame info
```

### Branches
```
✓/✗ GET    /api/v1/projects/:id/branches             - List branches
✓/✗ POST   /api/v1/projects/:id/branches             - Create branch
✓/✗ GET    /api/v1/projects/:id/branches/:name       - Get branch details
✓/✗ DELETE /api/v1/projects/:id/branches/:name       - Delete branch
✓/✗ POST   /api/v1/projects/:id/branches/:name/protect - Protect branch
```

### Tags & Releases
```
✓/✗ GET    /api/v1/projects/:id/tags                 - List tags
✓/✗ POST   /api/v1/projects/:id/tags                 - Create tag
✓/✗ DELETE /api/v1/projects/:id/tags/:name           - Delete tag
✓/✗ GET    /api/v1/projects/:id/releases             - List releases
✓/✗ POST   /api/v1/projects/:id/releases             - Create release
✓/✗ PUT    /api/v1/projects/:id/releases/:id         - Update release
```

### CI/CD Builds
```
✓/✗ GET    /api/v1/projects/:id/builds               - List builds
✓/✗ POST   /api/v1/projects/:id/builds               - Trigger build
✓/✗ GET    /api/v1/projects/:id/builds/:number       - Get build details
✓/✗ GET    /api/v1/projects/:id/builds/:number/logs  - Get build logs
✓/✗ POST   /api/v1/projects/:id/builds/:number/stop  - Stop build
✓/✗ POST   /api/v1/projects/:id/builds/:number/retry - Retry build
✓/✗ GET    /api/v1/projects/:id/builds/:number/artifacts - List artifacts
```

### Pipelines
```
✓/✗ GET    /api/v1/projects/:id/pipelines            - List pipelines
✓/✗ POST   /api/v1/projects/:id/pipelines            - Create pipeline
✓/✗ GET    /api/v1/projects/:id/pipelines/:id        - Get pipeline
✓/✗ PUT    /api/v1/projects/:id/pipelines/:id        - Update pipeline
✓/✗ DELETE /api/v1/projects/:id/pipelines/:id        - Delete pipeline
```

---

## User & Permissions APIs

### Users
```
✓/✗ GET    /api/v1/users                             - List users
✓/✗ POST   /api/v1/users                             - Create user
✓/✗ GET    /api/v1/users/:id                         - Get user details
✓/✗ PUT    /api/v1/users/:id                         - Update user
✓/✗ DELETE /api/v1/users/:id                         - Delete user
✓/✗ GET    /api/v1/users/me                          - Get current user
✓/✗ PUT    /api/v1/users/me                          - Update current user
✓/✗ GET    /api/v1/users/:id/projects                - List user projects
✓/✗ GET    /api/v1/users/:id/repositories            - List user repos
```

### Teams/Organizations
```
✓/✗ GET    /api/v1/organizations                     - List orgs
✓/✗ POST   /api/v1/organizations                     - Create org
✓/✗ GET    /api/v1/organizations/:id                 - Get org details
✓/✗ PUT    /api/v1/organizations/:id                 - Update org
✓/✗ DELETE /api/v1/organizations/:id                 - Delete org
✓/✗ GET    /api/v1/organizations/:id/members         - List members
✓/✗ POST   /api/v1/organizations/:id/members         - Add member
✓/✗ DELETE /api/v1/organizations/:id/members/:user   - Remove member
```

### Permissions/Roles
```
✓/✗ GET    /api/v1/projects/:id/members              - List project members
✓/✗ POST   /api/v1/projects/:id/members              - Add project member
✓/✗ PUT    /api/v1/projects/:id/members/:user        - Update member role
✓/✗ DELETE /api/v1/projects/:id/members/:user        - Remove project member
✓/✗ GET    /api/v1/projects/:id/permissions          - Get permissions
```

---

## New Feature APIs (Cheeta-Specific)

### Job Board
```
✓/✗ GET    /api/v1/jobs                              - List job listings
✓/✗ POST   /api/v1/jobs                              - Post job listing
✓/✗ GET    /api/v1/jobs/:id                          - Get job details
✓/✗ GET    /api/v1/jobs/companies/:id                - Get company profile
✓/✗ POST   /api/v1/jobs/:id/apply                    - Apply to job
✓/✗ GET    /api/v1/jobs/applications                 - Get user applications
✓/✗ GET    /api/v1/users/:id/portfolio               - Get user portfolio
```

### Sponsorships
```
✓/✗ GET    /api/v1/projects/:id/sponsors             - List project sponsors
✓/✗ GET    /api/v1/projects/:id/sponsorships         - Get sponsorship details
✓/✗ POST   /api/v1/projects/:id/sponsorships         - Create sponsorship
✓/✗ POST   /api/v1/users/:id/sponsor                 - Sponsor a project
✓/✗ GET    /api/v1/users/:id/sponsoring              - Get user sponsorships
```

### Packages/Registry
```
✓/✗ GET    /api/v1/projects/:id/packages             - List packages
✓/✗ POST   /api/v1/projects/:id/packages             - Publish package
✓/✗ GET    /api/v1/packages/:name/:version           - Get package version
✓/✗ DELETE /api/v1/packages/:name/:version           - Delete package
✓/✗ GET    /api/v1/packages/:name/versions           - List versions
```

### Pages (Static Hosting)
```
✓/✗ GET    /api/v1/projects/:id/pages                - List pages
✓/✗ POST   /api/v1/projects/:id/pages                - Create page
✓/✗ GET    /api/v1/projects/:id/pages/:slug          - Get page
✓/✗ PUT    /api/v1/projects/:id/pages/:slug          - Update page
✓/✗ DELETE /api/v1/projects/:id/pages/:slug          - Delete page
✓/✗ POST   /api/v1/projects/:id/pages/:slug/deploy   - Deploy page
```

### Templates
```
✓/✗ GET    /api/v1/templates                         - List templates
✓/✗ POST   /api/v1/templates                         - Create template
✓/✗ GET    /api/v1/templates/:id                     - Get template
✓/✗ POST   /api/v1/templates/:id/use                 - Use template
```

### Analytics & Metrics
```
✓/✗ GET    /api/v1/projects/:id/analytics            - Get project analytics
✓/✗ GET    /api/v1/projects/:id/metrics/dora         - Get DORA metrics
✓/✗ GET    /api/v1/projects/:id/metrics/team         - Get team metrics
✓/✗ GET    /api/v1/projects/:id/metrics/quality      - Get code quality metrics
```

### DevFeed (Community)
```
✓/✗ GET    /api/v1/feed/posts                        - List feed posts
✓/✗ POST   /api/v1/feed/posts                        - Create post
✓/✗ GET    /api/v1/feed/posts/:id                    - Get post
✓/✗ POST   /api/v1/feed/posts/:id/comments           - Comment on post
✓/✗ POST   /api/v1/feed/posts/:id/vote               - Vote on post
✓/✗ GET    /api/v1/feed/tags                         - List tags
```

---

## Authentication & Security APIs

```
✓/✗ POST   /api/v1/auth/login                        - Login
✓/✗ POST   /api/v1/auth/logout                       - Logout
✓/✗ POST   /api/v1/auth/refresh                      - Refresh token
✓/✗ POST   /api/v1/auth/register                     - Register user
✓/✗ POST   /api/v1/auth/forgot-password              - Request password reset
✓/✗ POST   /api/v1/auth/reset-password               - Reset password
✓/✗ GET    /api/v1/auth/verify                       - Verify token
✓/✗ POST   /api/v1/auth/mfa/enable                   - Enable 2FA
✓/✗ POST   /api/v1/auth/mfa/disable                  - Disable 2FA
```

---

## Admin APIs

```
✓/✗ GET    /api/v1/admin/users                       - List all users
✓/✗ POST   /api/v1/admin/users                       - Create user
✓/✗ PUT    /api/v1/admin/users/:id                   - Update user
✓/✗ DELETE /api/v1/admin/users/:id                   - Delete user
✓/✗ GET    /api/v1/admin/organizations               - List orgs
✓/✗ GET    /api/v1/admin/system/health               - System health
✓/✗ GET    /api/v1/admin/system/logs                 - System logs
✓/✗ GET    /api/v1/admin/system/metrics              - System metrics
```

---

## Audit Instructions

1. **Run existing API tests**:
   ```bash
   cd server-core
   mvn test -Dtest=*ResourceTest
   ```

2. **Check Swagger/OpenAPI docs**:
   ```
   http://localhost:8080/swagger-ui.html
   http://localhost:8080/api/v1/docs
   ```

3. **List all endpoints**:
   ```bash
   mvn test -Dtest=*Resource* | grep -i "path\|endpoint"
   ```

4. **Missing endpoint checklist**:
   - [ ] Run through Core APIs section - mark completed ones
   - [ ] Run through User & Permissions - mark completed ones
   - [ ] Run through New Feature APIs - mark completed ones
   - [ ] Create issues in backlog for any missing APIs
   - [ ] Estimate effort to build missing APIs

5. **API quality checklist**:
   - [ ] All endpoints return consistent JSON format
   - [ ] All endpoints have proper error handling
   - [ ] All endpoints have authentication/authorization
   - [ ] All endpoints have input validation
   - [ ] All endpoints have pagination support (where applicable)
   - [ ] All endpoints have logging

---

## Example: Checking an API

```bash
# Test getting issues
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/v1/projects/1/issues?limit=10

# Response should be:
{
  "data": [
    {
      "id": 1,
      "number": 1,
      "title": "Bug: login fails",
      "description": "...",
      "state": "open",
      "priority": 1,
      "assignee": {...},
      "createdAt": 1234567890,
      "updatedAt": 1234567890
    }
  ],
  "total": 42,
  "limit": 10,
  "offset": 0
}
```

If this works, mark ✓ next to `GET /api/v1/projects/:id/issues` above.

---

## Priority Levels

### High Priority (Required for MVP)
- Projects, Issues, PRs, Commits, Files, Branches, CI/CD Builds
- Users, Permissions
- Authentication

### Medium Priority (Nice to have)
- Tags/Releases
- Pipelines
- Organizations
- Admin APIs

### Low Priority (Phase 2+)
- Job Board APIs
- Sponsorship APIs
- Package Registry APIs
- Pages APIs
- Analytics APIs
- DevFeed APIs

---

## Notes

- Once all high-priority APIs are verified, start Vaadin frontend development
- Create API client for each resource type
- Create Vaadin view for each API resource
- Use test data if real data unavailable
- Document any API gaps and create tickets

Good luck! 🚀
