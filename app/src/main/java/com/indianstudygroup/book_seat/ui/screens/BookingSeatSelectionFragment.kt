package com.indianstudygroup.book_seat.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.book_seat.ui.adapter.SeatAdapter
import com.indianstudygroup.databinding.ConfirmBookingBottomDialogBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.FragmentBookingSeatSelectionBinding


class BookingSeatSelectionFragment : Fragment() {
    private lateinit var binding: FragmentBookingSeatSelectionBinding
    private lateinit var adapter: SeatAdapter
    private var selectedPosition = -1
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


        val totalSeats = requireArguments().getString("totalSeats")
        val vacantSeats = requireArguments().getString("vacantSeats")
        val priceSeats = requireArguments().getString("priceSeats")
        val libId = requireArguments().getString("libId")
        val userId = requireArguments().getString("userId")
        val startTimeHour = requireArguments().getString("startTimeHour")
        val startTimeMinute = requireArguments().getString("startTimeMinute")
        val endTimeHour = requireArguments().getString("endTimeHour")
        val endTimeMinute = requireArguments().getString("endTimeMinute")



        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        adapter = SeatAdapter(
            requireContext(), totalSeats?.toInt() ?: 0, vacantSeats?.toInt() ?: 0
        ) { position ->
            selectedPosition = position

            if (selectedPosition > -1) {
                binding.tvSeatsSelected.text = "Number of Seats Selected : 1"
            } else {
                binding.tvSeatsSelected.text = "Number of Seats Selected : 0"
            }
        }
        binding.recyclerView.adapter = adapter

        binding.tvSeats.text = HtmlCompat.fromHtml(
            "Seats Available : <b>$vacantSeats / $totalSeats </b>", HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvTotalBill.text = "Total bill ₹ $priceSeats"

        binding.submitButton.setOnClickListener {
            if (selectedPosition < 0) {
                ToastUtil.makeToast(requireContext(), "Select a seat first")
            } else {
                showConfirmBookingDialog(priceSeats.toString())
            }

        }
        binding.moreBill.setOnClickListener {
            findNavController().navigate(R.id.action_bookingSeatSelectionFragment_to_bookingOrderSummaryFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun showConfirmBookingDialog(price: String) {
        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val dialogBinding = ConfirmBookingBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.moreBill.setOnClickListener {
            bottomDialog.dismiss()
            findNavController().navigate(R.id.action_bookingSeatSelectionFragment_to_bookingOrderSummaryFragment)
        }
        dialogBinding.tvTotalBill.text = "Total bill ₹ $price"
        dialogBinding.textView8.text = "Book For ₹ $price"
        dialogBinding.bookButton.setOnClickListener {
            requireActivity().setResult(AppCompatActivity.RESULT_OK)
            requireActivity().finish()
        }
    }


}