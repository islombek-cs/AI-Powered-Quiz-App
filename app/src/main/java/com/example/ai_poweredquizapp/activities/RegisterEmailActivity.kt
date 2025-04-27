package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityRegisterEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterEmailActivity : AppCompatActivity() {

    // ViewBinding for the Activity
    private lateinit var binding: ActivityRegisterEmailBinding

    // Tag for Debugging (Logcat)
    private val TAG = "REGISTER_EMAIL_TAG"

    // Progress Dialog to show loading
    private lateinit var progressDialog: ProgressDialog

    // Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Init/setup ProgressDialog to show while sign-in
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Processing up...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.RegisterBtn.setOnClickListener {
            validateData()
        }

        binding.haveAccountTv.setOnClickListener {
            startActivity(Intent(this, LoginEmailActivity::class.java))
        }
    }

    private var email = ""
    private var password = ""
    private var cPassword = ""

    private fun validateData() {
        // Input Data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()
        cPassword = binding.cPasswordEt.text.toString()

        Log.d(TAG, "validateData: Email: $email")
        Log.d(TAG, "validateData: Password: $password")
        Log.d(TAG, "validateData: Confirm Password: $cPassword")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid Email Pattern"
            binding.emailEt.requestFocus()
        }else if (password.isEmpty()) {
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        }else if (password != cPassword) {
            binding.cPasswordEt.error = "Password doesn't match"
            binding.cPasswordEt.requestFocus()
        }else {
            registerUser()
        }
    }

    private fun registerUser() {
        // Show progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // User registered successfully
                Log.d(TAG, "registerUser: Registered successfully...")
                updateUserInfo()
                progressDialog.dismiss()
            }
            .addOnFailureListener { e ->
                // User registration failed
                Log.e(TAG, "registerUser: ", e)
                MyUtils.toast(this, "Registration Failed due to ${e.message}")
                progressDialog.dismiss()
            }
    }

    private fun updateUserInfo(){
        progressDialog.setMessage("Saving User Info...")

        val timestamp = MyUtils.timestamp()
        val registeredUserEmail = firebaseAuth.currentUser!!.email
        val registeredUserUid = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["uid"] = "$registeredUserUid"
        hashMap["email"] = "$registeredUserEmail"
        hashMap["name"] = ""
        hashMap["timestamp"] = timestamp
        hashMap["phoneCode"] = ""
        hashMap["phoneNumber"] = ""
        hashMap["dob"] = ""
        hashMap["userType"] = MyUtils.USER_TYPE_EMAIL
        hashMap["token"] = ""

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("$registeredUserUid")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "updateUserInfo: User info saved...")
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "updateUserInfo: ", e)
                MyUtils.toast(this, "Failed to save user info due to ${e.message}")
                progressDialog.dismiss()
            }
    }
}