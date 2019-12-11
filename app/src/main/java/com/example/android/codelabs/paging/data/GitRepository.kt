package com.example.android.codelabs.paging.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.android.codelabs.paging.api.GitService
import com.example.android.codelabs.paging.api.searchGit
import com.example.android.codelabs.paging.db.GitLocalCache
import com.example.android.codelabs.paging.db.Item


/**
 * Data
 * （レポジトリが返すデータ）
 */
data class GitSearchResult(
        val data: LiveData<PagedList<Item>>,
        val networkError: LiveData<String>
)

/**
 * Gitレポジトリ
 */
class GitRepository(
        private val service: GitService,
        private val cache: GitLocalCache) {

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }

    fun seach(query: String): GitSearchResult {
        val dataSouceFactory = cache.query(query)

        val boundaryCallback = GitBoundaryCallBack(query, service, cache)

        val networkErrors = boundaryCallback.networkErrors

        val pagedList =
                LivePagedListBuilder(dataSouceFactory, DATABASE_PAGE_SIZE)
                        .setBoundaryCallback(boundaryCallback)
                        .build()

        return GitSearchResult(pagedList, networkErrors)
    }
}

/**
 * BoundaryCallBack
 */
class GitBoundaryCallBack(
        private val query: String,
        private val service: GitService,
        private val cache: GitLocalCache
) : PagedList.BoundaryCallback<Item>() {

    companion object {
        private const val NETWOEK_PAGE_SIZE = 50
    }

    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()
    val networkErrors: LiveData<String>
        get() = _networkErrors

    private var isProgress = false

    override fun onZeroItemsLoaded() {
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Item) {
        super.onItemAtEndLoaded(itemAtEnd)
    }

    private fun requestAndSaveData(query: String) {
        if (isProgress) return

        isProgress = true

        searchGit(service, query, lastRequestedPage, NETWOEK_PAGE_SIZE,
                { list ->
                    cache.insert(list) {
                        lastRequestedPage++
                        isProgress = false
                    }
                },
                { error ->
                    _networkErrors.postValue(error)
                    isProgress = false
                })
    }
}
