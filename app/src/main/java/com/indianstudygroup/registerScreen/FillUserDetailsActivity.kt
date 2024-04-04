package com.indianstudygroup.registerScreen

import android.app.ProgressDialog
import android.content.pm.PackageManager
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
    private lateinit var auth: FirebaseAuth

    private var uri: Uri? = null
    lateinit var imageUri: Uri
    private val contract =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
//                binding.tvDone.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(imageUri)
                uri = imageUri
            } else {
//                binding.tvDone.visibility = View.GONE
                binding.ivProfile.setImageURI(uri)
                uri = null
            }
        }

    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                contract.launch(imageUri)
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
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var selectedQualificationFromList: String
    lateinit var pincodeViewModel: PincodeViewModel
    lateinit var viewModel: UserDetailsViewModel
    lateinit var district: String
    lateinit var state: String
    private var selectedTopics: ArrayList<String>? = arrayListOf()

    private val qualificationList =
        arrayOf("High School", "Intermediate", "Graduation", "Post Graduation")
    private val topicsList =
        arrayOf("JEE Mains", "NEET", "Defence", "NDA", "CDS", "SSC CHSL", "SSC CGL", "Police")
    private val checkedTopics =
        BooleanArray(topicsList.size) // Initialize with the same length as topicsList
    val checkedQualification = BooleanArray(qualificationList.size)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        pincodeViewModel = ViewModelProvider(this)[PincodeViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
//                binding.tvDone.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(uri)
                this.uri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        imageUri = createImageUri()!!
        initListener()
        focusChangeListeners()

        observerUserFillDetailsApiResponse()
        observerPincodeApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
    }

    private fun initListener() {
        binding.ivProfile.setOnClickListener {
            chooseImage()
        }
        binding.ivEditProfileCircle.setOnClickListener {
            chooseImage()
        }

        binding.tvQualification.setOnClickListener {
            chooseQualificationsDialog()
        }

        binding.tvTopics.setOnClickListener {
            chooseTopicsDialog()
        }


        binding.pincodeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used, but needs to be implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used, but needs to be implemented
            }

            override fun afterTextChanged(s: Editable?) {
                val pincode = s.toString().trim()
                if (pincode.length == 6) {
                    callPincodeApi(pincode)
                }
            }
        })



        binding.continueButton.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val address = binding.addressEt.text.toString()
            val pincode = binding.pincodeEt.text.toString()
            val state = binding.tvState.text.toString()
            val district = binding.tvCity.text.toString()
            val bio = binding.aboutET.text.toString()
            val highestQualification = binding.tvQualification.text.toString()
            if (highestQualification == "Choose Your Qualification") {
                binding.tvQualification.error = "Select Qualification"
            } else if (name.trim().isEmpty()) {
                binding.nameEt.error = "Empty Field"
            } else if (name.length < 2) {
                binding.nameEt.error = "Enter minimum 2 characters"
            } else if (name.length > 30) {
                binding.nameEt.error = "Enter less than 30 characters"
            } else if (address.trim().isEmpty()) {
                binding.addressEt.error = "Empty Field"
            } else if (address.length < 3) {
                binding.addressEt.error = "Enter minimum 3 characters"
            } else if (address.length > 100) {
                binding.addressEt.error = "Enter less than 100 characters"
            } else if (pincode.trim().isEmpty()) {
                binding.pincodeEt.error = "Empty Field"
            } else if (pincode.length < 6) {
                binding.pincodeEt.error = "Enter Valid Pincode"
            } else {
                callPutUserDetailsApi(
                    auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
                        name,
                        uri?.path ?: "",
                        Address(state, district, pincode, address),
                        bio ?: "",
                        highestQualification ?: "",
                        selectedTopics ?: arrayListOf()
                    )
                )
            }
        }
    }

    private fun chooseTopicsDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Items").setMultiChoiceItems(
            topicsList, checkedTopics
        ) { dialog, which, isChecked ->
            checkedTopics[which] = isChecked
        }.setPositiveButton("OK") { dialog, which ->
            // Check if none of the options is selected
            if (checkedTopics.all { !it }) {
                Toast.makeText(
                    applicationContext, "Please select at least one item", Toast.LENGTH_SHORT
                ).show()
            } else {
                // User clicked OK and at least one item is selected
                for (i in checkedTopics.indices) {
                    if (checkedTopics[i]) {
                        selectedTopics?.add(topicsList[i])
                    }
                }
            }
        }.setNegativeButton("Cancel") { dialog, which ->
            // User cancelled the dialog
            dialog.dismiss()
        }.setNeutralButton("Clear All") { dialog, which ->
            // Clear all selections
            checkedTopics.fill(false)
        }
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun chooseQualificationsDialog() {

        val builder = AlertDialog.Builder(this)
        var selectedItem = -1 // Variable to store the index of the selected item

        builder.setTitle("Select Item")
            .setSingleChoiceItems(qualificationList, selectedItem) { _, which ->
                selectedItem = which // Update the selected item index
            }.setPositiveButton("OK") { dialog, which ->
                if (selectedItem == -1) {
                    Toast.makeText(
                        applicationContext, "Please select an item", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Retrieve the selected item
                    val selectedQualification = qualificationList[selectedItem]
                    // Display the selected item
                    selectedQualificationFromList = selectedQualification
                    binding.tvQualification.text = selectedQualification
                }
            }.setNegativeButton("Cancel") { dialog, which ->
                // User cancelled the dialog
                dialog.dismiss()
            }

// Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()

    }


    private fun uploadImage() {
        if (uri == null) {
            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Wait while uploading profile")
        progressDialog.show()


    }


    private fun chooseImage() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.media_choose_bottom_dialog, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val gallery = view.findViewById<TextView>(R.id.chooseGallery)
        gallery.setOnClickListener {
            dialog.dismiss()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        val camera = view.findViewById<TextView>(R.id.chooseCamera)
        camera.setOnClickListener {
            dialog.dismiss()
            if (checkPermission()) {
                contract.launch(imageUri)
            } else {
                requestForPermission.launch(android.Manifest.permission.CAMERA)
            }

        }
    }

    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Camera Permission")
            .setMessage("This app requires camera permission to take profile photos. If you deny this time you have to manually go to app setting to allow permission.")
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

    private fun createImageUri(): Uri? {
        val image = File(this.filesDir, "profile_photos.png")
        return FileProvider.getUriForFile(
            this, "com.indianstudygroup.fileProvider", image
        )
    }


    private fun focusChangeListeners() {


        binding.addressEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.addressEt.text.toString().trim()
                        .isNotEmpty() && binding.addressEt.text.toString().length < 3
                ) {
                    binding.addressEt.error = "Enter Minimum 3 Characters"
                } else if (binding.addressEt.text.toString().trim()
                        .isNotEmpty() && binding.addressEt.text.toString().length > 100
                ) {
                    binding.addressEt.error = "Enter Less Than 100 Characters"

                }
                //                else if (binding.nameEt.text.toString().trim().isNotEmpty() && !binding.nameEt.text!!.matches(namePattern.toRegex())
//                ) {
//                    binding.nameEt.error = "Enter Valid Name (Only Alphabets No Space)"
//                }
            }
        }




        binding.nameEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length < 2
                ) {
                    binding.nameEt.error = "Enter Minimum 2 Characters"
                } else if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length > 30
                ) {
                    binding.nameEt.error = "Enter Less Than 30 Characters"

                }
                //                else if (binding.nameEt.text.toString().trim().isNotEmpty() && !binding.nameEt.text!!.matches(namePattern.toRegex())
