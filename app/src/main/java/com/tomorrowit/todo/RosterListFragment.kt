package com.tomorrowit.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tomorrowit.todo.databinding.TodoRosterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RosterListFragment : Fragment() {

    private var binding: TodoRosterBinding? = null

    private val motor: RosterMotor by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TodoRosterBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //When a function type is the last parameter for a function call, we can use a lambda
        //expression outside of the function call parentheses. So, the lambda expression that
        //we have here turns into onCheckboxToggle.
        val adapter = RosterAdapter(layoutInflater) {
            //Here, we create the updated model by using copy(), a function added to all Kotlin
            //data classes. As the name suggests, copy() makes a copy of the immutable object,
            //except it replaces whatever properties we include as parameters to the copy() call. In
            //our case, we replace isCompleted with the opposite of its current value.
            motor.save(it.copy(isCompleted = !it.isCompleted))
        }

        binding?.items?.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        adapter.submitList(motor.items)
        binding?.empty?.visibility = View.GONE
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}