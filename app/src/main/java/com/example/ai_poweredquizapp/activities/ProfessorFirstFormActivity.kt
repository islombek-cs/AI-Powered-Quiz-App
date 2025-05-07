package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorFirstFormBinding
import com.google.android.material.textfield.TextInputLayout
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener

class ProfessorFirstFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorFirstFormBinding
    private lateinit var mContext: Context
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorFirstFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button action
        binding.toolbarBackBtn.setOnClickListener { finish() }

        // Helper function to reset the error state
        fun resetField(field: TextInputLayout) {
            field.endIconDrawable = null
            val grayColor = ContextCompat.getColor(this, R.color.colorGray01)
            field.setBoxStrokeColorStateList(ColorStateList.valueOf(grayColor))
            field.refreshDrawableState()
        }

        binding.subjectIdEt.addTextChangedListener {
            val text = it?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                resetField(binding.subjectCodeTil)
            }
        }
        binding.subjectNameEt.addTextChangedListener {
            val text = it?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                resetField(binding.subjectNameTil)
            }
        }
        binding.quantityQuizEt.addTextChangedListener {
            val text = it?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                resetField(binding.quantityQuizTil)
            }
        }
        binding.numberStudentEt.addTextChangedListener {
            val text = it?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                resetField(binding.numberQuizTil)
            }
        }


        // Submit button click
        binding.continueFirstBtn.setOnClickListener {
            val moduleCode = binding.subjectIdEt.text.toString().trim()
            val moduleName = binding.subjectNameEt.text.toString().trim()
            val quizCount = binding.quantityQuizEt.text.toString().trim()
            val totalStudents = binding.numberStudentEt.text.toString().trim()

            var isValid = true

            if (moduleCode.isEmpty()) {
                binding.subjectCodeTil.setEndIconDrawable(R.drawable.error_red)
                binding.subjectCodeTil.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.RED))
                isValid = false
            }

            if (moduleName.isEmpty()) {
                binding.subjectNameTil.setEndIconDrawable(R.drawable.error_red)
                binding.subjectNameTil.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.RED))
                isValid = false
            }

            if (quizCount.isEmpty()) {
                binding.quantityQuizTil.setEndIconDrawable(R.drawable.error_red)
                binding.quantityQuizTil.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.RED))
                isValid = false
            }

            if (totalStudents.isEmpty()) {
                binding.numberQuizTil.setEndIconDrawable(R.drawable.error_red)
                binding.numberQuizTil.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.RED))
                isValid = false
            }

            if (isValid) {
                startActivity(Intent(this, ProfessorFileUploadActivity::class.java))
            }
        }
    }

    private fun clearFieldErrors() {
        binding.subjectCodeTil.isErrorEnabled = false
        binding.subjectNameTil.isErrorEnabled = false
        binding.quantityQuizTil.isErrorEnabled = false
        binding.numberQuizTil.isErrorEnabled = false
    }
}