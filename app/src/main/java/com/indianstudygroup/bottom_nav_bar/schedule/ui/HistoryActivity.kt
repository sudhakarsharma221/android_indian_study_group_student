package com.indianstudygroup.bottom_nav_bar.schedule.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.ui.adapter.ScheduleAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.viewModel.ScheduleViewModel
import com.indianstudygroup.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ScheduleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        viewModel.callScheduleHistoryDetails(auth.currentUser!!.uid)
        initListener()
        observeProgress()
        observerErrorMessageApiResponse()
        observerSessionHistoryApiResponse()
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.swiperefresh.setOnRefreshListener {
            viewModel.callScheduleDetails(auth.currentUser!!.uid)
        }
        binding.backButton.setOnClickListener {
            finish()

        }
    }

    private fun observerSessionHistoryApiResponse() {
        viewModel.sessionsHistoryDetailsResponse.observe(this, Observer {
            if (it.isEmpty()) {
                binding.noSchedule.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noSchedule.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                Log.d("SCHEDULERESPONSE", it.toString())
                adapter = ScheduleAdapter(this, it)
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