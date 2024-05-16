package com.indianstudygroup.bottom_nav_bar.more.setting.screens

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel
import com.indianstudygroup.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding
    private lateinit var viewModel: PolicyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PolicyViewModel::class.java]

        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
        getPolicyDetails()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getPolicyDetails(
    ) {
        val data = viewModel.getPolicyDetailsResponse()
        binding.tvText.text = data?.data?.aboutUs ?: "Error Getting The Data"

    }
}