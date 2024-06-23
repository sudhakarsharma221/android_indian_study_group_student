package com.indianstudygroup.notification.ui

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityNotificationBinding
import com.indianstudygroup.notification.model.NotificationStatusChangeRequestModel
import com.indianstudygroup.notification.ui.adapter.NotificationAdapter
import com.indianstudygroup.notification.viewModel.NotificationViewModel
import com.indianstudygroup.userDetailsApi.model.Notifications
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationList: ArrayList<Notifications>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestForPermissionNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                ToastUtil.makeToast(this, "Notification Permission Granted")
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showRationaleDialogNotification()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.statusBarColor = Color.WHITE
        setResult(RESULT_OK)
        if (!checkPermissionNotification()) {
            requestForPermissionNotification.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
        initListener()
        observeUserDetails()
        observeProgress()
        observerErrorMessageApiResponse()
        observerNotificationResponse()
    }

    private fun initListener() {

//        binding.apply {
//            markAsRead.setOnClickListener {
//                for (notification in notificationList) {
//                    if (notification.status == "unread") notificationViewModel.callPostChangeNotificationStatus(
//                        auth.currentUser!!.uid,
//                        NotificationStatusChangeRequestModel(notification.id)
//                    )
//                }
//
//            }
//            markAsReadIcon.setOnClickListener {
//                for (notification in notificationList) {
//                    if (notification.status == "unread") notificationViewModel.callPostChangeNotificationStatus(
//                        auth.currentUser!!.uid,
//                        NotificationStatusChangeRequestModel(notification.id)
//                    )
//                }
//            }
//        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        notificationList = arrayListOf()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showRationaleDialogNotification() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Notification Permission")
            .setMessage("This app requires notification permission to keep you updated. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermissionNotification.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionNotification(): Boolean {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun observeUserDetails() {
        userDetailsViewModel.userDetailsResponse.observe(this, Observer {
            notificationList = it?.notifications!!

            if (notificationList.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.noNotiAvailable.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.noNotiAvailable.visibility = View.GONE
                adapter = NotificationAdapter(this, notificationList.asReversed()) { id ->
                    notificationViewModel.callPostChangeNotificationStatus(
                        auth.currentUser!!.uid, NotificationStatusChangeRequestModel(id)
                    )
                }
                binding.recyclerView.adapter = adapter
            }
        })
    }

    private fun observerNotificationResponse() {
        notificationViewModel.notificationChangeResponse.observe(this, Observer {
            notificationList = it.updatedUser?.notifications!!

            adapter = NotificationAdapter(this, notificationList.asReversed()) { id ->
                notificationViewModel.callPostChangeNotificationStatus(
                    auth.currentUser!!.uid, NotificationStatusChangeRequestModel(id)
                )
            }
            binding.recyclerView.adapter = adapter
        })
    }

    private fun observeProgress() {

        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.recyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        notificationViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }
}