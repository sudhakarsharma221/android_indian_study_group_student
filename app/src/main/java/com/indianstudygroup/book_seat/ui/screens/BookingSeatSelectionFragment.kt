package com.indianstudygroup.book_seat.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.book_seat.model.BookingRequestModel
import com.indianstudygroup.book_seat.ui.adapter.SeatAdapter
import com.indianstudygroup.book_seat.viewModel.BookSeatViewModel
import com.indianstudygroup.databinding.ConfirmBookingBottomDialogBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.FragmentBookingSeatSelectionBinding


class BookingSeatSelectionFragment : Fragment() {
    private var endTimeHour: String? = null
    private var endTimeMinute: String? = null
    private var startTimeMinute: String? = null
    private var startTimeHour: String? = null
    private var userId: String? = null
    private var libId: String? = null
    private lateinit var binding: FragmentBookingSeatSelectionBinding
    private lateinit var dialogBinding: ConfirmBookingBottomDialogBinding
    private lateinit var adapter: SeatAdapter
    private var selectedPosition = -1
    private lateinit var bookSeatViewModel: BookSeatViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bookSeatViewModel = ViewModelProvider(requireActivity())[BookSeatViewModel::class.java]
        binding = FragmentBookingSeatSelectionBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
//        inflater.inflate(R.layout.fragment_booking_seat_selection, container, false)
        observeProgress()
        observerIdLibraryApiResponse()
        observerErrorMessageApiResponse()

        intiListener()
        return binding.root
    }

    private fun intiListener() {


        val totalSeats = requireArguments().getString("totalSeats")
        val vacantSeats = requireArguments().getString("vacantSeats")
        val priceSeats = requireArguments().getString("priceSeats")
        libId = requireArguments().getString("libId")
        userId = requireArguments().getString("userId")
        startTimeHour = requireArguments().getString("startTimeHour")
        startTimeMinute = requireArguments().getString("startTimeMinute")
        endTimeHour = requireArguments().getString("endTimeHour")
        endTimeMinute = requireArguments().getString("endTimeMinute")



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
            findNavController().navigate(R.id.action_bookingSeatSelectionFragment_to_bookingOrderSummaryFragment,
                Bundle().apply {
                    putString("priceSeats", priceSeats)
                })
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun showConfirmBookingDialog(price: String) {
        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialogBinding = ConfirmBookingBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.moreBill.setOnClickListener {
            bottomDialog.dismiss()
            findNavController().navigate(R.id.action_bookingSeatSelectionFragment_to_bookingOrderSummaryFragment,
                Bundle().apply {
                    putString("priceSeats", price)
                    putString("libId", libId)
                    putString("userId", userId)
                    putString("startTimeHour", "startTimeHour")
                    putString("startTimeMinute", "startTimeMinute")
                    putString("endTimeHour", "endTimeHour")
                    putString("endTimeMinute", "endTimeMinute")
                })
        }
        dialogBinding.tvTotalBill.text = "Total bill ₹ $price"
        dialogBinding.textView8.text = "Book For ₹ $price"
        dialogBinding.bookButton.setOnClickListener {
            bottomDialog.dismiss()

            callBookSeatApi(
                BookingRequestModel(
                    libId, userId, 0, "", startTimeHour, startTimeMinute, endTimeHour, endTimeMinute
                )
            )

        }
    }


    private fun callBookSeatApi(
        bookingRequestModel: BookingRequestModel
    ) {
        bookSeatViewModel.callBookSeat(bookingRequestModel)
    }

    private fun observerIdLibraryApiResponse() {
        bookSeatViewModel.bookSeatResponse.observe(viewLifecycleOwner, Observer {
            requireActivity().setResult(AppCompatActivity.RESULT_OK)
            requireActivity().finish()
            ApiCallsConstant.apiCallsOnceSchedule = false
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
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.messageTv.text = message
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
            requireActivity().finish()
        }
    }

}