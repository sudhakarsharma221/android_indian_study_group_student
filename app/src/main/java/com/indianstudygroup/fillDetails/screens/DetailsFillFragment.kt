package com.indianstudygroup.fillDetails.screens

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.FragmentDetailsFillBinding
import com.indianstudygroup.pincode.PincodeViewModel


class DetailsFillFragment : Fragment() {
    private lateinit var binding: FragmentDetailsFillBinding

    private lateinit var pincodeViewModel: PincodeViewModel
    private val namePattern = "^[a-zA-Z]+$"

    private lateinit var district: String
    lateinit var state: String
    private var selectedQualificationFromList = ""
    private lateinit var selectedQualificationButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsFillBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        pincodeViewModel = ViewModelProvider(this)[PincodeViewModel::class.java]

//        inflater.inflate(R.layout.fragment_details_fill, container, false)

        observerPincodeApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        initListener()
        focusChangeListeners()
        return binding.root
    }

    private fun initListener() {

        setButtonState(binding.buttonGraduation, false)
        setButtonState(binding.buttonIntermediate, false)
        setButtonState(binding.buttonHghSchool, false)
        setButtonState(binding.buttonPostGraduation, false)

        binding.buttonGraduation.setOnClickListener {
            toggleButtonState(binding.buttonGraduation)
        }
        binding.buttonIntermediate.setOnClickListener {
            toggleButtonState(binding.buttonIntermediate)
        }
        binding.buttonHghSchool.setOnClickListener {
            toggleButtonState(binding.buttonHghSchool)
        }
        binding.buttonPostGraduation.setOnClickListener {
            toggleButtonState(binding.buttonPostGraduation)
        }

        binding.backButton.setOnClickListener {
            requireActivity().finishAffinity()
        }
        binding.pincodeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used, but needs to be implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used, but needs to be implemented
            }

            override fun afterTextChanged(s: Editable?) {
                val pincode = s.toString().trim()
                if (pincode.length == 6) {
                    callPincodeApi(pincode)
                }
            }
        })

        binding.submitButton.setOnClickListener {

            val name = binding.nameEt.text.toString()
            val pincode = binding.pincodeEt.text.toString()
            val state = binding.tvState.text.toString()
            val city = binding.tvCity.text.toString()
            val bio = binding.aboutET.text.toString()

            if (name.trim().isEmpty()) {
                binding.nameEt.error = "Empty Field"
            } else if (name.length < 2) {
                binding.nameEt.error = "Enter minimum 2 characters"
            } else if (name.length > 30) {
                binding.nameEt.error = "Enter less than 30 characters"
            } else if (bio.isNotEmpty() && bio.isBlank()) {
                binding.aboutET.error = "Bio should not contain only spaces"
            } else if (pincode.trim().isEmpty()) {
                binding.pincodeEt.error = "Empty Field"
            } else if (pincode.length < 6) {
                binding.pincodeEt.error = "Enter Valid Pincode"
            } else if (selectedQualificationFromList.isEmpty()) {
                binding.tvchoosequalification.visibility = View.VISIBLE
            } else {
                Log.d(
                    "DATAFROMSCREENS",
                    "$name $pincode $city $state $bio $selectedQualificationFromList"
                )
                findNavController().navigate(
                    R.id.action_detailsFillFragment_to_photoFillFragment,
                    Bundle().apply {
                        putString("name", name)
                        putString("pincode", pincode)
                        putString("city", city)
                        putString("state", state)
                        putString("bio", bio)
                        putString("qualification", selectedQualificationFromList ?: "")
                    })
            }

        }

    }

    private fun callPincodeApi(pincode: String?) {
        pincodeViewModel.callPinCodeDetails(pincode)
    }

    private fun observeProgress() {

        pincodeViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        pincodeViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    private fun observerPincodeApiResponse() {
        pincodeViewModel.pincodeResponse.observe(viewLifecycleOwner, Observer {
            Log.d("testPINCODEAPI", it.toString())
            if (it[0].postOffice == null) {
                ToastUtil.makeToast(requireContext(), "Please enter valid pincode")

            } else {
                state = it[0].postOffice?.get(0)?.state!!
                district = it[0].postOffice?.get(0)?.district!!
                binding.tvCity.text = district
                binding.tvState.text = state
            }

        })
    }

    private fun toggleButtonState(textView: TextView) {
        // Deselect previously selected button
        if (::selectedQualificationButton.isInitialized) {
            selectedQualificationButton.isSelected = false
            setButtonState(selectedQualificationButton, false)
        }
        // Select the clicked button
        textView.isSelected = true
        setButtonState(textView, true)
        selectedQualificationButton = textView
        selectedQualificationFromList = textView.text.toString()
    }

    private fun setButtonState(textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.background_button) // Change to selected background
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(), android.R.color.white
                )
            ) // Change to white text color
        } else {
            textView.setBackgroundResource(R.drawable.background_button_color_change) // Revert to normal background
            textView.setTextColor(
                Color.parseColor("#747688")
            ) // Revert to original text color
        }
    }


    private fun focusChangeListeners() {

        binding.nameEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length < 2
                ) {
                    binding.nameEt.error = "Enter Minimum 2 Characters"
                } else if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length > 30
                ) {
                    binding.nameEt.error = "Enter Less Than 30 Characters"
                }
            }
        }

        binding.pincodeEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.pincodeEt.text.toString().trim()
                        .isNotEmpty() && binding.pincodeEt.text.toString().length < 6
                ) {
                    binding.pincodeEt.error = "Enter valid pincode"
                }
            }
        }


    }
}