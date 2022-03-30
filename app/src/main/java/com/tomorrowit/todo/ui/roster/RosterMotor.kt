package com.tomorrowit.todo.ui.roster

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.todo.repo.FilterMode
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository
import com.tomorrowit.todo.report.RosterReport
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val isLoaded: Boolean = false,
    val filterMode: FilterMode = FilterMode.ALL
)

sealed class Nav {
    data class ViewReport(val doc: Uri) : Nav()
}

class RosterMotor(
    private val repo: ToDoRepository,
    private val report: RosterReport
) : ViewModel() {
    private val _states = MutableStateFlow(RosterViewState())
    val states = _states.asStateFlow()
    private var job: Job? = null

    private val _navEvents = MutableSharedFlow<Nav>()
    val navEvents = _navEvents.asSharedFlow()

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
}