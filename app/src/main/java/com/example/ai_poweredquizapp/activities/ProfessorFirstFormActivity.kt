package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorFirstFormBinding

class ProfessorFirstFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorFirstFormBinding

    private val TAG = "PROFESSOR_FIRST_FORM_TAG"

    private lateinit var mContext: Context

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorFirstFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.continueFirstBtn.setOnClickListener {
            val intent = Intent(this, ProfessorFileUploadActivity::class.java)
            startActivity(intent)
        }

    }
}