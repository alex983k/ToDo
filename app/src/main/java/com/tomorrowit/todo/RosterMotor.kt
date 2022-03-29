package com.tomorrowit.todo

import androidx.lifecycle.ViewModel

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    val items = repo.items

    fun save(model: ToDoModel) {
        repo.save(model)
    }
}