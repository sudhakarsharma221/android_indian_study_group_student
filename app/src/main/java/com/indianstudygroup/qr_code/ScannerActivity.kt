package com.indianstudygroup.qr_code

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.indianstudygroup.app_utils.HideStatusBarUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityScannerBinding
import com.indianstudygroup.databinding.ScannerBottomDialogBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var scannedData: String

    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                initListener()
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    showRationaleDialog()
                } else {
                    val message =
                        "You've denied camera permission twice. To enable it, open app settings."
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)
        HideStatusBarUtil.hideStatusBar(this)

        if (checkPermission()) {
            initListener()
        } else {
            requestForPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun initListener() {
        startQRCodeScanner()
        observeProgress()
        observerAllLibraryApiResponse()
        observerErrorMessageApiResponse()
    }

    private fun startQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setBeepEnabled(true)
        integrator.setCameraId(0)
        integrator.setPrompt("Scan QR Code")
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            scannedData = result.contents
//            Toast.makeText(this, scannedData, Toast.LENGTH_SHORT).show()
            Log.d("SCANNERDATA", scannedData)
            callAllLibraryDetailsApi()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
//            Toast.makeText(this, "Error scanning the QR", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED, Intent().apply {
                putExtra("NoSession", false)
            })
            finish()
        }
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


    private fun callAllLibraryDetailsApi(
    ) {
        libraryDetailsViewModel.callGetAllLibrary()
    }

    private fun observeProgress() {
        libraryDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        libraryDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }


    private fun observerAllLibraryApiResponse() {
        libraryDetailsViewModel.allLibraryResponse.observe(this, Observer {
            var libraryPresent = false
            var library: LibraryResponseItem? = null
            for (library1 in it) {
                if (library1.id.equals(scannedData)) {
                    library = library1
                    libraryPresent = true
                    break
                } else {
                    libraryPresent = false
                }
            }

            if (libraryPresent) {
                setResult(RESULT_OK, Intent().apply {
                    putExtra("libraryDataPhoto", library?.photo)
                    putExtra("libraryDataName", library?.name)
                    putExtra("libraryDataAddress", library?.address?.street)
                })
                Log.d("SCANNERDATA", "OK")
                finish()
            } else {
                setResult(RESULT_CANCELED, Intent().apply {
                    putExtra("NoSession", true)
                })
                finish()
            }
        })
    }
}