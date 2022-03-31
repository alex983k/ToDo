package com.tomorrowit.todo.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.tomorrowit.todo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class PrefsRepository(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val webServiceUrlKey = context.getString(R.string.web_service_url_key)
    private val defaultWebServiceUrl =
        context.getString(R.string.web_service_url_default)

    private val importKey = context.getString(R.string.import_key)

    suspend fun loadWebServiceUrl(): String = withContext(Dispatchers.IO) {
        prefs.getString(webServiceUrlKey, defaultWebServiceUrl) ?: defaultWebServiceUrl
    }

    fun observeImportChanges() = channelFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (importKey == key) {
                offer(prefs.getBoolean(importKey, false))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
}