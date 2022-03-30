package com.tomorrowit.todo.repo

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ToDoRemoteDataSource(private val ok: OkHttpClient) {
    private val moshi = Moshi.Builder().add(MoshiInstantAdapter()).build()
    private val adapter: JsonAdapter<List<ToDoServerItem>> = moshi.adapter(
        Types.newParameterizedType(
            List::class.java,
            ToDoServerItem::class.java
        )
    )

    suspend fun load(url: String) = withContext(Dispatchers.IO) {
        val response = ok.newCall(Request.Builder().url(url).build()).execute()
        if (response.isSuccessful) {
            response.body?.let { adapter.fromJson(it.source()) }
                ?: throw IOException("No response body: $response")
        } else {
            throw IOException("Unexpected HTTP response code: ${response.code}")
        }
    }
}