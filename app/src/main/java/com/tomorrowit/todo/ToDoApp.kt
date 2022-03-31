package com.tomorrowit.todo

import android.app.Application
import android.text.format.DateUtils
import androidx.work.*
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.tomorrowit.todo.repo.*
import com.tomorrowit.todo.report.RosterReport
import com.tomorrowit.todo.ui.SingleModelMotor
import com.tomorrowit.todo.ui.roster.RosterMotor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Instant
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

private const val TAG_IMPORT_WORK = "doPeriodicImport"

class ToDoApp : Application(), KoinComponent {

    /**
     * [single] defines an object that will be available as a Koin-managed singleton.
     * [viewModel] defines an object that uses AndroidX ViewModel system to make it available to those activities and fragments that need it.
     * [module] is part of a Koin domain-specific language (DSL) that describes the roster of objects to be available via dependency inversion.
     * You can of course have one or several Koin modules.
     */
    private val koinModule = module {
        single { ToDoDatabase.newInstance(androidContext()) }

        single(named("appScope")) { CoroutineScope(SupervisorJob()) }

        single {
            ToDoRepository(
                get<ToDoDatabase>().todoStore(),
                get(named("appScope")),
                get()
            )
        }

        single {
            Handlebars().apply {
                registerHelper("dateFormat", Helper<Instant> { value, _ ->
                    DateUtils.getRelativeDateTimeString(
                        androidContext(),
                        value.toEpochMilli(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS, 0
                    )
                })
            }
        }

        single { RosterReport(androidContext(), get(), get(named("appScope"))) }

        single { OkHttpClient.Builder().build() }
        single { ToDoRemoteDataSource(get()) }
        single { PrefsRepository(androidContext()) }

        viewModel { RosterMotor(get(), get(), androidApplication(), get(named("appScope")), get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)                      //Here we tell Koin that if it has any messages to log, it should use Logcat.
            androidContext(this@ToDoApp)      //Here we tell Koin what context to use for instantiating Room.
            modules(koinModule)                             //Here we can provide one or more modules that we want Koin to support
        }

        scheduleWork()
    }

    private fun scheduleWork() {
        val prefs: PrefsRepository by inject()
        val appScope: CoroutineScope by inject(named("appScope"))
        val workManager = WorkManager.getInstance(this)
        appScope.launch {
            prefs.observeImportChanges().collect() {
                if (it) {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request =
                        PeriodicWorkRequestBuilder<ImportWorker>(15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .addTag(TAG_IMPORT_WORK)
                            .build()
                    workManager.enqueueUniquePeriodicWork(
                        TAG_IMPORT_WORK,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                    )
                } else {
                    workManager.cancelAllWorkByTag(TAG_IMPORT_WORK)
                }
            }
        }
    }
}