package com.indianstudygroup.bottom_nav_bar.more.setting.screens

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel
import com.indianstudygroup.databinding.ActivityPaymentTermsBinding

class PaymentTermsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentTermsBinding
    private lateinit var viewModel: PolicyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentTermsBinding.inflate(layoutInflater)
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
        val text = HtmlCompat.fromHtml(
//            "<b>${data?.data?.tnc?.get(1)?.key}</b><br/>" +
                    "<b>Booking :</b><br/>${data?.data?.tnc?.get(1)?.value?.booking}<br/><br/>" +
                    "<b>Payment :</b><br/>${data?.data?.tnc?.get(1)?.value?.payment}<br/><br/>" +
                    "<b>Cancellation and Refunds :</b><br/>${data?.data?.tnc?.get(1)?.value?.cancellationAndRefunds}<br/>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.tvText.text = text
    }
}