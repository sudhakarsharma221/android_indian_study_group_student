package com.indianstudygroup.bottom_nav_bar.schedule.ui

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.ui.adapter.ScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.viewModel.ScheduleViewModel
import com.indianstudygroup.databinding.FragmentScheduleBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var adapter: ScheduleAdapter
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var userData: UserDetailsResponseModel
    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE
//         inflater.inflate(R.layout.fragment_schedule, container, false)
        if (!ApiCallsConstant.apiCallsOnceSchedule) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceSchedule = true
        }
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        observerIdLibraryApiResponse()
        return binding.root
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (!ApiCallsConstant.apiCallsOnceScheduleLibrary) {
            Log.d("SCHEDULEID1", userData.sessions.toString())
            userData.sessions.forEach {
                callIdLibraryDetailsApi(it.libraryId)
            }
            ApiCallsConstant.apiCallsOnceScheduleLibrary = true
        }

        binding.swiperefresh.setOnRefreshListener {
            libraryList.clear()
            userData.sessions.forEach {
                callIdLibraryDetailsApi(it.libraryId)
            }
        }

        binding.historyButton.setOnClickListener {
            ToastUtil.makeToast(requireContext(), "History Activity")
        }
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it
            if (userData.sessions.isEmpty()) {
                binding.noSchedule.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            initListener()

            binding.swiperefresh.isRefreshing = false
        })
    }

    private fun callIdLibraryDetailsApi(
        id: String?
    ) {
        libraryDetailsViewModel.callIdLibrary(id)
    }

    private fun observerIdLibraryApiResponse() {
        libraryDetailsViewModel.idLibraryResponse.observe(viewLifecycleOwner, Observer {
            it.libData?.let { it1 -> libraryList.add(it1) }
            adapter = ScheduleAdapter(requireContext(), libraryList)
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.swiperefresh.isRefreshing = false

        })
    }

    private fun observeProgress() {

        userDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
//                binding.noLibAvailable.visibility = View.GONE
//                binding.pincodeRecyclerView.visibility = View.GONE
//                binding.shimmerLayout.startShimmer()
            } else {
                binding.progressBar.visibility = View.GONE
//                binding.pincodeRecyclerView.visibility = View.VISIBLE
//                binding.shimmerLayout.stopShimmer()
            }
        })
        libraryDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
//                binding.noLibAvailable.visibility = View.GONE
//                binding.pincodeRecyclerView.visibility = View.GONE
//                binding.shimmerLayout.startShimmer()
            } else {
                binding.progressBar.visibility = View.GONE
//                binding.pincodeRecyclerView.visibility = View.VISIBLE
//                binding.shimmerLayout.stopShimmer()
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        libraryDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

}