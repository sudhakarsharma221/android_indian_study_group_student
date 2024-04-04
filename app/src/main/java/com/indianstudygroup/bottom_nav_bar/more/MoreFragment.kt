package com.indianstudygroup.bottom_nav_bar.more

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.FragmentMoreBinding
import com.indianstudygroup.registerScreen.SignInActivity

class MoreFragment : Fragment() {

    private lateinit var viewModel: MoreViewModel
    private lateinit var binding: FragmentMoreBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MoreViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        initListener()
//        inflater.inflate(R.layout.fragment_profile, container, false)
        return binding.root
    }

    private fun initListener() {
        binding.tvSignOut.setOnClickListener {
            auth.signOut()
            ToastUtil.makeToast(requireContext(), "Successful Sign Out")
            IntentUtil.startIntent(requireContext(), SignInActivity())
            requireActivity().finish()
        }
    }
}