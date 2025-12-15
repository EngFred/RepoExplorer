package com.engfred.repoexplorer.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val ownerAvatarUrl: String,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val openIssues: Int,
    val language: String?,
    val htmlUrl: String,
    val isFavorite: Boolean = false
)