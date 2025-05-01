package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    // View Binding
    private lateinit var binding: ActivityForgotPasswordBinding

    // TAG for logs in logcat
    private val TAG = "FORGOT_PASSWORD_TAG"

    // ProgressDialog to show while sending password recovery instructions
    private lateinit var progressDialog: ProgressDialog

    // Firebase Auth for auth related tasks
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait.. Processing...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.resetPasswordBtn.setOnClickListener {
            validateData()
        }
    }

    private var email = ""

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        Log.d(TAG, "validateData: Email: $email")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Invalid email pattern, show error in emailEt
            binding.emailEt.error = "Invalid Email Pattern!"
            binding.emailEt.requestFocus()
        } else {
            // Valid email pattern, start sending recovery email instructions
            sendPasswordRecoveryInstructions()
        }
    }

    private fun sendPasswordRecoveryInstructions() {
        Log.d(TAG, "Sending reset instructions...")

        // Show progress dialog
        progressDialog.setMessage("Sending reset link to $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Log.d(TAG, "Reset link sent to $email")
                progressDialog.dismiss()
                MyUtils.toast(context = this, message = "Reset link sent to $email")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Reset failed: ${e.message}", e)
                progressDialog.dismiss()
                MyUtils.toast(context = this, message = "Failed: ${e.message}")
            }
    }
}