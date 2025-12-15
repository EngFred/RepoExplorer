package com.engfred.repoexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RepoEntity::class], version = 2)
abstract class RepoDatabase : RoomDatabase() {
    abstract val dao: RepoDao
}