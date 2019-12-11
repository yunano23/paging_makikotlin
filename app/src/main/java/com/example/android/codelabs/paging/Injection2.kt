package com.example.android.codelabs.paging

import android.content.Context
import com.example.android.codelabs.paging.db.GitDB
import com.example.android.codelabs.paging.db.GitLocalCache
import java.util.concurrent.Executors

object Injection2 {

    private fun provideCache(con:Context): GitLocalCache {
        val db = GitDB.getInstance(con)
        return GitLocalCache(db.gitDao(),Executors.newSingleThreadExecutor())
    }

    private fun provideGitRepository
}