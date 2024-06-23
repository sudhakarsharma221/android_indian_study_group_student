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
import com.indianstudygroup.app_utils.HideStatusBarUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.gym.model.GymResponseItem
import com.indianstudygroup.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indianstudygroup.databinding.ActivityScannerBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.qr_code.model.GymMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.LibraryMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.viewModel.MarkAttendanceViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var gymViewModel: GymViewModel
    private lateinit var markAttendanceViewModel: MarkAttendanceViewModel
    private lateinit var scannedData: String
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var codeScanner: CodeScanner
    private lateinit var auth: FirebaseAuth
    private lateinit var time: String
    private var library: LibraryResponseItem? = null
    private var gym: GymResponseItem? = null


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
        gymViewModel = ViewModelProvider(this)[GymViewModel::class.java]
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
        observeGymIdApiResponse()
        observerMarkAttendanceApiResponse()
        observerMarkAttendanceGymApiResponse()
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
        var gymPresent = false
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

        for (gym in userData.gymSessions) {
            if (gym.gymId == scannedData) {

                val timeStartHours = gym.startTime?.substring(9)?.toInt()?.div(60)
                val timeStartMinutes = gym.startTime?.substring(9)?.toInt()?.rem(60)

                val timeEndHours = gym.endTime?.substring(9)?.toInt()?.div(60)
                val timeEndMinutes = gym.endTime?.substring(9)?.toInt()?.rem(60)

                val timeStartFormatted = formatTime(timeStartHours, timeStartMinutes)
                val timeEndFormatted = formatTime(timeEndHours, timeEndMinutes)
                time = "$timeStartFormatted to $timeEndFormatted"
                gymPresent = true
                break
            } else {
                gymPresent = false
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
        } else if (gymPresent) {
            callGymDetailsApi(scannedData)
        } else {
            setResult(RESULT_CANCELED, Intent().apply {
                putExtra("NoSession", true)
            })
            finish()
        }
    }

    private fun callMarkAttendancesApi(
        userId: String?, markAttendanceRequestModel: LibraryMarkAttendanceRequestModel
    ) {
        markAttendanceViewModel.callLibraryMarkAttendance(userId, markAttendanceRequestModel)
    }

    private fun callMarkAttendancesGymApi(
        userId: String?, markAttendanceRequestModel: GymMarkAttendanceRequestModel
    ) {
        markAttendanceViewModel.callGymMarkAttendance(userId, markAttendanceRequestModel)
    }

    private fun callLibraryDetailsApi(
        libId: String?
    ) {
        libraryViewModel.callIdLibrary(libId)
    }

    private fun callGymDetailsApi(
        gymId: String?
    ) {
        gymViewModel.callIdGym(gymId)
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
        gymViewModel.showProgress.observe(this, Observer {
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
        gymViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun observeLibraryIdApiResponse() {
        libraryViewModel.idLibraryResponse.observe(this, Observer {
            library = it.libData
            callMarkAttendancesApi(
                auth.currentUser!!.uid, LibraryMarkAttendanceRequestModel(
                    scannedData, auth.currentUser!!.uid
                )
            )
        })
    }

    private fun observeGymIdApiResponse() {
        gymViewModel.idGymResponse.observe(this, Observer {
            gym = it.gymData
            callMarkAttendancesGymApi(
                auth.currentUser!!.uid, GymMarkAttendanceRequestModel(
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

    private fun observerMarkAttendanceGymApiResponse() {
        markAttendanceViewModel.gymMarkAttendanceResponse.observe(this, Observer {

            if (it.message == "success") {

                userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
                setResult(RESULT_OK, Intent().apply {

                    putExtra(
                        "DataPhoto", if (gym?.photo?.isNotEmpty() == true) {
                            gym!!.photo?.get(0)
                        } else {
                            ""
                        }
                    )
                    putExtra("DataTime", time)
                    putExtra("Review", gym?.reviews?.size.toString())
                    putExtra(
                        "Rating", ratingFunction(gym?.rating?.count, gym?.rating?.totalRatings)
                    )
                    putExtra("DataName", gym?.name)
                    putExtra("DataAddress", gym?.address?.street)
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


    private fun observerMarkAttendanceApiResponse() {
        markAttendanceViewModel.markAttendanceResponse.observe(this, Observer {
            if (it.message == "success") {
                userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
                setResult(RESULT_OK, Intent().apply {

                    putExtra(
                        "DataPhoto", if (library?.photo?.isNotEmpty() == true) {
                            library!!.photo?.get(0)
                        } else {
                            ""
                        }
                    )
                    putExtra("DataTime", time)
                    putExtra("Review", library?.reviews?.size.toString())
                    putExtra(
                        "Rating",
                        ratingFunction(library?.rating?.count, library?.rating?.totalRatings)
                    )
                    putExtra("DataName", library?.name)
                    putExtra("DataAddress", library?.address?.street)
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

    private fun ratingFunction(count: Int?, totalRatings: Int?): String {
        var rating = 1f
        if (count == 0) {
            return "1.0"
        } else {
            if (count == null) {
                return "1.0"
            } else {
                rating = (count.toFloat().let {
                    totalRatings?.toFloat()?.div(
                        it
                    )
                })?.toFloat()!!
            }
        }
        return String.format("%.1f", rating)
    }

    private fun formatTime(hours: Int?, minutes: Int?): String {
        if (hours == null || minutes == null) {
            throw IllegalArgumentException("Hours and minutes cannot be null")
        }

        val adjustedHours = when {
            hours == 24 -> 0
            hours == 0 -> 12
            hours > 12 -> hours - 12
            hours == 12 -> 12
            else -> hours
        }

        val amPm = if (hours < 12 || hours == 24) "AM" else "PM"

        return String.format("%02d:%02d %s", adjustedHours, minutes, amPm)
    }

}