//                ) {
//                    binding.nameEt.error = "Enter Valid Name (Only Alphabets No Space)"
//                }
            }
        }

        binding.pincodeEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length < 6
                ) {
                    binding.nameEt.error = "Enter valid pincode"
                }
                //                else if (binding.nameEt.text.toString().trim().isNotEmpty() && !binding.nameEt.text!!.matches(namePattern.toRegex())
//                ) {
//                    binding.nameEt.error = "Enter Valid Name (Only Alphabets No Space)"
//                }
            }
        }
    }

    private fun callPincodeApi(pincode: String?) {
        pincodeViewModel.callPinCodeDetails(pincode)
    }

    private fun callPutUserDetailsApi(
        userId: String?, putUserDetailsPutRequestBodyModel: UserDetailsPutRequestBodyModel
    ) {
        viewModel.callPutUserDetails(userId, putUserDetailsPutRequestBodyModel)
    }


    private fun observeProgress() {
        viewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })

        pincodeViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        pincodeViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun observerPincodeApiResponse() {
        pincodeViewModel.pincodeResponse.observe(this, Observer {

            if (it.postOffice == null) {
                ToastUtil.makeToast(this, "Please enter valid pincode")

            } else {
                state = it.postOffice[0].state!!
                district = it.postOffice[0].district!!
            }

        })
    }

    private fun observerUserFillDetailsApiResponse() {
        viewModel.userDetailsResponse.observe(this, Observer {
            IntentUtil.startIntent(this, MainActivity())
            finish()
        })
    }
}

