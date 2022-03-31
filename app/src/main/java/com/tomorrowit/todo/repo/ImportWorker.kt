package com.tomorrowit.todo.repo

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

//ImportWorker extends CoroutineWorker
//CoroutineWorker is a worker that knows how to integrate with coroutines

class ImportWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    //A CoroutineWorker is not a Fragment, ViewModel, etc; so we can't use import them normally
    //We can add KoinComponent as an interface; This lets us use inject() properties from Koin

    private val repo: ToDoRepository by inject()
    private val prefs: PrefsRepository by inject()

    override suspend fun doWork() = try {
        repo.importItems(prefs.loadWebServiceUrl())
        Result.success()
    } catch (ex: Exception) {
        Log.e("ToDo", "Exception importing items in doWork()", ex)
        Result.failure()
    }
}