package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {

    // ViewBinding for the Activity
    private lateinit var binding: ActivityLoginEmailBinding

    // Tag for Debugging (Logcat)
    private val TAG = "LOGIN_EMAIL_TAG"

    // Progress Dialog to show loading
    private lateinit var progressDialog: ProgressDialog

    // Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set window insets (already there)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Init/setup ProgressDialog to show while sign-in
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbarBackBtn.setOnClickListener {
            val intent = Intent(this, LoginOptionsActivity::class.java)
            startActivity(intent)
        }

        binding.LoginBtn.setOnClickListener {
            validateData()
        }

        binding.goToRegisterTv.setOnClickListener {
            startActivity(Intent(this, RegisterEmailActivity::class.java))
        }

        binding.forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }


    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()

        Log.d(TAG, "validateData: Email: $email")
        Log.d(TAG, "validateData: Password: $password")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid Email"
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()) {
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        } else {
            // Email pattern is valid and password is entered
            loginUser()
        }
    }

    private fun loginUser() {
        // Show progress
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        // Start user login
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // User login success
                Log.d(TAG, "loginUser: Logged In...")
                progressDialog.dismiss()

                // Move to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity() // Close all previous activities
            }
            .addOnFailureListener { e ->
                // User login failed
                Log.e(TAG, "loginUser: ", e)
                progressDialog.dismiss()

                // Show error toast
                MyUtils.toast(this, "Login Failed! Check Your Credentials")
            }
    }

}
