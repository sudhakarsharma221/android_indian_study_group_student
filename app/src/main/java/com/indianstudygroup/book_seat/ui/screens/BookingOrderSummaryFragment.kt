package com.indianstudygroup.book_seat.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.databinding.FragmentBookingOrderSummaryBinding

class BookingOrderSummaryFragment : Fragment() {
    private lateinit var binding: FragmentBookingOrderSummaryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingOrderSummaryBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
//         inflater.inflate(R.layout.fragment_booking_order_summary, container, false)
        initListener()
        return binding.root
    }

    private fun initListener() {

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.submitButton.setOnClickListener {
            requireActivity().setResult(AppCompatActivity.RESULT_OK)
            requireActivity().finish()
        }
    }
}