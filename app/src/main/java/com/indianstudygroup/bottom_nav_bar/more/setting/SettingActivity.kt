package com.indianstudygroup.bottom_nav_bar.more.setting

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel
import com.indianstudygroup.bottom_nav_bar.more.setting.screens.AboutUsActivity
import com.indianstudygroup.bottom_nav_bar.more.setting.screens.PaymentTermsActivity
import com.indianstudygroup.bottom_nav_bar.more.setting.screens.PrivacyPolicyActivity
import com.indianstudygroup.bottom_nav_bar.more.setting.screens.TermsOfServiceActivity
import com.indianstudygroup.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()

    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.aboutUs.setOnClickListener {
            IntentUtil.startIntent(this, AboutUsActivity())
        }
        binding.privacyPolicy.setOnClickListener {
            IntentUtil.startIntent(this, PrivacyPolicyActivity())
        }
        binding.termsOfService.setOnClickListener {
            IntentUtil.startIntent(this, TermsOfServiceActivity())
        }
        binding.paymentTerms.setOnClickListener {
            IntentUtil.startIntent(this, PaymentTermsActivity())
        }

    }

}