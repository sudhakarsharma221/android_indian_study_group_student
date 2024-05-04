package com.indianstudygroup.book_seat.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.R
import com.indianstudygroup.databinding.FragmentBookingTimeSlotBinding
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel

class BookingTimeSlotFragment : Fragment() {
    private lateinit var binding: FragmentBookingTimeSlotBinding
    private lateinit var viewModel: LibraryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingTimeSlotBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]

        // Inflate the layout for this fragment
//        inflater.inflate(R.layout.fragment_booking_time_slot, container, false)

        initListener()
        return binding.root

    }

    private fun initListener() {
        callLibraryResponse()


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun callLibraryResponse(
    ) {
        val userId = requireArguments().getString("userId").toString()


        val libraryResponse = viewModel.getLibraryDetailsResponse()

        val timing = libraryResponse?.libData?.timing

        binding.tvMorning.text = "${timing?.get(0)?.from} to ${timing?.get(0)?.to}"
        binding.tvNoon.text = "${timing?.get(1)?.from} to ${timing?.get(1)?.to}"
        binding.submitButton.setOnClickListener {
            findNavController().navigate(R.id.action_bookingTimeSlotFragment_to_bookingSeatSelectionFragment,
                Bundle().apply {
                    putString("totalSeats", libraryResponse?.libData?.seats.toString())
                    putString("vacantSeats", libraryResponse?.libData?.vacantSeats.toString())
                    putString("priceSeats", libraryResponse?.libData?.pricing?.daily.toString())
                    putString("libId", libraryResponse?.libData?.Id)
                    putString("userId", userId)
                    putString("startTimeHour", "startTimeHour")
                    putString("startTimeMinute", "startTimeMinute")
                    putString("endTimeHour", "endTimeHour")
                    putString("endTimeMinute", "endTimeMinute")
                })
        }

    }


}