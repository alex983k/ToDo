package com.tomorrowit.todo.ui.roster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RosterViewState(
    val items: List<ToDoModel> = listOf()
)

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    val states = repo.items()
        .map { RosterViewState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, RosterViewState())

    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repo.save(model)
        }
    }
}