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
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val namePattern = "^[a-zA-Z]+$"
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var phoneNo: String

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

            if (phoneNo.trim().isEmpty()) {
                binding.phoneEt.error = "Empty Field"
            } else if (phoneNo.length < 10) {
                binding.phoneEt.error = "Please Enter Valid Mobile Number"
            } else {
                showBottomDialog(phoneNo)
            }
        }
    }

    private fun showBottomDialog(phoneNo: String) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ConfirmBottomDialogPhoneNoBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()

        dialogBinding.phoneNo.text = "Are you sure your mobile no is $phoneNo"
        dialogBinding.continueButton.setOnClickListener {
            HideKeyboard.hideKeyboard(this, binding.phoneEt.windowToken)
            callGetUserExistApi(phoneNo)
            bottomDialog.dismiss()
        }
        dialogBinding.changeButton.setOnClickListener {
            bottomDialog.dismiss()
        }
    }


    private fun callGetUserExistApi(contact: String?) {
        viewModel.callUserExists(contact)
    }

    private fun observerUserExistsApiResponse() {
        viewModel.userExistResponse.observe(this, Observer {
            if (it.userExist == false) {
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("phoneNumber", phoneNo)
                intent.putExtra("fromSignUp", true)
                startActivity(intent)
                finish()
            } else {
                ToastUtil.makeToast(this, "User already exist, please sign in")
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
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