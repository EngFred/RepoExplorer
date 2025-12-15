package com.engfred.repoexplorer.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): SearchResponseDto

    @GET("repositories/{id}")
    suspend fun getRepo(@Path("id") id: Long): RepoDto
}

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