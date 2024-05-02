package com.indianstudygroup.book_seat.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.R
import com.indianstudygroup.databinding.FragmentBookingDateBinding

class BookingDateFragment : Fragment() {
    private lateinit var binding: FragmentBookingDateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//         inflater.inflate(R.layout.fragment_booking_date, container, false)
binding = FragmentBookingDateBinding.inflate(layoutInflater)
        intiListener()
        return binding.root
    }

    private fun intiListener() {

        binding.submitButton.setOnClickListener {
            findNavController().navigate(R.id.action_bookingDateFragment_to_bookingTimeSlotFragment)
        }
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

}