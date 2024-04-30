package com.indianstudygroup.fillDetails.screens

import android.graphics.Color
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.MainActivity
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.FragmentTopicFillBinding
import com.indianstudygroup.userDetailsApi.model.Address
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class TopicFillFragment : Fragment() {
    private lateinit var binding: FragmentTopicFillBinding
    lateinit var viewModel: UserDetailsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedTopics: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicFillBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        selectedTopics = arrayListOf()
//        inflater.inflate(R.layout.fragment_topic_fill, container, false)
        initListener()
        observerUserFillDetailsApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()

        return binding.root
    }


    private fun initListener() {
        setButtonState(binding.buttonCds, false)
        setButtonState(binding.buttonChsl, false)
        setButtonState(binding.buttonJee, false)
        setButtonState(binding.buttonNda, false)
        setButtonState(binding.buttonNeet, false)
        setButtonState(binding.buttonDefence, false)
        setButtonState(binding.buttonPolice, false)
        setButtonState(binding.buttoncgl, false)

        binding.buttonCds.setOnClickListener {
            toggleButtonState(binding.buttonCds)
        }
        binding.buttonChsl.setOnClickListener {
            toggleButtonState(binding.buttonChsl)
        }
        binding.buttonJee.setOnClickListener {
            toggleButtonState(binding.buttonJee)
        }
        binding.buttonNda.setOnClickListener {
            toggleButtonState(binding.buttonNda)
        }
        binding.buttonNeet.setOnClickListener {
            toggleButtonState(binding.buttonNeet)
        }
        binding.buttonDefence.setOnClickListener {
            toggleButtonState(binding.buttonDefence)
        }
        binding.buttonPolice.setOnClickListener {
            toggleButtonState(binding.buttonPolice)
        }
        binding.buttoncgl.setOnClickListener {
            toggleButtonState(binding.buttoncgl)
        }


        val name = requireArguments().getString("name").toString()
        val pincode = requireArguments().getString("pincode").toString()
        val city = requireArguments().getString("city").toString()
        val state = requireArguments().getString("state").toString()
        val bio = requireArguments().getString("bio").toString()
        val qualification = requireArguments().getString("qualification").toString()
        val photoUrl = requireArguments().getString("photoUrl").toString()

        Log.d(
            "DATAFROMSCREENS",
            "$name $pincode $city $state $bio $qualification , $photoUrl"
        )



        binding.tvSkip.setOnClickListener {
            callPutUserDetailsApi(
                auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
                    name.trim(),
                    photoUrl ?: "",
                    Address(state.trim(), city, pincode),
                    bio ?: "",
                    qualification,
                    arrayListOf()
                )
            )
        }
        binding.submitButton.setOnClickListener {
            if (selectedTopics.isEmpty()) {
                ToastUtil.makeToast(requireContext(), "Select at least one topic")
            } else {
                Log.d(
                    "DATAFROMSCREENS",
                    "$name $pincode $city $state $bio $qualification , $photoUrl $selectedTopics"
                )

                callPutUserDetailsApi(
                    auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
                        name.trim(),
                        photoUrl ?: "",
                        Address(state.trim(), city, pincode),
                        bio ?: "",
                        qualification,
                        selectedTopics ?: arrayListOf()
                    )
                )
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }


    private fun callPutUserDetailsApi(
        userId: String?, putUserDetailsPutRequestBodyModel: UserDetailsPutRequestBodyModel
    ) {
        viewModel.callPutUserDetails(userId, putUserDetailsPutRequestBodyModel)
    }


    private fun observeProgress() {
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
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
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })

    }


    private fun observerUserFillDetailsApiResponse() {
        viewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_topicFillFragment_to_mainActivity)
            activity?.finish()
        })
    }

    private fun toggleButtonState(textView: TextView) {
        textView.isSelected = !textView.isSelected
        if (textView.isSelected) {
            selectedTopics.add(textView.text.toString())
        } else {
            selectedTopics.remove(textView.text.toString())
        }
        setButtonState(textView, textView.isSelected)
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
            textView.setBackgroundResource(R.drawable.background_button_color_change_2) // Revert to normal background
            textView.setTextColor(
                Color.parseColor("#747688")
            ) // Revert to original text color
        }
    }


}