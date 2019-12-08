/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.android.codelabs.paging.model.Repo

/**
 * RoomDatabase
 */
@Database(entities = [Repo::class], version = 1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun reposDao(): RepoDao

    companion object {

        @Volatile
        private var INSTANCE: RepoDatabase? = null

        fun getInstance(context: Context): RepoDatabase =
                INSTANCE ?: synchronized(this) {
                    // 待ってる間に他のスレッドで生成されるかもしれないから再確認
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, RepoDatabase::class.java, "Github.db")
                        .build()
    }
}


/*
    @Volatile
        このプロパティの JVMバッキングフィールド をvolatileとしてマークすると、
        このフィールドへの書込は、他のスレッドからすぐに見えるようになる。
    synchronized
        排他制御＝複数のプロセス（またはスレッド）が同時に入ることを防ぐ
        １つのスレッドで実行中の時は、その処理が終わるまで、他のスレッドはアクセスできない

 */
