package com.indianstudygroup.editProfile

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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityEditProfileBinding
import com.indianstudygroup.pincode.PincodeViewModel
import com.indianstudygroup.userDetailsApi.model.Address
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private val EDIT_PROFILE_REQUEST_CODE = 100

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var auth: FirebaseAuth
    private var photoUrl: String? = ""
    private var storageRef = Firebase.storage
    private var uri: Uri? = null
    private lateinit var imageUri: Uri
    private val contract =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.tvDone.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(imageUri)
                uri = imageUri
            } else {
                binding.tvDone.visibility = View.GONE
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
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var selectedQualificationFromList: String
    private lateinit var pinCodeViewModel: PincodeViewModel
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var district: String
    lateinit var state: String
    private var selectedTopics: ArrayList<String> = arrayListOf()

    private val qualificationList =
        arrayOf("High School", "Intermediate", "Graduation", "Post Graduation")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        pinCodeViewModel = ViewModelProvider(this)[PincodeViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()
        window.statusBarColor = Color.parseColor("#5669FF")

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.tvDone.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(uri)
                this.uri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        imageUri = createImageUri()!!
        focusChangeListeners()

        observerUserFillDetailsApiResponse()
        observerPincodeApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()


        val receivedIntent = intent

        if (receivedIntent.hasExtra("userData")) {
            val userDetails: UserDetailsResponseModel? =
                receivedIntent.getParcelableExtra("userData")
            userDetails?.let {
                userData = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "User details not found")
            finish()
        }
    }

    private fun initListener() {
        topicsSelectListener()

        binding.nameEt.setText(userData.name)
        binding.usernameEt.text = userData.username
        binding.pincodeEt.setText(userData.address?.pincode)
        binding.tvCity.text = userData.address?.district
        binding.tvState.text = userData.address?.state
        binding.aboutET.setText(userData.bio)
        binding.tvQualification.text = userData.highestQualification
        photoUrl = userData.photo
        Glide.with(this).load(userData.photo).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(binding.ivProfile)

        binding.ivProfile.setOnClickListener {
            chooseImage()
        }
//        binding.ivEditProfileCircle.setOnClickListener {
//            chooseImage()
//        }

        binding.tvQualification.setOnClickListener {
            chooseQualificationsDialog()
        }
        binding.tvDone.setOnClickListener {
            uploadImage()
        }

        binding.editPincode.setOnClickListener {
            binding.pincodeEt.isEnabled = true
            binding.pincodeEt.requestFocus()
            binding.pincodeEt.text?.clear()

            binding.pincodeEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
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
        }

        binding.continueButton.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val pincode = binding.pincodeEt.text.toString()
            val state = binding.tvState.text.toString()
            val district = binding.tvCity.text.toString()
            val bio = binding.aboutET.text.toString()
            val highestQualification = binding.tvQualification.text.toString()

            if (name.trim().isEmpty()) {
                binding.nameEt.error = "Empty Field"
            } else if (name.length < 2) {
                binding.nameEt.error = "Enter minimum 2 characters"
            } else if (name.length > 30) {
                binding.nameEt.error = "Enter less than 30 characters"
            } else if (pincode.trim().isEmpty()) {
                binding.pincodeEt.error = "Empty Field"
            } else if (pincode.length < 6) {
                binding.pincodeEt.error = "Enter Valid Pincode"
            } else if (highestQualification == "Choose Your Qualification") {
                binding.tvchoosequalification.visibility = android.view.View.VISIBLE
            } else {
                val uniqueSelectedTopics = selectedTopics.toSet().toList()
//                Log.d("SELECTEDTOPICSEDIT", uniqueSelectedTopics.toString())
                callPutUserDetailsApi(
                    auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
                        name.trim(), photoUrl ?: "", Address(
                            state.trim(), district, pincode
                        ), bio ?: "", highestQualification ?: "", uniqueSelectedTopics
                    )
                )
            }
        }
    }

    private fun topicsSelectListener() {
        selectedTopics = userData.topic

        for (topic in userData.topic) {
            when (topic) {
                "JEE Mains" -> setButtonState(binding.buttonJee, true)
                "NEET" -> setButtonState(binding.buttonNeet, true)
                "Defence" -> setButtonState(binding.buttonDefence, true)
                "NDA" -> setButtonState(binding.buttonNda, true)
                "CDS" -> setButtonState(binding.buttonCds, true)
                "SSC CHSL" -> setButtonState(binding.buttonChsl, true)
                "SSC CGL" -> setButtonState(binding.buttoncgl, true)
                "Police" -> setButtonState(binding.buttonPolice, true)
            }
        }


        val buttonIds = listOf(
            R.id.buttonCds,
            R.id.buttonChsl,
            R.id.buttonJee,
            R.id.buttonNda,
            R.id.buttonNeet,
            R.id.buttonDefence,
            R.id.buttonPolice,
            R.id.buttoncgl
        )

        buttonIds.forEach { buttonId ->
            val button = findViewById<TextView>(buttonId)
            button.setOnClickListener {
                toggleButtonState(button)
            }
        }

    }

    private fun toggleButtonState(textView: TextView) {
        textView.isSelected = !textView.isSelected
        val topic = textView.text.toString()
        if (textView.isSelected) {
            if (!selectedTopics.contains(topic)) {
                selectedTopics.add(topic)
            }
        } else {
            selectedTopics.remove(topic)
        }
        setButtonState(textView, textView.isSelected)
    }


    private fun setButtonState(textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.background_button) // Change to selected background
            textView.setTextColor(
                ContextCompat.getColor(
                    this, android.R.color.white
                )
            ) // Change to white text color
        } else {
            textView.setBackgroundResource(R.drawable.background_button_color_change_2) // Revert to normal background
            textView.setTextColor(
                Color.parseColor("#120D26")
            ) // Revert to original text color
        }
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
                    binding.tvchoosequalification.visibility = View.GONE

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
        val imageRef = storageRef.reference.child("images")
            .child("${System.currentTimeMillis()} ${auth.currentUser!!.uid}")
        imageRef.putFile(uri!!).addOnSuccessListener { task ->
            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                photoUrl = uri.toString()
                binding.tvDone.visibility = View.GONE
                progressDialog.dismiss()

            }?.addOnFailureListener {
                Toast.makeText(
                    this, "Error: ${it.message}", Toast.LENGTH_SHORT
                ).show()
                binding.tvDone.visibility = View.GONE
                progressDialog.dismiss()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this, "Error: ${it.message}", Toast.LENGTH_SHORT
            ).show()
            binding.tvDone.visibility = View.GONE
            progressDialog.dismiss()
        }
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
            }
        }

        binding.pincodeEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.pincodeEt.text.toString().trim()
                        .isNotEmpty() && binding.pincodeEt.text.toString().length < 6
                ) {
                    binding.pincodeEt.error = "Enter valid pincode"
                }
            }
        }
    }

    private fun callPincodeApi(pincode: String?) {
        pinCodeViewModel.callPinCodeDetails(pincode)
    }

    private fun callPutUserDetailsApi(
        userId: String?, putUserDetailsPutRequestBodyModel: UserDetailsPutRequestBodyModel
    ) {
        userDetailsViewModel.callPutUserDetails(userId, putUserDetailsPutRequestBodyModel)
    }


    private fun observeProgress() {
        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })

        pinCodeViewModel.showProgress.observe(this, Observer {
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
        userDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        pinCodeViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun observerPincodeApiResponse() {
        pinCodeViewModel.pincodeResponse.observe(this, Observer {
            Log.d("testPINCODEAPI", it.toString())
            if (it[0].postOffice == null) {
                ToastUtil.makeToast(this, "Please enter valid pincode")

            } else {
                state = it[0].postOffice?.get(0)?.state!!
                district = it[0].postOffice?.get(0)?.district!!
                binding.tvCity.text = district
                binding.tvState.text = state
            }

        })
    }

    private fun observerUserFillDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(this, Observer {
            userDetailsViewModel.setUserDetailsResponse(it)

            ToastUtil.makeToast(this, "Details Updated")
            setResult(RESULT_OK)
            finish()
            ApiCallsConstant.apiCallsOnceHome = false
        })
    }
}



