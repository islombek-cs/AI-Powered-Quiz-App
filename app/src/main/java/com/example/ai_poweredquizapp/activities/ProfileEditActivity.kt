package com.example.ai_poweredquizapp.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileEditActivity : AppCompatActivity() {
    // View Binding
    private lateinit var binding: ActivityProfileEditBinding

    private val TAG = "PROFILE_EDIT_TAG"

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var mUserType = ""

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        loadMyInfo()

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.profileImagePickFab.setOnClickListener{
            imagePickDialog()
        }

        binding.updateBtn.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private var dob = ""
    private var email = ""
    private var phoneCode = ""
    private var phoneNumber = ""

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        dob = binding.dobEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        phoneCode = binding.countryCodePicker.selectedCountryCodeWithPlus
        phoneNumber = binding.phoneEt.text.toString().trim()

        if (imageUri == null) {
            updateProfileDb(null)
        } else {
            uploadProfileImageStorage()
        }
    }

    private fun uploadProfileImageStorage() {
        Log.d(TAG, "uploadProfileImageStorage: ")

        //show progress
        progressDialog.setMessage("Updating user profile image....")
        progressDialog.show()

        val filePathAndName = "UserImages/${firebaseAuth.uid}"

        val ref = FirebaseStorage.getInstance().getReference().child(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnProgressListener { snapshot ->

                Log.d(TAG, "uploadProfileImageStorage: Uploading...")

                val progress = 100 * snapshot.bytesTransferred / snapshot.totalByteCount
                Log.d(TAG, "uploadProfileImageStorage: Progress: $progress")

                progressDialog.setMessage("Uploading profile image. Progress: ${progress.toInt()}%")
            }
            .addOnSuccessListener { taskSnapshot ->

                Log.d(TAG, "uploadProfileImageStorage: Uploaded")

                val uriTask = taskSnapshot.storage.downloadUrl

                while (!uriTask.isSuccessful);
                val uploadedImageUrl = uriTask.result.toString()

                if (uriTask.isSuccessful) {
                    updateProfileDb(uploadedImageUrl)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "uploadProfileImageStorage: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Failed to upload profile image due to ${e.message}"
                )
            }
    }

    private fun updateProfileDb(imageUrl: String?) {
        //show progress
        progressDialog.setMessage("Updating user info...")
        progressDialog.show()

        //setup data in hashmap to update to firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["name"] = name
        hashMap["dob"] = dob
        if (imageUrl != null) {
            //update profileImageUrl in db only if uploaded image url is not null
            hashMap["profileImageUrl"] = "$imageUrl"
        }

        //if user type is Phone then allow to update email otherwise (in case of Google or Email) allow to update Phone
        if (mUserType == MyUtils.USER_TYPE_EMAIL || mUserType == MyUtils.USER_TYPE_GOOGLE) {
            //User type is google/email, allow to update phone number not email
            hashMap["phoneCode"] = phoneCode
            hashMap["phoneNumber"] = phoneNumber
        } else {
            //User type is phone, allow to update email, not phone number
            hashMap["email"] = email
        }

        //Database reference of user to update info
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .updateChildren(hashMap)
            .addOnSuccessListener {
                //Updated successfully
                Log.d(TAG, "updateProfileDb: Profile Updated")
                progressDialog.dismiss()
                MyUtils.toast(this,  "Profile Updated...!")
            }
            .addOnFailureListener { e ->
                //Failed to update
                Log.e(TAG, "updateProfileDb: ", e)
                progressDialog.dismiss()
                MyUtils.toast(context = this, message = "Failed to update due to ${e.message}")
            }
    }


    private fun imagePickDialog() {
        val popupMenu = PopupMenu(this, binding.profileImagePickFab)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")

        popupMenu.show()
        popupMenu.setOnMenuItemClickListener{item ->
            val itemId = item.itemId

            if (itemId == 1){
                Log.d(TAG, "imagePickDialog: Camera clicked")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestCameraPermissions.launch(arrayOf(Manifest.permission.CAMERA))
                } else {
                    requestCameraPermissions.launch(arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }

            } else if (itemId == 2){
                Log.d(TAG, "imagePickDialog: Gallery clicked")
                pickImageGallery()
            }
            true
        }
    }

    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        Log.d(TAG, "requestCameraPermissions: result $result")

        var areAllGranted = true

        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }

        if (areAllGranted) {
            Log.d(TAG, "requestCameraPermissions: All permissions granted...!")
            pickImageCamera()
        } else {
            Log.d(TAG, "requestCameraPermissions: All or either one permission denied...!")
            MyUtils.toast(context = this, message = "Camera or Storage or both permissions denied...!")
        }
    }

    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "temp image")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "temp image description")

        imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Check if image is captured or not
        if (result.resultCode == Activity.RESULT_OK) {
            // Image Captured, we have image in imageUri as assigned in pickImageCamera()
            Log.d(TAG, "cameraActivityResultLauncher: Image Captured: $imageUri")

            try {
                Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.person_black_profile)
                    .into(binding.profileIv)
            } catch (e: Exception) {
                Log.e(TAG, "cameraActivityResultLauncher: ", e)
            }
        } else {
            Log.d(TAG, "cameraActivityResultLauncher: Cancelled")
            MyUtils.toast(this, "Cancelled...")
        }
    }

    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ")

        // Intent to launch Image Picker e.g. Gallery
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            imageUri = data!!.data
            Log.d(TAG, "galleryActivityResultLauncher: Image Picked $imageUri")

            try {
                Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.person_black_profile)
                    .into(binding.profileIv)
            } catch (e: Exception) {
                Log.e(TAG, "cameraActivityResultLauncher: ", e)
            }
        } else {
            Log.d(TAG, "galleryActivityResultLauncher: Cancelled")
            MyUtils.toast(this, "Cancelled...!")
        }
    }

    private fun loadMyInfo(){
        Log.d(TAG, "loadMyInfo: ")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val dob = "${snapshot.child("dob").value}"
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    mUserType = "${snapshot.child("userType").value}"

                    if (mUserType == MyUtils.USER_TYPE_EMAIL || mUserType == MyUtils.USER_TYPE_GOOGLE) {
                        binding.emailTil.isEnabled = false
                        binding.emailEt.isEnabled = false
                    } else {
                        binding.phoneNumberTil.isEnabled = false
                        binding.phoneEt.isEnabled = false
                        binding.countryCodePicker.isEnabled = false
                    }

                    // Set data to UI
                    binding.fullNameTv.text = name
                    binding.nameEt.setText(name)
                    binding.emailEt.setText(email)
                    binding.dobEt.setText(dob)
                    binding.phoneEt.setText(phoneNumber)

                    try {
                        val phoneCodeInt = phoneCode.replace("+", "").toInt()
                        binding.countryCodePicker.setCountryForPhoneCode(phoneCodeInt)
                    } catch (e: Exception) {
                        Log.e(TAG, "onDataChange: ", e)
                    }

                    try {
                        Glide.with(this@ProfileEditActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.person_black_profile)
                            .into(binding.profileIv)
                    } catch (e: Exception) {
                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

}