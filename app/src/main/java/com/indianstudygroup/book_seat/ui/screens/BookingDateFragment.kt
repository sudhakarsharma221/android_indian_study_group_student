package com.indianstudygroup.book_seat.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.schedule.ui.adapter.ScheduleAdapter
import com.indianstudygroup.databinding.FragmentBookingDateBinding
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class BookingDateFragment : Fragment() {
    private lateinit var binding: FragmentBookingDateBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        binding = FragmentBookingDateBinding.inflate(layoutInflater)
        intiListener()
        getUserData()
        return binding.root
    }

    private fun intiListener() {


        binding.backButton.setOnClickListener {
            requireActivity().finish()
        }


//         findNavController().navigate(R.id.action_detailsFillFragment_to_photoFillFragment,
//                    Bundle().apply {
//                        putString("name", name)
//                        putString("pincode", pincode)
//                        putString("city", city)
//                        putString("state", state)
//                        putString("bio", bio)
//                        putString("qualification", selectedQualificationFromList ?: "")
//                    })


    }

    private fun getUserData(
    ) {
        val userData = userDetailsViewModel.getUserDetailsResponse()

        binding.submitButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_bookingDateFragment_to_bookingTimeSlotFragment,
                Bundle().apply {
                    putString("userId",userData?.userId)
                })
        }

        if (userData == null) {
            binding.error.visibility = View.VISIBLE
        } else {

            if (userData.sessions.isEmpty()) {
                binding.noSchedule.visibility = View.VISIBLE
            } else {
//                binding.recyclerView.visibility = View.VISIBLE
//                binding.recyclerView.layoutManager =
//                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//                adapter = ScheduleAdapter(requireContext())
//                binding.recyclerView.adapter = adapter
            }
        }

    }

}