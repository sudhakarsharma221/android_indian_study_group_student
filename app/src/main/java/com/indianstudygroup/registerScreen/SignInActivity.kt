package com.indianstudygroup.registerScreen

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.MainActivity
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.HideKeyboard
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivitySignInBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNo: String
    private lateinit var viewModel: UserDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            IntentUtil.startIntent(this@SignInActivity, MainActivity())
            finish()
        }
        initListener()

        observerUserExistsApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()

    }


    private fun initListener() {
        binding.tvSignUp.setOnClickListener {
            IntentUtil.startIntent(this, SignUpActivity())
        }


        binding.continueButton.setOnClickListener {
            HideKeyboard.hideKeyboard(this, binding.phoneEt.windowToken)
            phoneNo = binding.phoneEt.text.toString()
            if (phoneNo.trim().isEmpty()) {
                binding.phoneEt.error = "Empty Field"
            } else if (phoneNo.length < 10) {
                binding.phoneEt.error = "Enter Valid Mobile Number"
            } else {
                callGetUserExistApi(phoneNo)
            }
        }

    }


    private fun callGetUserExistApi(contact: String?) {
        viewModel.callUserExists(contact, "")
    }

    private fun observerUserExistsApiResponse() {
        viewModel.userExistResponse.observe(this, Observer {
            if (it.contactExist == true) {
                if (it.authType == "student") {
                    val intent = Intent(this, OtpActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNo)
                    startActivity(intent)
                    finish()
                } else {
                    showErrorBottomDialog("You have an account on library owner profile. PLease login on that application.")
                }
            } else {
                ToastUtil.makeToast(this, "User does not exist, please sign up")
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun showErrorBottomDialog(message: String) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.messageTv.text = message
        dialogBinding.continueButton.setOnClickListener {
            HideKeyboard.hideKeyboard(this, binding.phoneEt.windowToken)
            bottomDialog.dismiss()
        }
    }


    private fun observeProgress() {
        viewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}