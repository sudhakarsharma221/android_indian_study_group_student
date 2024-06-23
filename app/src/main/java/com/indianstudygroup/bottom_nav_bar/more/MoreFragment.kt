package com.indianstudygroup.bottom_nav_bar.more

import android.app.Dialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.more.help_desk.HelpDeskActivity
import com.indianstudygroup.bottom_nav_bar.more.setting.SettingActivity
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel
import com.indianstudygroup.bottom_nav_bar.more.wallet.WalletActivity
import com.indianstudygroup.databinding.FragmentMoreBinding
import com.indianstudygroup.registerScreen.SignInActivity

class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: PolicyViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PolicyViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE

        if (!ApiCallsConstant.apiCallsOnceMore) {
            callPolicyDetails()
            ApiCallsConstant.apiCallsOnceMore = true
        }



        initListener()
//        inflater.inflate(R.layout.fragment_profile, container, false)
        observeProgress()
        observerPolicyApiResponse()
        observerErrorMessageApiResponse()

        return binding.root
    }

    private fun initListener() {


        binding.tvWallet.setOnClickListener {
            ToastUtil.makeToast(requireContext(), "Coming Soon...")
//            IntentUtil.startIntent(requireContext(), WalletActivity())
        }
        binding.tvSetting.setOnClickListener {
            IntentUtil.startIntent(requireContext(), SettingActivity())
        }
        binding.tvHelpDesk.setOnClickListener {
            IntentUtil.startIntent(requireContext(), HelpDeskActivity())
        }

        binding.tvSignOut.setOnClickListener {
            signOutDialog()
        }
    }

    private fun signOutDialog() {
        val builder = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.logout_dialog, null)
        builder.setContentView(view)
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
        builder.setCancelable(true)
        val logout = view.findViewById<MaterialCardView>(R.id.logout)
        val cancel = view.findViewById<TextView>(R.id.cancelButton)
        cancel.setOnClickListener {
            builder.dismiss()
        }
        logout.setOnClickListener {
            auth.signOut()
            ToastUtil.makeToast(requireContext(), "Successful Sign Out")
            IntentUtil.startIntent(requireContext(), SignInActivity())
            ApiCallsConstant.apiCallsOnceHome = false
            ApiCallsConstant.apiCallsOnceHomeGym = false
            requireActivity().finish()
        }
    }

    private fun callPolicyDetails(
    ) {
        viewModel.callPolicyDetails()
    }

    private fun observerPolicyApiResponse() {
        viewModel.policyDetailsResponse.observe(viewLifecycleOwner, Observer {
            viewModel.setPolicyDetailsResponse(it)
        })
    }

    private fun observeProgress() {
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }


}