package com.indianstudygroup.book_seat_gym.ui.screens

import android.os.Bundle
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
import com.indianstudygroup.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indianstudygroup.bottom_nav_bar.schedule.gym.model.GymScheduleResponseModelItem
import com.indianstudygroup.bottom_nav_bar.schedule.gym.ui.adapter.GymScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.gym.viewModel.GymScheduleViewModel
import com.indianstudygroup.databinding.FragmentGymBookingDateBinding
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.Calendar

class GymBookingDateFragment : Fragment() {
    private lateinit var binding: FragmentGymBookingDateBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var viewModel: GymScheduleViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var gymViewModel: GymViewModel
    private lateinit var adapter: GymScheduleAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGymBookingDateBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        gymViewModel = ViewModelProvider(requireActivity())[GymViewModel::class.java]
        viewModel = ViewModelProvider(this)[GymScheduleViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        intiListener()
        viewModel.callGymScheduleDetails(auth.currentUser!!.uid)

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
            findNavController().navigate(
                R.id.action_gymBookingDateFragment_to_gymBookingTimeSlotFragment2,
                Bundle().apply {
                    putString("userId", userData?.userId)
                })
        }
    }

    private fun observerIdLibraryApiResponse() {
        viewModel.sessionsGymDetailsResponse.observe(viewLifecycleOwner, Observer {
            val gymResponse = gymViewModel.getGymDetailsResponse()
            val adapterList: ArrayList<GymScheduleResponseModelItem> = arrayListOf()

            var noItem = true
            it.forEach { item ->
                if (item.gymId == gymResponse?.gymData?.id) {
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
                adapter = GymScheduleAdapter(requireContext(), adapterList)
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