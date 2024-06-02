package com.indianstudygroup.qr_code.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.HideStatusBarUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityScannerBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.qr_code.model.MarkAttendanceRequestModel
import com.indianstudygroup.qr_code.viewModel.MarkAttendanceViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var markAttendanceViewModel: MarkAttendanceViewModel
    private lateinit var scannedData: String
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var codeScanner: CodeScanner
    private lateinit var auth: FirebaseAuth
    private lateinit var time: String
    private var library: LibraryResponseItem? = null


    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                initListener()
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    showRationaleDialog()
                } else {
                    binding.openSettings.visibility = View.VISIBLE
                    binding.textView.visibility = View.VISIBLE
                    binding.scannerView.visibility = View.GONE
                    openSettings()

                }
            }
        }

    private fun openSettings() {
        binding.openSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        libraryViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        markAttendanceViewModel = ViewModelProvider(this)[MarkAttendanceViewModel::class.java]
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        HideStatusBarUtil.hideStatusBar(this)

        if (checkPermission()) {
            binding.openSettings.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.scannerView.visibility = View.VISIBLE
            initListener()
        } else {
            requestForPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun initListener() {

        binding.backButton.setOnClickListener {
            finish()
        }

        newCodeScanner()


//        startQRCodeScanner()
        observerUserDetailsApiResponse()
        observeProgress()
        observeLibraryIdApiResponse()
        observerMarkAttendanceApiResponse()
        observerErrorMessageApiResponse()
    }


    private fun newCodeScanner() {
        codeScanner = CodeScanner(this, binding.scannerView)
        binding.scannerView.isMaskVisible = true
        binding.scannerView.maskColor = Color.TRANSPARENT

        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE)// list of type BarcodeFormat,
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.isTouchFocusEnabled = true
        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                scannedData = it.text
                callUserDetailsApi()
                binding.cardView.visibility = View.GONE
                binding.backButton.visibility = View.GONE
                binding.textView2.visibility = View.GONE
                binding.textView3.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Error: ${it.localizedMessage}", Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
        codeScanner.startPreview()

    }


    override fun onResume() {
        super.onResume()
        if (this::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (this::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Camera Permission")
            .setMessage("This app requires camera permission. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermission.launch(android.Manifest.permission.CAMERA)
            }
        builder.create().show()
    }

    private fun checkPermission(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun callUserDetailsApi(
    ) {
        userData = userDetailsViewModel.getUserDetailsResponse()!!
        var libraryPresent = false
//        val libraryList = ArrayList<String>()
//        libraryList.clear()
        for (lib in userData.sessions) {
            if (lib.libraryId == scannedData) {

                val timeStartHours = lib.startTime?.substring(9)?.toInt()?.div(60)
                val timeStartMinutes = lib.startTime?.substring(9)?.toInt()?.rem(60)

                val timeEndHours = lib.endTime?.substring(9)?.toInt()?.div(60)
                val timeEndMinutes = lib.endTime?.substring(9)?.toInt()?.rem(60)

                val timeStartFormatted = formatTime(timeStartHours, timeStartMinutes)
                val timeEndFormatted = formatTime(timeEndHours, timeEndMinutes)
                time = "$timeStartFormatted to $timeEndFormatted"
                libraryPresent = true
                break
            } else {
                libraryPresent = false
            }
        }
//        for (library in libraryList) {
//            if (library == scannedData) {
//                libraryPresent = true
//                break
//            } else {
//                libraryPresent = false
//            }
//        }

        if (libraryPresent) {
            callLibraryDetailsApi(scannedData)
        } else {
            setResult(RESULT_CANCELED, Intent().apply {
                putExtra("NoSession", true)
            })
            finish()
        }
    }

    private fun callMarkAttendancesApi(
        userId: String?, markAttendanceRequestModel: MarkAttendanceRequestModel
    ) {
        markAttendanceViewModel.callMarkAttendance(userId, markAttendanceRequestModel)
    }

    private fun callLibraryDetailsApi(
        libId: String?
    ) {
        libraryViewModel.callIdLibrary(libId)
    }


    private fun observeProgress() {

        markAttendanceViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.scannerView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.scannerView.visibility = View.VISIBLE
            }
        })
        libraryViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.scannerView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.scannerView.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        markAttendanceViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        libraryViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun observeLibraryIdApiResponse() {
        libraryViewModel.idLibraryResponse.observe(this, Observer {
            library = it.libData
            callMarkAttendancesApi(
                auth.currentUser!!.uid, MarkAttendanceRequestModel(
                    scannedData, auth.currentUser!!.uid
                )
            )
        })
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(this, Observer {
            userDetailsViewModel.setUserDetailsResponse(it)
        })
    }

    private fun observerMarkAttendanceApiResponse() {
        markAttendanceViewModel.markAttendanceResponse.observe(this, Observer {
            if (it.message == "success") {
                userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
                setResult(RESULT_OK, Intent().apply {

                    putExtra(
                        "libraryDataPhoto", if (library?.photo?.isNotEmpty() == true) {
                            library!!.photo?.get(0)
                        } else {
                            ""
                        }
                    )
                    putExtra("libraryDataTime", time)
                    putExtra("libraryDataName", library?.name)
                    putExtra("libraryDataAddress", library?.address?.street)
                })
                finish()
            } else {
                setResult(RESULT_CANCELED, Intent().apply {
                    putExtra("NoSession", false)
                })
                finish()
            }
        })
    }


    private fun formatTime(hours: Int?, minutes: Int?): String {
        val hourFormatted = if (hours == 0 || hours == 21) 12 else hours?.rem(12)
        val amPm = if (hours!! < 12) "am" else "pm"
        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
    }

}