package com.indianstudygroup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityMainBinding
import com.indianstudygroup.qr_code.ScannerActivity
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
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        if (auth.currentUser == null) {
            IntentUtil.startIntent(this, SignInActivity())
            finish()
        }

        binding.fabScanner.setOnClickListener {
            IntentUtil.startIntent(this, ScannerActivity())
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
        navView.setupWithNavController(navController)

    }


}