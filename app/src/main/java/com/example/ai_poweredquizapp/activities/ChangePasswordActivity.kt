package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    // TAG for logs in logcat
    private val TAG = "CHANGE_PASSWORD_TAG"

    // Firebase Auth for auth related tasks
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get instance of firebase auth for Auth related tasks
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var currentPassword = ""
    private var newPassword = ""
    private var confirmNewPassword = ""

    private fun validateData(){
        Log.d(TAG, "validateData: validating data..")

        currentPassword = binding.currentPasswordEt.text.toString()
        newPassword = binding.newPasswordEt.text.toString()
        confirmNewPassword = binding.confirmNewPasswordEt.text.toString()

        Log.d(TAG, "validateData: currentPassword: $currentPassword")
        Log.d(TAG, "validateData: newPassword: $newPassword")
        Log.d(TAG, "validateData: confirmNewPassword: $confirmNewPassword")

        if (currentPassword.isEmpty()) {
            binding.currentPasswordEt.error = "Enter current password"
            binding.currentPasswordEt.requestFocus()
        } else if (newPassword.isEmpty()) {
            binding.newPasswordEt.error = "Enter new password"
            binding.newPasswordEt.requestFocus()
        } else if (confirmNewPassword.isEmpty()) {
            binding.confirmNewPasswordEt.error = "Enter confirm new password"
            binding.confirmNewPasswordEt.requestFocus()
        } else if (newPassword != confirmNewPassword) {
            binding.confirmNewPasswordEt.error = "Password doesn't match"
            binding.confirmNewPasswordEt.requestFocus()
        } else {
            authenticateUserForUpdatePassword()
        }
    }

    private fun authenticateUserForUpdatePassword() {
        Log.d(TAG, "authenticateUserForUpdatePassword: Authenticating User...")

        // show progress
        progressDialog.setMessage("Authenticating User...")
        progressDialog.show()

        // before changing password re-authenticate the user to check if the user has entered correct current password
        val authCredential = EmailAuthProvider.getCredential(firebaseUser!!.email!!, currentPassword)
        firebaseUser!!.reauthenticate(authCredential)
            .addOnSuccessListener {
                // successfully authenticated, begin update
                Log.d(TAG, "authenticateUserForUpdatePassword: Authentication success")
                updatePassword()
            }
            .addOnFailureListener { e ->
                // failed to authenticate user, maybe wrong current password entered
                Log.e(TAG, "authenticateUserForUpdatePassword: ", e)
                progressDialog.dismiss()
                MyUtils.toast(context = this, message = "Failed to authenticate due to ${e.message}")
            }
    }

    private fun updatePassword() {
        Log.d(TAG, "updatePassword: Updating Password...")

        // show progress
        progressDialog.setMessage("Updating Password...")
        progressDialog.show()

        // begin update password, pass the new password as parameter
        firebaseUser!!.updatePassword(newPassword)
            .addOnSuccessListener {
                // password update success, you may do logout and move to login activity if you want
                Log.d(TAG, "updatePassword: Password updated...")
                progressDialog.dismiss()
                MyUtils.toast(context = this, message = "Password updated...")
            }
            .addOnFailureListener { e ->
                // password update failure, show error message
                Log.e(TAG, "updatePassword: ", e)
                progressDialog.dismiss()
                MyUtils.toast(context = this, message = "Failed to update password due to ${e.message}")
            }
    }

}