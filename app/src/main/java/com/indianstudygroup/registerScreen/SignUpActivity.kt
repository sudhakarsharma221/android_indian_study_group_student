package com.indianstudygroup.registerScreen

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.HideKeyboard
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivitySignUpBinding
import com.indianstudygroup.databinding.ConfirmBottomDialogPhoneNoBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var phoneNo: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        window.statusBarColor = Color.parseColor("#2f3133")
        focusChangeListeners()

        initListener()

        observerUserExistsApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
    }

    private fun initListener() {
        binding.tvLogin.setOnClickListener {
            IntentUtil.startIntent(this, SignInActivity())
        }

        binding.continueButton.setOnClickListener {
            HideKeyboard.hideKeyboard(this, binding.phoneEt.windowToken)
            phoneNo = binding.phoneEt.text.toString()
            userName = binding.usernameEt.text.toString()

            if (phoneNo.trim().isEmpty()) {
                binding.phoneEt.error = "Empty Field"
            } else if (phoneNo.length < 10) {
                binding.phoneEt.error = "Please Enter Valid Mobile Number"
            } else if (userName.trim().isEmpty()) {
                binding.usernameEt.error = "Empty Field"
            } else if (userName.contains(" ")) { // Check for spaces in the username
                binding.usernameEt.error = "Username should not contain spaces"
            } else if (userName.length < 2) {
                binding.usernameEt.error = "Enter Minimum 2 Characters"
            } else if (userName.length > 50) {
                binding.usernameEt.error = "Enter Less Than 50 Characters"
            } else {
                showBottomDialog(phoneNo, userName)
            }
        }
    }

    private fun showBottomDialog(phoneNo: String, userName: String?) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ConfirmBottomDialogPhoneNoBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.phoneNo.text = "An otp will be sent to +91$phoneNo"
        dialogBinding.continueButton.setOnClickListener {
            HideKeyboard.hideKeyboard(this, binding.phoneEt.windowToken)
            callGetUserExistApi(phoneNo, userName)
            bottomDialog.dismiss()
        }
        dialogBinding.changeButton.setOnClickListener {
            bottomDialog.dismiss()
        }
    }


    private fun callGetUserExistApi(contact: String?, userName: String?) {
        viewModel.callUserExists(contact, userName)
    }

    private fun observerUserExistsApiResponse() {
        viewModel.userExistResponse.observe(this, Observer {

            if (it.userNameExist == true) {
                showErrorBottomDialog("This Username already exists. Try Something different.")
            } else if (it.contactExist == true) {
                if (it.authType == "student") {
                    ToastUtil.makeToast(this, "User already exist, please sign in")
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showErrorBottomDialog("You already have an account on library owner app. Please sign in on that application.")
                }
            } else {
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("phoneNumber", phoneNo)
                intent.putExtra("userName", userName)
                intent.putExtra("fromSignUp", true)
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

    private fun focusChangeListeners() {

        binding.usernameEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.usernameEt.text.toString().trim()
                        .isNotEmpty() && binding.usernameEt.text.toString().length < 2
                ) {
                    binding.usernameEt.error = "Enter Minimum 2 Characters"
                } else if (binding.usernameEt.text.toString().trim()
                        .isNotEmpty() && binding.usernameEt.text.toString().length > 50
                ) {
                    binding.usernameEt.error = "Enter Less Than 50 Characters"
                }
            }
        }


        binding.phoneEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.phoneEt.text.toString().trim()
                        .isNotEmpty() && binding.phoneEt.text.toString().length < 10
                ) {
                    binding.phoneEt.error = "Enter Valid Mobile No"
                }
            }
        }
    }
}