package com.indianstudygroup.bottom_nav_bar.more

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.more.help_desk.HelpDeskActivity
import com.indianstudygroup.bottom_nav_bar.more.wallet.WalletActivity
import com.indianstudygroup.databinding.FragmentMoreBinding
import com.indianstudygroup.registerScreen.SignInActivity

class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: MoreViewModel


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
        binding.tvWallet.setOnClickListener {
            IntentUtil.startIntent(requireContext(), WalletActivity())
        }
//        binding.tvSeting.setOnClickListener {
//            IntentUtil.startIntent(requireContext(), WalletActivity())
//        }
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
            requireActivity().finish()
        }
    }


}