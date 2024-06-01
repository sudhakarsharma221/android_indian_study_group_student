package com.indianstudygroup.registerScreen

import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.indianstudygroup.MainActivity
import com.indianstudygroup.app_utils.HideKeyboard
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityOtpBinding
import com.indianstudygroup.fillDetails.FillUserDetailsActivity
import com.indianstudygroup.userDetailsApi.model.AddFcmTokenRequestBody
import com.indianstudygroup.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var verificationInProgress = false

    private var countdownTimer: CountDownTimer? = null
    private val countdownDurationMillis: Long = 90 * 1000

    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var credential: PhoneAuthCredential
    private var fromSignUp: Boolean? = true
    private lateinit var phoneNo: String
    private lateinit var userName: String
    private lateinit var togoPhoneNo: String
    private lateinit var fcmToken: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        initListener()
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                fcmToken = it.result.toString()
            }
        }

        observerUserDetailsApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Wait while we are redirecting you for reCaptcha verification...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        phoneNo = "+91" + intent.getStringExtra("phoneNumber")
        userName = intent.getStringExtra("userName").toString()
        togoPhoneNo = intent.getStringExtra("phoneNumber").toString()
        fromSignUp = intent.getBooleanExtra("fromSignUp", false)

        val shortPhoneNo = phoneNo.substring(0, 7)

        binding.phoneNo.text =
            "We will send you an One Time Password on \n $shortPhoneNo****** mobile number"
        val options =
            PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNo) // Phone number to verify
                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        binding.progressBar.visibility = View.GONE
                        progressDialog.dismiss()

                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        ToastUtil.makeToast(this@OtpActivity, "Failed: ${p0.localizedMessage}")
                        Log.d("OTPACTIVITYERROR", "Failed: Please try again")
                        binding.progressBar.visibility = View.GONE
                        IntentUtil.startIntent(this@OtpActivity, SignInActivity())
                        progressDialog.dismiss()

                    }

                    override fun onCodeSent(
                        verification: String, token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        super.onCodeSent(verification, token)
                        binding.phoneNo.text =
                            "We’ve send you the verification code on \n $shortPhoneNo******"
                        progressDialog.dismiss()
                        startCountdownTimer()

                        verificationId = verification
                        resendToken = token
                    }

                }) // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        auth.setLanguageCode("en")

        binding.resendButton.setOnClickListener {

            progressDialog.show()

            binding.resendButton.visibility = View.GONE
            if (!verificationInProgress) { // Check if verification is not already in progress
                // Resend verification code
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNo) // Phone number to verify
                    .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                            binding.progressBar.visibility = View.GONE
                            verificationInProgress = false
                            cancelCountdownTimer()
                            progressDialog.dismiss()

                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            ToastUtil.makeToast(this@OtpActivity, "Failed: ${p0.localizedMessage}")
                            Log.d("OTPACTIVITYERROR", "Failed: ${p0.localizedMessage}")
                            binding.progressBar.visibility = View.GONE
                            verificationInProgress = false
                            progressDialog.dismiss()

                        }

                        override fun onCodeSent(
                            verification: String, token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            super.onCodeSent(verification, token)
                            verificationId = verification
                            resendToken = token
                            ToastUtil.makeToast(this@OtpActivity, "Verification code resent")
                            startCountdownTimer()
                            progressDialog.dismiss()

                        }

                    }) // OnVerificationStateChangedCallbacks
                    .setForceResendingToken(resendToken).build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        binding.continueButton.setOnClickListener {
            HideKeyboard.hideKeyboard(this, binding.otpEt.windowToken)
            val otp = binding.otpEt.text.toString()
            if (otp.trim().isEmpty()) {
                binding.otpEt.error = "Empty Field"
            } else if (otp.length < 6) {
                binding.otpEt.error = "Enter 6 characters"

            } else {
                binding.progressBar.visibility = View.VISIBLE
                val credential =
                    PhoneAuthProvider.getCredential(verificationId!!, binding.otpEt.text.toString())
                signInWithPhoneAuthCredential(credential)

            }
        }


    }

    private fun callGetUserDetailsApi(userId: String?) {
        viewModel.callGetUserDetails(userId)
    }

    private fun callPostUserDetailsApi(postUserDetailsPostRequestBodyModel: UserDetailsPostRequestBodyModel) {
        viewModel.callPostUserDetails(postUserDetailsPostRequestBodyModel)
    }

    private fun observerUserDetailsApiResponse() {
        viewModel.userDetailsResponse.observe(this, Observer {

            if (!it.devices.contains(fcmToken)) {
                viewModel.callPostFcmToken(
                    auth.currentUser!!.uid, AddFcmTokenRequestBody(
                        fcmToken, "library"
                    )
                )
            }

            if (fromSignUp == true) {
                IntentUtil.startIntent(this@OtpActivity, FillUserDetailsActivity())
                cancelCountdownTimer()
                finish()

                ToastUtil.makeToast(this, "Successful Sign Up")
            } else {
                if (it.name?.trim().isNullOrEmpty() || it.address?.pincode.isNullOrEmpty()) {
                    ToastUtil.makeToast(this, "Successful Log In")
                    IntentUtil.startIntent(this@OtpActivity, FillUserDetailsActivity())
                    cancelCountdownTimer()
                    finish()
                } else {
                    ToastUtil.makeToast(this, "Successful Log In")
                    IntentUtil.startIntent(this@OtpActivity, MainActivity())
                    cancelCountdownTimer()
                    finish()
                }
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

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (fromSignUp == true) {
                    callPostUserDetailsApi(
                        UserDetailsPostRequestBodyModel(
                            auth.currentUser!!.uid, togoPhoneNo, "student", "student", userName
                        )
                    )

                } else {
                    callGetUserDetailsApi(auth.currentUser!!.uid)
                }
                // Sign in success, update UI with the signed-in user's information
            } else {
                // Sign in failed, display a message and update the UI
                binding.progressBar.visibility = View.GONE

                ToastUtil.makeToast(this, "Failed: ${task.exception?.localizedMessage}")

                // Update UI
            }
        }
    }

    // Function to start the countdown timer
    private fun startCountdownTimer() {
        binding.tvResendTimer.visibility = View.VISIBLE
        countdownTimer = object : CountDownTimer(countdownDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update UI with remaining time, e.g., update a TextView
                val secondsRemaining = millisUntilFinished / 1000
                // Example: update a TextView with remaining seconds
                binding.tvResendTimer.text = "Re-send code in \n $secondsRemaining seconds"
            }

            override fun onFinish() {
                // Countdown timer finished, enable the resend button
                binding.resendButton.visibility = View.VISIBLE
                // Example: update a TextView when timer finishes
                binding.tvResendTimer.text = "Resend available"
            }
        }.start()
    }

    // Function to cancel the countdown timer if needed
    private fun cancelCountdownTimer() {
        countdownTimer?.cancel()
    }

}