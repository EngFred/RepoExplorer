package com.engfred.repoexplorer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class RepoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val ownerAvatarUrl: String,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val openIssues: Int,
    val language: String?,
    val htmlUrl: String
)