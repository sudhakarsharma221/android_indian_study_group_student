package com.indianstudygroup.coaching

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indianstudygroup.databinding.FragmentCoachingBinding
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel

class CoachingFragment(val userData: UserDetailsResponseModel) : Fragment() {
    private lateinit var binding: FragmentCoachingBinding
    private lateinit var viewModel: CoachingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CoachingViewModel::class.java]
        binding = FragmentCoachingBinding.inflate(layoutInflater)
//         inflater.inflate(R.layout.fragment_coaching, container, false)
        return binding.root
    }

}