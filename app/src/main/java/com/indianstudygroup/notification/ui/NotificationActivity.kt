package com.indianstudygroup.notification.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.indianstudygroup.R
import com.indianstudygroup.databinding.ActivityNotificationBinding
import com.indianstudygroup.notification.model.NotificationResponseModel
import com.indianstudygroup.notification.ui.adapter.NotificationAdapter

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var notificationList: ArrayList<NotificationResponseModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        notificationList = arrayListOf()
        notificationList.add(
            NotificationResponseModel(
                "", "Test Notification Heading 1", "Test Notification SubHeading 1", "", true
            )
        )
        notificationList.add(
            NotificationResponseModel(
                "", "Test Notification Heading 2", "Test Notification SubHeading 2", "", false
            )
        )

        adapter = NotificationAdapter(this, notificationList)
        binding.recyclerView.adapter = adapter


        binding.backButton.setOnClickListener {
            finish()
        }
    }
}