package com.indianstudygroup.fillDetails

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indianstudygroup.databinding.ActivityFillUserDetailsBinding


class FillUserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFillUserDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.statusBarColor = Color.parseColor("#2f3133")
        window.statusBarColor = Color.WHITE

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}

