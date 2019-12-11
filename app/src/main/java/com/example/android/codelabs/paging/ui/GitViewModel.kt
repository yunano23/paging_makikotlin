package com.example.android.codelabs.paging.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.codelabs.paging.data.GitRepository

/**
 * View Model
 * (レポジトリとワークする)
 */
class GitViewModel(private val repo: GitRepository) : ViewModel() {

    private val query = MutableLiveData<String>()

    private val result =
            Transformations.map(query) { repo.seach(it) }

    val list =
            Transformations.switchMap(result) { it.data }

    val networkError =
            Transformations.switchMap(result) { it.networkError }


    fun search(queryString: String) =
            query.postValue(queryString)

    fun lastQuesy():String? = query.value

}

class GitViewModelFactory(private val repo:GitRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GitViewModelFactory::class.java))
            @Suppress("UNCHECKED_CAST")
            return GitViewModel(repo) as T
        throw IllegalMonitorStateException("Unknown ViewModel class")
    }

}