package com.engfred.repoexplorer.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.engfred.repoexplorer.data.mapper.toDomain
import com.engfred.repoexplorer.domain.model.Repo
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "RepoDebug"

class GithubPagingSource(
    private val api: GithubApi,
    private val query: String
) : PagingSource<Int, Repo>() {

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val page = params.key ?: 1

        if (query.isBlank()) {
            Log.d(TAG, "PagingSource: Query is empty, returning empty page.")
            return LoadResult.Page(emptyList(), null, null)
        }

        return try {
            Log.d(TAG, "PagingSource: Loading Page $page for query: '$query'")

            val response = api.searchRepos(query, page, params.loadSize)
            val repos = response.items.map { it.toDomain() }

            Log.d(TAG, "PagingSource: Success. Loaded ${repos.size} items.")

            LoadResult.Page(
                data = repos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (repos.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            Log.e(TAG, "PagingSource: Network Error: ${e.message}")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "PagingSource: API Error: ${e.code()} - ${e.message()}")
            LoadResult.Error(e)
        }
    }
}