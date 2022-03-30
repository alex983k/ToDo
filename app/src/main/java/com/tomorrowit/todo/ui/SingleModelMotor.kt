package com.tomorrowit.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SingleModelViewState(
    val item: ToDoModel? = null
)

class SingleModelMotor(
    private val repo: ToDoRepository,
    private val modelId: String?
) : ViewModel() {
    val states = repo.find(modelId)
        .map { SingleModelViewState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SingleModelViewState())

    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repo.save(model)
        }
    }

    fun delete(model: ToDoModel) {
        viewModelScope.launch {
            repo.delete(model)
        }
    }
}