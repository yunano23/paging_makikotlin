package com.example.android.codelabs.paging.api

import com.example.android.codelabs.paging.db.Item
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Apiで取得するデータ
 */
data class SearchGitResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Item> = emptyList(),
        val nextPage: Int? = null
)


/**
 * Retrofitサービス
 */
interface GitService {
    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GitService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GitService::class.java)
        }
    }

    /**
     * Api接続
     */
    @GET("search/repositories?sort=stars")
    fun searchGit(@Query("q") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int)
            : Call<SearchGitResponse>
}

private const val TAG = "GitService"
private const val INQUALIFIER = "in:name,description"

/**
 * APIリクエストのトリガー
 * （成功時、失敗時の処理は、呼び出し側にもらう）
 */
fun searchGit(
        service: GitService,
        query: String,
        page: Int,
        itemPerPage: Int,
        onSuccess: (item: List<Item>) -> Unit,
        onError: (error: String) -> Unit) {

    val apiQuery = query + INQUALIFIER

    service.searchGit(apiQuery, page, itemPerPage).enqueue(
            object : Callback<SearchGitResponse> {
                override fun onResponse(call: Call<SearchGitResponse>, res: Response<SearchGitResponse>) =
                        if (res.isSuccessful)
                            onSuccess(res.body()?.items ?: emptyList())
                        else
                            onError(res.errorBody()?.string() ?: "unknown error")

                override fun onFailure(call: Call<SearchGitResponse>, t: Throwable) =
                        onError(t.message ?: "unknown error")

            }

    )
}


