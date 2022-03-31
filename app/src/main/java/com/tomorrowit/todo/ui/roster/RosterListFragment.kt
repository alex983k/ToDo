package com.tomorrowit.todo.ui.roster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tomorrowit.todo.R
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.databinding.TodoRosterBinding
import com.tomorrowit.todo.repo.FilterMode
import com.tomorrowit.todo.ui.ErrorDialogFragment
import com.tomorrowit.todo.ui.ErrorScenario
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "ToDo"

class RosterListFragment : Fragment() {
    private var binding: TodoRosterBinding? = null

    private val motor: RosterMotor by viewModel()

    private val menuMap = mutableMapOf<FilterMode, MenuItem>()

    private val createDoc =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) {
            motor.saveReport(it)
        }

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

//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            motor.states.collect() {state ->
//
//            }
//
//        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.states.collect() { state ->
                adapter.submitList(state.items)
                binding?.apply {
                    progressBar.visibility = View.GONE
                    when {
                        state.items.isEmpty() && state.filterMode == FilterMode.ALL -> {
                            empty.visibility = View.VISIBLE
                            empty.setText(R.string.msg_empty)
                        }
                        state.items.isEmpty() -> {
                            empty.visibility = View.VISIBLE
                            empty.setText(R.string.msg_empty_filtered)
                        }
                        else -> empty.visibility = View.GONE
                    }
                }
                menuMap[state.filterMode]?.isChecked = true
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.navEvents.collect { nav ->
                when (nav) {
                    is Nav.ViewReport -> viewReport(nav.doc)
                    is Nav.ShareReport -> shareReport(nav.doc)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.errorEvents.collect { error ->
                when (error) {
                    ErrorScenario.Import -> handleImportError()
                }
            }
        }
        findNavController()
            .getBackStackEntry(R.id.rosterListFragment)
            .savedStateHandle
            .getLiveData<ErrorScenario>(ErrorDialogFragment.KEY_RETRY)
            .observe(viewLifecycleOwner) { retryScenario ->
                when (retryScenario) {
                    ErrorScenario.Import -> {
                        clearImportError()
                        motor.importItems()
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster, menu)

        menuMap.apply {
            put(FilterMode.ALL, menu.findItem(R.id.all))
            put(FilterMode.COMPLETED, menu.findItem(R.id.completed))
            put(FilterMode.OUTSTANDING, menu.findItem(R.id.outstanding))
        }

        menuMap[motor.states.value.filterMode]?.isChecked = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                add()
                return true
            }
            R.id.all -> {
                item.isChecked = true
                motor.load(FilterMode.ALL)
                return true
            }
            R.id.completed -> {
                item.isChecked = true
                motor.load(FilterMode.COMPLETED)
                return true
            }
            R.id.outstanding -> {
                item.isChecked = true
                motor.load(FilterMode.OUTSTANDING)
                return true
            }
            R.id.save -> {
                saveReport()
                return true
            }
            R.id.share -> {
                motor.shareReport()
                return true
            }
            R.id.importItems -> {
                motor.importItems()
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

    private fun safeStartActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (t: Throwable) {
            Log.e(TAG, "Exception starting $intent", t)
            Toast.makeText(requireActivity(), R.string.oops, Toast.LENGTH_LONG).show()
        }
    }

    private fun viewReport(uri: Uri) {
        safeStartActivity(
            Intent(Intent.ACTION_VIEW, uri)
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
    }

    private fun saveReport() {
        createDoc.launch("report.html")
    }

    private fun shareReport(doc: Uri) {
        safeStartActivity(
            Intent(Intent.ACTION_SEND)
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("text/html")
                .putExtra(Intent.EXTRA_STREAM, doc)
        )
    }

    private fun handleImportError() {
        findNavController().navigate(
            RosterListFragmentDirections.showError(
                getString(R.string.import_error_title),
                getString(R.string.import_error_message),
                ErrorScenario.Import
            )
        )
    }

    private fun clearImportError() {
        findNavController()
            .getBackStackEntry(R.id.rosterListFragment)
            .savedStateHandle
            .set(ErrorDialogFragment.KEY_RETRY, ErrorScenario.None)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}