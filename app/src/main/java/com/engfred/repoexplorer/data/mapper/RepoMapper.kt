package com.engfred.repoexplorer.data.mapper

import com.engfred.repoexplorer.data.local.RepoEntity
import com.engfred.repoexplorer.data.remote.RepoDto
import com.engfred.repoexplorer.domain.model.Repo

// API -> Domain
fun RepoDto.toDomain(): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerAvatarUrl = owner.avatarUrl,
        stars = stars,
        forks = forks,
        watchers = watchers,
        openIssues = openIssues,
        language = language,
        htmlUrl = htmlUrl,
        isFavorite = false
    )
}

// DB -> Domain
fun RepoEntity.toDomain(): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerAvatarUrl = ownerAvatarUrl,
        stars = stars,
        forks = forks,
        watchers = watchers,
        openIssues = openIssues,
        language = language,
        htmlUrl = htmlUrl,
        isFavorite = true
    )
}

// Domain -> DB
fun Repo.toEntity(): RepoEntity {
    return RepoEntity(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerAvatarUrl = ownerAvatarUrl,
        stars = stars,
        forks = forks,
        watchers = watchers,
        openIssues = openIssues,
        language = language,
        htmlUrl = htmlUrl
    )
}