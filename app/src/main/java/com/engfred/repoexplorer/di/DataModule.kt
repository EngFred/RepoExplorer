package com.engfred.repoexplorer.di

import android.app.Application
import androidx.room.Room
import com.engfred.repoexplorer.data.local.RepoDatabase
import com.engfred.repoexplorer.data.remote.GithubApi
import com.engfred.repoexplorer.data.repository.RepoRepositoryImpl
import com.engfred.repoexplorer.domain.repository.RepoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideApi(): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): RepoDatabase {
        return Room.databaseBuilder(
            app,
            RepoDatabase::class.java,
            "repo_explorer_db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideRepository(
        api: GithubApi,
        db: RepoDatabase
    ): RepoRepository {
        return RepoRepositoryImpl(api, db.dao)
    }
}