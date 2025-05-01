package com.example.ai_poweredquizapp.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityLoginPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class LoginPhoneActivity : AppCompatActivity() {

    // ViewBinding for activity_login_phone.xml
    private lateinit var binding: ActivityLoginPhoneBinding

    // Log tag for debugging
    private val TAG = "LOGIN_PHONE_TAG"

    // Progress dialog to show while sending or verifying OTP
    private lateinit var progressDialog: ProgressDialog

    // Firebase authentication instance
    private lateinit var firebaseAuth: FirebaseAuth

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private lateinit var mCallBacks: OnVerificationStateChangedCallbacks

    private var mVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Show the phone input section
        binding.phoneInputRL.visibility = View.VISIBLE
        binding.otpInputRL.visibility = View.GONE

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        phoneLoginCallBack()

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.sendOtpBtn.setOnClickListener {
            validateData()
        }

        binding.resendOtpTv.setOnClickListener {

            if (forceResendingToken != null) {
                resendVerificationCode()
            }else{
                MyUtils.toast(this, "Can't resend code! Try again...")
            }
        }

        binding.verifyOtpBtn.setOnClickListener {
            // Input OTP
            val otp = binding.otpEt.text.toString().trim()

            // Validate if OTP is entered and length is 6 characters
            if (otp.isEmpty()) {
                // OTP is empty, show error
                binding.otpEt.error = "Enter Code"
                binding.otpEt.requestFocus()
            } else if (otp.length < 6) {
                // OTP length is less than 6, show error
                binding.otpEt.error = "Code length must be 6 characters"
                binding.otpEt.requestFocus()
            } else {
                // Data is valid, start verification
                verifyPhoneNumberWithCode(otp)
            }
        }

    }

    private var phoneCode = ""
    private var phoneNumber = ""
    private var phoneNumberWithCode = ""

    private fun validateData() {
        // Input data
        phoneCode = binding.phoneCodeEt.selectedCountryCodeWithPlus
        phoneNumber = binding.phoneNumberEt.text.toString().trim()
        phoneNumberWithCode = phoneCode + phoneNumber

        // Show input data in logcat
        Log.d(TAG, "validateData: Phone Code: $phoneCode")
        Log.d(TAG, "validateData: Phone Number: $phoneNumber")
        Log.d(TAG, "validateData: Phone Number With Code: $phoneNumberWithCode")

        // Validate data
        if (phoneNumber.isEmpty()) {
            // Phone Number is not entered, show error
            binding.phoneNumberEt.error = "Enter Phone Number"
            binding.phoneNumberEt.requestFocus()
        } else {
            // Phone number entered, start verification
            startPhoneNumberVerification()
        }
    }

    private fun startPhoneNumberVerification() {
        // Show progress dialog
        progressDialog.setMessage("Sending code to $phoneNumberWithCode")
        progressDialog.show()

        // Setup PhoneAuthOptions with phone number, timeout, callback, etc.
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumberWithCode)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)         // Timeout duration
            .setActivity(this)                         // Activity (for callback binding)
            .setCallbacks(mCallBacks)                  // OnVerificationStateChangedCallbacks
            .build()

        // Start phone verification with PhoneAuthOptions
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(){
        // Show progress dialog
        progressDialog.setMessage("Sending code to $phoneNumberWithCode")
        progressDialog.show()

        // Setup PhoneAuthOptions with phone number, timeout, callback, etc.
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumberWithCode)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)         // Timeout duration
            .setActivity(this)                         // Activity (for callback binding)
            .setCallbacks(mCallBacks)                  // OnVerificationStateChangedCallbacks
            .setForceResendingToken(forceResendingToken!!)
            .build()

        // Start phone verification with PhoneAuthOptions
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(otp: String) {
        Log.d(TAG, "verifyPhoneNumberWithCode: OTP: $otp")

        // Show progress dialog
        progressDialog.setMessage("Verifying OTP...")
        progressDialog.show()

        // Get PhoneAuthCredential using the verification ID and OTP
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun phoneLoginCallBack(){

        mCallBacks = object: OnVerificationStateChangedCallbacks(){

            @SuppressLint("SetTextI18n")
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent: ")

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                forceResendingToken = token

                // code is sent, so hide the progress dialog
                progressDialog.dismiss()

                // OTP is sent, so hide phone input UI and show OTP input UI
                binding.phoneInputRL.visibility = View.GONE
                binding.otpInputRL.visibility = View.VISIBLE

                // Show a toast message
                MyUtils.toast(this@LoginPhoneActivity, "Code sent to $phoneNumberWithCode")

                // Update the label text to inform user
                binding.LoginPhoneLabelTv.text = "Enter SMS code: $phoneNumberWithCode"
            }

            override fun onVerificationCompleted(phoneAuthCredencial: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredencial)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "onVerificationFailed: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this@LoginPhoneActivity, "Failed to verify due to ${e.message}")
            }

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: ")
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        // Sign in to Firebase Auth using Phone Credentials
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d(TAG, "onSuccess: signInWithPhoneAuthCredential: ")

                // Sign-in Success, check if user is new or existing
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.d(TAG, "onSuccess: signInWithPhoneAuthCredential: Account created...")
                    // New user - save user info to Firebase Realtime Database
                    updateUserInfo()
                } else {
                    Log.d(TAG, "onSuccess: signInWithPhoneAuthCredential: Logged In")
                    // Existing user - go to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                // Sign-in failed, show error
                Log.e(TAG, "onFailure: signInWithPhoneAuthCredential: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Login failed due to ${e.message}")
            }
    }

    private fun updateUserInfo() {
        Log.d(TAG, "updateUserInfo: ")

        // Show progress
        progressDialog.setMessage("Saving User Info...")
        progressDialog.show()

        val timestamp = MyUtils.timestamp()
        val registeredUserUid = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["uid"] = registeredUserUid!!
        hashMap["email"] = ""
        hashMap["name"] = ""
        hashMap["timestamp"] = timestamp
        hashMap["phoneCode"] = phoneCode
        hashMap["phoneNumber"] = phoneNumber
        hashMap["profileImageUrl"] = ""
        hashMap["dob"] = ""
        hashMap["userType"] = MyUtils.USER_TYPE_PHONE
        hashMap["token"] = ""

        // Set data to Firebase Realtime Database
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(registeredUserUid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                // User info save success
                Log.d(TAG, "onSuccess: updateUserInfo: User info saved...")
                progressDialog.dismiss()

                // Start MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                // User info save failed
                Log.e(TAG, "onFailure: updateUserInfo: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Failed to save due to ${e.message}")
            }

    }

}