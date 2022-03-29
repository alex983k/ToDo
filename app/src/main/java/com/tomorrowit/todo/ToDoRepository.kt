package com.tomorrowit.todo

class ToDoRepository {
    var items = listOf(
        ToDoModel(
            description = "Buy food from _Cool Food_",
            isCompleted = true,
            notes = "Check their website for discounts"
        ),
        ToDoModel(
            description = "Buy milk"
        ),
        ToDoModel(
            description = "Buy some eggs",
            notes = "Make sure to buy 10!"
        )
    )

    fun save(model: ToDoModel) {
        items = if (items.any { it.id == model.id }) {
            items.map { if (it.id == model.id) model else it }
        } else {
            items + model
        }
    }

    fun find(modelId: String) = items.find { it.id == modelId }
}