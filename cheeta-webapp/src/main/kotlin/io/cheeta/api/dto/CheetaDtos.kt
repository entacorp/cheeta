package io.cheeta.api.dto

// ===== COMMON =====
data class UserDto(
    val id: Long? = null,
    val name: String = "",
    val email: String = "",
    val avatar: String = ""
)

// ===== PROJECTS =====
data class ProjectDto(
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val isPrivate: Boolean = false,
    val avatar: String = "",
    val owner: UserDto? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class CreateProjectRequest(
    val name: String,
    val description: String = "",
    val isPrivate: Boolean = false
)

data class UpdateProjectRequest(
    val name: String? = null,
    val description: String? = null,
    val isPrivate: Boolean? = null
)

// ===== ISSUES =====
data class IssueDto(
    val id: Long? = null,
    val number: Long? = null,
    val title: String = "",
    val description: String = "",
    val state: String = "OPEN",
    val assignees: List<UserDto> = emptyList(),
    val labels: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class CreateIssueRequest(
    val title: String,
    val description: String? = null,
    val assignees: List<Long> = emptyList(),
    val labels: List<String> = emptyList()
)

data class UpdateIssueRequest(
    val title: String? = null,
    val description: String? = null,
    val state: String? = null,
    val assignees: List<Long>? = null,
    val labels: List<String>? = null
)

// ===== PULL REQUESTS =====
data class PullRequestDto(
    val id: Long? = null,
    val number: Long? = null,
    val title: String = "",
    val description: String = "",
    val state: String = "OPEN",
    val sourceBranch: String = "",
    val targetBranch: String = "",
    val author: UserDto? = null,
    val reviewers: List<UserDto> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class CreatePullRequestRequest(
    val title: String,
    val description: String = "",
    val sourceBranch: String,
    val targetBranch: String,
    val reviewers: List<Long> = emptyList()
)

data class UpdatePullRequestRequest(
    val title: String? = null,
    val description: String? = null,
    val reviewers: List<Long>? = null
)

// ===== BUILDS =====
data class BuildDto(
    val id: Long? = null,
    val number: Long? = null,
    val branch: String = "",
    val commit: String = "",
    val status: String = "PENDING",
    val author: UserDto? = null,
    val duration: Long? = null,
    val startTime: String = "",
    val endTime: String = ""
)
