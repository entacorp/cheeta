package io.cheeta.api.client

import io.cheeta.api.dto.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Central API client for all server-core REST endpoints.
 * Handles Projects, Issues, PRs, Builds, Users, and more.
 */
@Service
class CheetaApiClient(
    @Value("\${cheeta.api.base-url}") baseUrl: String
) : ApiClient(baseUrl) {

    // ===== PROJECTS =====
    fun getProjects(): List<ProjectDto> = try {
        get("/rest/projects")
    } catch (e: Exception) {
        emptyList()
    }

    fun getProject(projectId: String): ProjectDto? = try {
        get("/rest/projects/$projectId")
    } catch (e: Exception) {
        null
    }

    fun createProject(request: CreateProjectRequest): ProjectDto =
        post("/rest/projects", request)

    fun updateProject(projectId: String, request: UpdateProjectRequest): ProjectDto =
        put("/rest/projects/$projectId", request)

    fun deleteProject(projectId: String) {
        delete<Unit>("/rest/projects/$projectId")
    }

    // ===== ISSUES =====
    fun getIssues(projectId: String, state: String? = null): List<IssueDto> = try {
        val path = "/rest/projects/$projectId/issues" + 
            (if (state != null) "?state=$state" else "")
        get(path)
    } catch (e: Exception) {
        emptyList()
    }

    fun getIssue(projectId: String, issueNumber: Long): IssueDto? = try {
        get("/rest/projects/$projectId/issues/$issueNumber")
    } catch (e: Exception) {
        null
    }

    fun createIssue(projectId: String, request: CreateIssueRequest): IssueDto =
        post("/rest/projects/$projectId/issues", request)

    fun updateIssue(projectId: String, issueNumber: Long, request: UpdateIssueRequest): IssueDto =
        put("/rest/projects/$projectId/issues/$issueNumber", request)

    fun deleteIssue(projectId: String, issueNumber: Long) {
        delete<Unit>("/rest/projects/$projectId/issues/$issueNumber")
    }

    // ===== PULL REQUESTS =====
    fun getPullRequests(projectId: String, state: String? = null): List<PullRequestDto> = try {
        val path = "/rest/projects/$projectId/pullrequests" + 
            (if (state != null) "?state=$state" else "")
        get(path)
    } catch (e: Exception) {
        emptyList()
    }

    fun getPullRequest(projectId: String, number: Long): PullRequestDto? = try {
        get("/rest/projects/$projectId/pullrequests/$number")
    } catch (e: Exception) {
        null
    }

    fun createPullRequest(projectId: String, request: CreatePullRequestRequest): PullRequestDto =
        post("/rest/projects/$projectId/pullrequests", request)

    fun updatePullRequest(
        projectId: String,
        number: Long,
        request: UpdatePullRequestRequest
    ): PullRequestDto =
        put("/rest/projects/$projectId/pullrequests/$number", request)

    fun mergePullRequest(projectId: String, number: Long): PullRequestDto =
        post("/rest/projects/$projectId/pullrequests/$number/merge", null)

    fun closePullRequest(projectId: String, number: Long): PullRequestDto =
        post("/rest/projects/$projectId/pullrequests/$number/close", null)

    // ===== BUILDS =====
    fun getBuilds(projectId: String): List<BuildDto> = try {
        get("/rest/projects/$projectId/builds")
    } catch (e: Exception) {
        emptyList()
    }

    fun getBuild(projectId: String, buildNumber: Long): BuildDto? = try {
        get("/rest/projects/$projectId/builds/$buildNumber")
    } catch (e: Exception) {
        null
    }

    fun getBuildLog(projectId: String, buildNumber: Long): String = try {
        get("/rest/projects/$projectId/builds/$buildNumber/log")
    } catch (e: Exception) {
        ""
    }

    fun triggerBuild(projectId: String, branch: String = "main"): BuildDto =
        post("/rest/projects/$projectId/builds", mapOf("branch" to branch))

    fun cancelBuild(projectId: String, buildNumber: Long) {
        delete<Unit>("/rest/projects/$projectId/builds/$buildNumber")
    }

    // ===== USERS =====
    fun getUser(userId: String): UserDto? = try {
        get("/rest/users/$userId")
    } catch (e: Exception) {
        null
    }

    fun getCurrentUser(): UserDto? = try {
        get("/rest/user")
    } catch (e: Exception) {
        null
    }
}
