package com.tomorrowit.todo

import android.app.Application
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ToDoApp : Application() {

    /**
     * [single] defines an object that will be available as a Koin-managed singleton.
     * [module] is part of a Koin domain-specific language (DSL) that describes the roster of objects to be available via dependency inversion.
     * You can of course have one or several Koin modules.
     */
    private val koinModule = module {
        single { ToDoRepository() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()     //Here we tell Koin that if it has any messages to log, it should use Logcat.
            modules(koinModule) //Here we can provide one or more modules that we want Koin to support
        }
    }
}