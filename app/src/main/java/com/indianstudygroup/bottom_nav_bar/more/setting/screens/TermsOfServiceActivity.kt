package com.indianstudygroup.bottom_nav_bar.more.setting.screens

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel
import com.indianstudygroup.databinding.ActivityTermsOfServiceBinding
import java.lang.StringBuilder

class TermsOfServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsOfServiceBinding
    private lateinit var viewModel: PolicyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsOfServiceBinding.inflate(layoutInflater)
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

    private fun getPolicyDetails() {
        val data = viewModel.getPolicyDetailsResponse()

        val text = "<b>${data?.data?.tnc?.get(0)?.key}</b><br/>" +
                "<b>Eligibility :</b><br/>${data?.data?.tnc?.get(0)?.value?.eligibility}<br/><br/>" +
                "<b>Account Registration :</b><br/>${data?.data?.tnc?.get(0)?.value?.accountRegistration}<br/><br/>" +
                "<b>Use Restrictions :</b><br/>${data?.data?.tnc?.get(0)?.value?.useRestrictions}<br/><br/>"

        val text2 = "<b>${data?.data?.tnc?.get(2)?.key}</b><br/>" +
                "<b>Submission of Content :</b><br/>${data?.data?.tnc?.get(2)?.value?.submissionOfContent}<br/><br/>" +
                "<b>Content Guidelines :</b><br/>${data?.data?.tnc?.get(2)?.value?.contentGuidelines}<br/><br/>"

        val text3 = "<b>${data?.data?.tnc?.get(3)?.key}</b><br/>" +
                "<b>Ownership :</b><br/>${data?.data?.tnc?.get(3)?.value?.ownership}<br/><br/>"

        val text4 = "<b>${data?.data?.tnc?.get(4)?.key}</b><br/>" +
                "<b>No Warranties :</b><br/>${data?.data?.tnc?.get(4)?.value?.noWarranties}<br/><br/>"

        binding.tvText.text = HtmlCompat.fromHtml("$text$text2$text3$text4", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}