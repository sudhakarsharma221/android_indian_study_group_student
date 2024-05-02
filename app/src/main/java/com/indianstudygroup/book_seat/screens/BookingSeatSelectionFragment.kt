package com.indianstudygroup.book_seat.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.R
import com.indianstudygroup.databinding.FragmentBookingSeatSelectionBinding


class BookingSeatSelectionFragment : Fragment() {
    private lateinit var binding: FragmentBookingSeatSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingSeatSelectionBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
//        inflater.inflate(R.layout.fragment_booking_seat_selection, container, false)


        intiListener()
        return binding.root
    }

    private fun intiListener() {
        binding.submitButton.setOnClickListener {
            findNavController().navigate(R.id.action_bookingSeatSelectionFragment_to_bookingOrderSummaryFragment)

        }


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}