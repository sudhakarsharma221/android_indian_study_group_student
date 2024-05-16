package com.indianstudygroup.bottom_nav_bar.more.help_desk

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.indianstudygroup.R
import com.indianstudygroup.databinding.ActivityHelpDeskBinding
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.model.Faqs
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel

class HelpDeskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpDeskBinding
    private lateinit var adapter: FaqsAdapter
    private lateinit var viewModel: PolicyViewModel

    private var faqsList: ArrayList<Faqs> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpDeskBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PolicyViewModel::class.java]

        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        initListener()
        getPolicyDetails()
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getPolicyDetails(
    ) {
        val data = viewModel.getPolicyDetailsResponse()
        faqsList = data?.data?.faq!!
        adapter = FaqsAdapter(this, faqsList)
        binding.recyclerView.adapter = adapter

    }
}