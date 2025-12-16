package com.engfred.repoexplorer.data.remote

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