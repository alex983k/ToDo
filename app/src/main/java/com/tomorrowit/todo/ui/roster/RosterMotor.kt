package com.tomorrowit.todo.ui.roster

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.todo.BuildConfig
import com.tomorrowit.todo.repo.FilterMode
import com.tomorrowit.todo.repo.PrefsRepository
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository
import com.tomorrowit.todo.report.RosterReport
import com.tomorrowit.todo.ui.ErrorScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val isLoaded: Boolean = false,
    val filterMode: FilterMode = FilterMode.ALL
)

sealed class Nav {
    data class ViewReport(val doc: Uri) : Nav()
    data class ShareReport(val doc: Uri) : Nav()
}

private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"

class RosterMotor(
    private val repo: ToDoRepository,
    private val report: RosterReport,
    private val context: Application,
    private val appScope: CoroutineScope,
    private val prefs: PrefsRepository
) : ViewModel() {
    private val _states = MutableStateFlow(RosterViewState())
    val states = _states.asStateFlow()
    private var job: Job? = null

    private val _navEvents = MutableSharedFlow<Nav>()
    val navEvents = _navEvents.asSharedFlow()

    private val _errorEvents = MutableSharedFlow<ErrorScenario>()
    val errorEvents = _errorEvents.asSharedFlow()

    init {
        load(FilterMode.ALL)
    }

    fun load(filterMode: FilterMode) {
        job?.cancel()

        job = viewModelScope.launch {
            repo.items(filterMode).collect {
                _states.emit(RosterViewState(it, true, filterMode))
            }
        }
    }

    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repo.save(model)
        }
    }

    fun saveReport(doc: Uri) {
        viewModelScope.launch {
            report.generate(_states.value.items, doc)
            _navEvents.emit(Nav.ViewReport(doc))
        }
    }

    fun shareReport() {
        viewModelScope.launch {
            saveForSharing()
        }
    }

    fun importItems() {
        viewModelScope.launch {
            try {
                repo.importItems(prefs.loadWebServiceUrl())
            } catch (ex: Exception) {
                Log.e("ToDo", "Exception importing items", ex)
                _errorEvents.emit(ErrorScenario.Import)
            }
        }
    }

    private suspend fun saveForSharing() {
        withContext(Dispatchers.IO + appScope.coroutineContext) {
            val shared = File(context.cacheDir, "shared").also { it.mkdirs() }
            val reportFile = File(shared, "report.html")
            val doc = FileProvider.getUriForFile(context, AUTHORITY, reportFile)
            _states.value.let { report.generate(it.items, doc) }
            _navEvents.emit(Nav.ShareReport(doc))
        }
    }
}