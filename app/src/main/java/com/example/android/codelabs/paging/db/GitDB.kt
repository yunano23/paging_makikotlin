package com.example.android.codelabs.paging.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.*
import com.example.android.codelabs.paging.api.GitService
import com.example.android.codelabs.paging.api.searchGit
import com.google.gson.annotations.SerializedName
import java.util.concurrent.Executor

/**
 * Entity
 */
@Entity(tableName = "Info")
data class Item(
        @PrimaryKey @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("description") val description: String,
        @SerializedName("html_url") val url: String,
        @SerializedName("stagazers_count") val stars: Int,
        @SerializedName("forks_count") val forks: Int,
        @SerializedName("language") val language: String?
)


/**
 * Dao
 */
@Dao
interface GitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<Item>)

    @Query("SELECt * From Infos WHERE (name LILE :query) OR (description LIKE:query)" +
           "ORDER BY stars DESC, name ASC")
    fun query(query: String): DataSource.Factory<Int, Item>
}

/**
 * Room DB
 */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class GitDB : RoomDatabase() {
    abstract fun gitDao(): GitDao

    companion object {

        @Volatile
        private var INSTANCE: GitDB? = null

        fun getInstance(con: Context): GitDB =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDB(con).also { INSTANCE = it }
                }

        private fun buildDB(con: Context): GitDB =
                Room.databaseBuilder(con.applicationContext, GitDB::class.java, "Git.db")
                        .build()
    }
}

/**
 * DAOローカルデータソースを扱うクラス。
 * （これにより、正しいエグゼキューターでメソッドがトリガーされる）
 */
class GitLocalCache(private val gitDao: GitDao, private val ioExecutor: Executor) {

    fun insert(data: List<Item>, insertFinished: () -> Unit) =
            ioExecutor.execute {
                gitDao.insert(data)
                insertFinished()
            }

    fun query(name: String): DataSource.Factory<Int, Item> {
        val query = "%${name.replace(' ', '%')}%"
        return gitDao.query(query)
    }
}