package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
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

class ProfileEditActivity : AppCompatActivity() {
    // View Binding
    private lateinit var binding: ActivityProfileEditBinding

    private val TAG = "PROFILE_EDIT_TAG"

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var mUserType = ""

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