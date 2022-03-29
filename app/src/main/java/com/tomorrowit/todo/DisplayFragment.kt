package com.tomorrowit.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.tomorrowit.todo.databinding.TodoDisplayBinding

class DisplayFragment : Fragment() {
    private val args: DisplayFragmentArgs by navArgs()

    private var binding: TodoDisplayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TodoDisplayBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}