package com.indianstudygroup.fillDetails

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indianstudygroup.MainActivity
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityFillUserDetailsBinding
import com.indianstudygroup.pincode.PincodeViewModel
import com.indianstudygroup.userDetailsApi.model.Address
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import java.io.File

class FillUserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFillUserDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.statusBarColor = Color.parseColor("#2f3133")
        window.statusBarColor = Color.WHITE

    }
}

