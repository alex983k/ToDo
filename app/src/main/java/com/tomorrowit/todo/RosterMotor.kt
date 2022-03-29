package com.tomorrowit.todo

import androidx.lifecycle.ViewModel

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    fun getItems() = repo.items

    fun save(model: ToDoModel) {
        repo.save(model)
    }
}