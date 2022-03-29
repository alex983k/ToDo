package com.tomorrowit.todo.ui.roster

import androidx.lifecycle.ViewModel
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    fun getItems() = repo.items

    fun save(model: ToDoModel) {
        repo.save(model)
    }
}