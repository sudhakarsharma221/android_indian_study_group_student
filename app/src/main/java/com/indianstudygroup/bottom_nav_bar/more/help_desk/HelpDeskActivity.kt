package com.indianstudygroup.bottom_nav_bar.more.help_desk

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indianstudygroup.R
import com.indianstudygroup.databinding.ActivityHelpDeskBinding

class HelpDeskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpDeskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpDeskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        initListener()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}