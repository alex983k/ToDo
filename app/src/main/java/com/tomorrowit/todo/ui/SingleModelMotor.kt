package com.tomorrowit.todo.ui

import androidx.lifecycle.ViewModel
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository

class SingleModelMotor(
    private val repo: ToDoRepository,
    private val modelId: String?
) : ViewModel() {

    fun getModel() = repo.find(modelId)

    /*  If you're using '=' for a function you can skip declaring the type and calling return.
    *   Example below.
    */
//    fun getModel2(): ToDoModel? {
//        return repo.find(modelId)
//    }

    fun save(model: ToDoModel) {
        repo.save(model)
    }

    fun delete(model: ToDoModel) {
        repo.delete(model)
    }
}