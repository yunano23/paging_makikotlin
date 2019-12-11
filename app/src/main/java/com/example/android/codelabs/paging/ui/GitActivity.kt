package com.example.android.codelabs.paging.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.android.codelabs.paging.Injection2

//import androidx.fragment.app.activityViewModels

class GitActivity : AppCompatActivity() {
    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "default_query"
    }

    //    private val viewModel: GitViewModel by activityViewModels()
    private val viewModel by lazy {
        ViewModelProviders.of(this, Injection2.provideViewModelFactory(this))
                .get(GitViewModel::class.java)
    }

    private val adapter = GitAdapter()

}