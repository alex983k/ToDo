package com.tomorrowit.todo.repo

import android.content.Context
import androidx.preference.PreferenceManager
import com.tomorrowit.todo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrefsRepository(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val webServiceUrlKey = context.getString(R.string.web_service_url_key)
    private val defaultWebServiceUrl =
        context.getString(R.string.web_service_url_default)

    suspend fun loadWebServiceUrl(): String = withContext(Dispatchers.IO) {
        prefs.getString(webServiceUrlKey, defaultWebServiceUrl) ?: defaultWebServiceUrl
    }
}