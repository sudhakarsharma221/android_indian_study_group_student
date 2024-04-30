package com.indianstudygroup.bottom_nav_bar.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.more.MoreViewModel
import com.indianstudygroup.databinding.FragmentProfileBinding
import com.indianstudygroup.editProfile.EditProfileActivity
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class ProfileFragment : Fragment() {
    private lateinit var userData: UserDetailsResponseModel
    private val EDIT_PROFILE_REQUEST_CODE = 100
    private lateinit var auth: FirebaseAuth

    private lateinit var userDetailsViewModel: UserDetailsViewModel

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        if (!ApiCallsConstant.apiCallsOnceProfile) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceProfile = true
        }
        intiListener()
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
//         inflater.inflate(R.layout.fragment_chat, container, false)
        return binding.root
    }

    private fun intiListener() {
        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("userData", userData)
            startActivity(intent)
        }
    }


    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it
            binding.tvTopics.text = userData.topic.joinToString("\n")
            binding.nameEt.setText(userData.name)
            binding.usernameEt.setText(userData.username)
            binding.pincodeEt.setText(userData.address?.pincode)
            binding.tvCity.text = userData.address?.district
            binding.tvState.text = userData.address?.state
            binding.aboutET.setText(userData.bio)
            binding.tvQualification.text = userData.highestQualification
            Glide.with(this).load(userData.photo).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.ivProfile)
        })
    }

    private fun observeProgress() {
        userDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
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
        userDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Refresh the user details here
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
        }
    }

}