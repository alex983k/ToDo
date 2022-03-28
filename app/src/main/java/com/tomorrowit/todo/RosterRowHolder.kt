package com.tomorrowit.todo

import androidx.recyclerview.widget.RecyclerView
import com.tomorrowit.todo.databinding.TodoRowBinding

class RosterRowHolder(
    private val binding: TodoRowBinding,
    val onCheckBoxToggle: (ToDoModel) -> Unit,
    val onRowClick: (ToDoModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: ToDoModel) {
        binding.apply {
            root.setOnClickListener { onRowClick(model) }
            isCompleted.isChecked = model.isCompleted
            isCompleted.setOnCheckedChangeListener { _, _ -> onCheckBoxToggle(model) }
            desc.text = model.description
        }
    }
}