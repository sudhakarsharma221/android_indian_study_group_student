package com.indianstudygroup.bottom_nav_bar.schedule.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.viewModel.ScheduleViewModel
import com.indianstudygroup.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {


    private lateinit var viewModel: ScheduleViewModel
    private lateinit var binding: FragmentScheduleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]
        binding = FragmentScheduleBinding.inflate(layoutInflater)
//         inflater.inflate(R.layout.fragment_schedule, container, false)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.historyButton.setOnClickListener {
            ToastUtil.makeToast(requireContext(), "History Activity")
        }
    }
}