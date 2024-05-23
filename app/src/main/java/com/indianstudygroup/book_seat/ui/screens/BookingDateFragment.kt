package com.indianstudygroup.book_seat.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModelItem
import com.indianstudygroup.bottom_nav_bar.schedule.ui.adapter.ScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.viewModel.ScheduleViewModel
import com.indianstudygroup.databinding.FragmentBookingDateBinding
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.Calendar

class BookingDateFragment : Fragment() {
    private lateinit var binding: FragmentBookingDateBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var libViewModel: LibraryViewModel
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        binding = FragmentBookingDateBinding.inflate(layoutInflater)
        libViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        intiListener()
        viewModel.callScheduleDetails(auth.currentUser!!.uid)

        getUserData()
        observeProgress()
        observerErrorMessageApiResponse()
        observerIdLibraryApiResponse()
        return binding.root
    }

    private fun intiListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        configureCalendarView()

        binding.backButton.setOnClickListener {
            requireActivity().finish()
        }

    }

    private fun configureCalendarView() {
        val calendarView = binding.calendarView

        // Get current date
        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        val todayInMillis = currentDate.timeInMillis

        // Set minimum and maximum date to today
        calendarView.minDate = todayInMillis
        calendarView.maxDate = todayInMillis

        // Set the date to today
        calendarView.date = todayInMillis

        // Optionally, handle date changes if required
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            if (selectedDate.timeInMillis != todayInMillis) {
                // Reset to today's date if user tries to select a different date
                calendarView.date = todayInMillis
            }
        }
    }

    private fun getUserData(
    ) {


        val userData = userDetailsViewModel.getUserDetailsResponse()

        binding.submitButton.setOnClickListener {
            binding.progressBar.visibility = View.GONE
            findNavController().navigate(R.id.action_bookingDateFragment_to_bookingTimeSlotFragment,
                Bundle().apply {
                    putString("userId", userData?.userId)
                })
        }
    }

    private fun observerIdLibraryApiResponse() {
        viewModel.sessionsDetailsResponse.observe(viewLifecycleOwner, Observer {
            val libraryResponse = libViewModel.getLibraryDetailsResponse()
            val adapterList: ArrayList<ScheduleResponseModelItem> = arrayListOf()

            var noItem = true
            it.forEach { item ->
                if (item.libraryId == libraryResponse?.libData?.id) {
                    adapterList.add(item)
                    noItem = false
                }
            }
            if (it.isEmpty()) {
                binding.noSchedule.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else if (noItem) {
                binding.noSchedule.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter = ScheduleAdapter(requireContext(), adapterList)
                binding.recyclerView.adapter = adapter
            }
        })
    }

    private fun observeProgress() {

        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        })

    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            binding.error.visibility = View.VISIBLE
        })
    }

}