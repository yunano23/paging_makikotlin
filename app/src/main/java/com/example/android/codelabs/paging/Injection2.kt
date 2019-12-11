package com.example.android.codelabs.paging

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.android.codelabs.paging.api.GitService
import com.example.android.codelabs.paging.data.GitRepository
import com.example.android.codelabs.paging.db.GitDB
import com.example.android.codelabs.paging.db.GitLocalCache
import com.example.android.codelabs.paging.ui.GitViewModelFactory
import com.example.android.codelabs.paging.ui.ViewModelFactory
import java.util.concurrent.Executors

object Injection2 {

    private fun provideCache(con:Context): GitLocalCache {
        val db = GitDB.getInstance(con)
        return GitLocalCache(db.gitDao(),Executors.newSingleThreadExecutor())
    }

    private fun provideGitRepository(con: Context):GitRepository=
            GitRepository(GitService.create(), provideCache(con))

    fun provideViewModelFactory(con: Context):ViewModelProvider.Factory =
            GitViewModelFactory(provideGitRepository(con))



}