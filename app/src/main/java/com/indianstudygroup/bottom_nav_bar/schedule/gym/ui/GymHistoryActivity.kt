package com.indianstudygroup.bottom_nav_bar.schedule.gym.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.gym.ui.adapter.GymScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.gym.viewModel.GymScheduleViewModel
import com.indianstudygroup.bottom_nav_bar.schedule.library.ui.adapter.LibraryScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.library.viewModel.LibraryScheduleViewModel
import com.indianstudygroup.databinding.ActivityGymHistoryBinding

class GymHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGymHistoryBinding
    private lateinit var viewModel: GymScheduleViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: GymScheduleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGymHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        viewModel = ViewModelProvider(this)[GymScheduleViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        viewModel.callGymScheduleHistoryDetails(auth.currentUser!!.uid)
        initListener()
        observeProgress()
        observerErrorMessageApiResponse()
        observerSessionHistoryApiResponse()
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.swiperefresh.setOnRefreshListener {
            viewModel.callGymScheduleHistoryDetails(auth.currentUser!!.uid)
        }
        binding.backButton.setOnClickListener {
            finish()

        }
    }

    private fun observerSessionHistoryApiResponse() {
        viewModel.sessionsGymHistoryDetailsResponse.observe(this, Observer {
            if (it.isEmpty()) {
                binding.noSchedule.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter = GymScheduleAdapter(this, it)
                binding.recyclerView.adapter = adapter
            }
            binding.swiperefresh.isRefreshing = false
        })
    }

    private fun observeProgress() {

        viewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })

    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

}