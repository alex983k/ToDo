package com.tomorrowit.todo

import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.todo.databinding.TodoRowBinding

class RosterRowHolder(private val binding: TodoRowBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: ToDoModel) {
        binding.apply {
            isCompleted.isChecked = model.isCompleted
            desc.text = model.description
        }
    }
}