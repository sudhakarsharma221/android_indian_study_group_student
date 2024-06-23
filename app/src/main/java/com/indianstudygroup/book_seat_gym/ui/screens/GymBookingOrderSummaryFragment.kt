package com.indianstudygroup.book_seat_gym.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.book_seat_gym.model.GymBookingRequestModel
import com.indianstudygroup.book_seat_gym.viewModel.GymBookSeatViewModel
import com.indianstudygroup.databinding.ConfirmBookingBottomDialogBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.FragmentGymBookingOrderSummaryBinding

class GymBookingOrderSummaryFragment : Fragment() {
    private lateinit var binding: FragmentGymBookingOrderSummaryBinding

    private var endTimeHour: String? = null
    private var endTimeMinute: String? = null
    private var startTimeMinute: String? = null
    private var startTimeHour: String? = null
    private var userId: String? = null
    private var slot: Int? = null
    private var gymId: String? = null
    private lateinit var dialogBinding: ConfirmBookingBottomDialogBinding

    private lateinit var bookSeatViewModel: GymBookSeatViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGymBookingOrderSummaryBinding.inflate(layoutInflater)
        bookSeatViewModel = ViewModelProvider(requireActivity())[GymBookSeatViewModel::class.java]

        // Inflate the layout for this fragment
//         inflater.inflate(R.layout.fragment_booking_order_summary, container, false)
        initListener()
        observeProgress()
        observerBookingApiResponse()
        observerErrorMessageApiResponse()
        return binding.root
    }

    private fun initListener() {

        gymId = requireArguments().getString("gymId")
        userId = requireArguments().getString("userId")
        slot = requireArguments().getInt("slot")
        startTimeHour = requireArguments().getString("startTimeHour")
        startTimeMinute = requireArguments().getString("startTimeMinute")
        endTimeHour = requireArguments().getString("endTimeHour")
        endTimeMinute = requireArguments().getString("endTimeMinute")

        val priceSeats = requireArguments().getString("priceSeats")
        binding.tvGrandTotal.text = "₹ $priceSeats"
        binding.totalBill.text = "Total bill ₹ $priceSeats"

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.submitButton.setOnClickListener {
            showConfirmBookingDialog(priceSeats.toString())

        }
    }

    private fun showConfirmBookingDialog(price: String) {
        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialogBinding = ConfirmBookingBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.moreBill.visibility = View.GONE
        dialogBinding.tvTotalBill.text = "Total bill ₹ $price"
        dialogBinding.textView8.text = "Book For ₹ $price"
        dialogBinding.bookButton.setOnClickListener {
            bottomDialog.dismiss()
            Log.d(
                "BOOKINGSEATRESPONSECOMING",
                "  $slot  $startTimeHour   $startTimeMinute    $endTimeHour   $endTimeMinute"
            )
            callBookSeatApi(
                GymBookingRequestModel(
                    gymId,
                    userId,
                    slot,
                    "",
                    startTimeHour,
                    startTimeMinute,
                    endTimeHour,
                    endTimeMinute
                )
            )

        }
    }


    private fun callBookSeatApi(
        bookingRequestModel: GymBookingRequestModel
    ) {
        bookSeatViewModel.callBookSeat(bookingRequestModel)
    }

    private fun observerBookingApiResponse() {
        bookSeatViewModel.gymSeatResponse.observe(viewLifecycleOwner, Observer {
            requireActivity().setResult(AppCompatActivity.RESULT_OK)
            requireActivity().finish()
            ApiCallsConstant.apiCallsOnceScheduleGym = false
        })
    }

    private fun observeProgress() {
        bookSeatViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        bookSeatViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            showErrorBottomDialog(it)
        })
    }


    private fun showErrorBottomDialog(message: String) {
        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(false)
        bottomDialog.show()
        dialogBinding.messageTv.text = message
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
            requireActivity().finish()
        }
    }
}