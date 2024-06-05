package com.indianstudygroup

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.databinding.ActivityMainBinding
import com.indianstudygroup.databinding.ScannerBottomDialogBinding
import com.indianstudygroup.qr_code.ui.ScannerActivity
import com.indianstudygroup.registerScreen.SignInActivity
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: UserDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.statusBarColor = Color.parseColor("#2f3133")
        window.statusBarColor = Color.WHITE
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TOKENFIREBASE", it.result.toString())
            }
        }
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        if (auth.currentUser == null) {
            IntentUtil.startIntent(this, SignInActivity())
            finish()
        }

        binding.fabScanner.setOnClickListener {

            val intent = Intent(this, ScannerActivity::class.java)
            startActivityForResult(intent, 1)
        }


        val navView: BottomNavigationView = binding.navView
//
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_profile
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id == R.id.navigation_profile) {
//                window.statusBarColor = Color.parseColor("#5669FF")
//            } else {
//                window.statusBarColor = Color.WHITE
//            }
//        }
        navView.setupWithNavController(navController)
    }

    private fun showStartDialog(
        message: String,
        layoutShow: Boolean,
        rating: String,
        review: String,
        address: String,
        name: String,
        photo: String,
        time: String
    ) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ScannerBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.textView.text = message
        dialogBinding.tvReviews.text = "$review Reviews"
        dialogBinding.tvRating.text = rating
        dialogBinding.libraryName.text = name
        dialogBinding.timeSlots.text = "Time Slot : $time"
        dialogBinding.libraryAddress.text = address
        Glide.with(this).load(photo).placeholder(R.drawable.noimage).error(R.drawable.noimage)
            .into(dialogBinding.libraryPhoto)

        if (!layoutShow) {
            dialogBinding.layoutView.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val libraryDataPhoto = data?.getStringExtra("libraryDataPhoto")
                val libraryDataName = data?.getStringExtra("libraryDataName")
                val libraryDataAddress = data?.getStringExtra("libraryDataAddress")
                val libraryDataTime = data?.getStringExtra("libraryDataTime")
                val libraryRating = data?.getStringExtra("libraryRating")
                val libraryReview = data?.getStringExtra("libraryReview")
                showStartDialog(
                    "Your session has started",
                    true,
                    libraryRating ?: "",
                    libraryReview ?: "",
                    libraryDataAddress ?: "",
                    libraryDataName ?: "",
                    libraryDataPhoto ?: "",
                    libraryDataTime ?: ""
                )
            } else if (resultCode == RESULT_CANCELED) {
                val noSession = data?.getBooleanExtra("NoSession", false)
                if (noSession == true) {
                    showStartDialog("You don't have any session", false, "", "", "", "", "", "")
                } else {
                    showStartDialog(
                        "Error scanning the code. Contact the library owner",
                        false,
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ApiCallsConstant.apiCallsOnceHome = false
        ApiCallsConstant.apiCallsOnceAllLibrary = false
        ApiCallsConstant.apiCallsOnceLibrary = false
    }
}