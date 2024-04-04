package com.indianstudygroup.bottom_nav_bar.home.tab_layout.screens.library

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indianstudygroup.R
import com.indianstudygroup.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding
    private lateinit var viewModel: LibraryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]

//         inflater.inflate(R.layout.fragment_library, container, false)

        return binding.root
    }


}