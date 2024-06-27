package com.indianstudygroup.bottom_nav_bar.schedule.library.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.library.ui.adapter.LibraryScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.library.viewModel.LibraryScheduleViewModel
import com.indianstudygroup.databinding.FragmentLibraryScheduleBinding


class LibraryScheduleFragment : Fragment() {

    private lateinit var binding: FragmentLibraryScheduleBinding
    private lateinit var viewModel: LibraryScheduleViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: LibraryScheduleAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryScheduleBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LibraryScheduleViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE
//         inflater.inflate(R.layout.fragment_schedule, container, false)
        if (!ApiCallsConstant.apiCallsOnceScheduleLibrary) {
            viewModel.callScheduleDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceScheduleLibrary = true
        }
        initListener()
        observeProgress()
        observerErrorMessageApiResponse()
        observerSessionApiResponse()
        return binding.root
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.swiperefresh.setOnRefreshListener {
            viewModel.callScheduleDetails(auth.currentUser!!.uid)
        }

        binding.historyButton.setOnClickListener {
            IntentUtil.startIntent(requireContext(), HistoryActivity())
        }
    }


    private fun observerSessionApiResponse() {
        viewModel.sessionsDetailsResponse.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.noSchedule.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                Log.d("SCHEDULERESPONSE", it.toString())
                adapter = LibraryScheduleAdapter(requireContext(), it)
                binding.recyclerView.adapter = adapter
            }
            binding.swiperefresh.isRefreshing = false
        })
    }

    private fun observeProgress() {

        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })

    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

}