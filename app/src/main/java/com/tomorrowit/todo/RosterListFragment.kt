package com.tomorrowit.todo

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tomorrowit.todo.databinding.TodoRosterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RosterListFragment : Fragment() {

    private var binding: TodoRosterBinding? = null

    private val motor: RosterMotor by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TodoRosterBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Old implementation, good for studying, inline comments for old code, multiple line for explanation.
        /*When a function type is the last parameter for a function call, we can use a lambda
        expression outside of the function call parentheses. So, the lambda expression that
        we have here turns into onCheckboxToggle.*/
        //val adapter = RosterAdapter(layoutInflater) {
        /*Here, we create the updated model by using copy(), a function added to all Kotlin
        data classes. As the name suggests, copy() makes a copy of the immutable object,
        except it replaces whatever properties we include as parameters to the copy() call. In
        our case, we replace isCompleted with the opposite of its current value.*/
        //motor.save(it.copy(isCompleted = !it.isCompleted))
        //}

        val adapter = RosterAdapter(
            layoutInflater,
            onCheckboxToggle = { motor.save(it.copy(isCompleted = !it.isCompleted)) },
            onRowClick = ::display
        )

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

        adapter.submitList(motor.getItems())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                add()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun add() {
        findNavController().navigate(RosterListFragmentDirections.createModel(null))
    }

    private fun display(model: ToDoModel) {
        findNavController().navigate(RosterListFragmentDirections.displayModel(model.id))
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}