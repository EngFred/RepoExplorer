package com.engfred.repoexplorer.data.remote

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("items") val items: List<RepoDto>
)

data class RepoDto(
    val id: Long,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("watchers_count") val watchers: Int,
    @SerializedName("open_issues_count") val openIssues: Int,

    val language: String?,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("owner") val owner: OwnerDto
)

data class OwnerDto(
    @SerializedName("avatar_url") val avatarUrl: String
)