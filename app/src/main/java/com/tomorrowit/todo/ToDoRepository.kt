package com.tomorrowit.todo

class ToDoRepository {
    var items = emptyList<ToDoModel>()

    fun save(model: ToDoModel) {
        items = if (items.any { it.id == model.id }) {
            items.map { if (it.id == model.id) model else it }
        } else {
            items + model
        }
    }

    fun delete(model: ToDoModel) {
        //Here we just replace items with a filtered edition of items, keeping any item that has an ID different than the one that we are trying to remove.
        items = items.filter { it.id != model.id }
    }

    fun find(modelId: String?) = items.find { it.id == modelId }
}