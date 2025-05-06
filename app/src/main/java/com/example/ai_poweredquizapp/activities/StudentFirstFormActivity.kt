package com.example.ai_poweredquizapp.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorFirstFormBinding
import com.example.ai_poweredquizapp.databinding.ActivityStudentFirstFormBinding

class StudentFirstFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentFirstFormBinding

    private val TAG = "STUDENT_FIRST_FORM_TAG"

    private lateinit var mContext: Context

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentFirstFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        val levels = arrayOf("Freshman", "Sophomore", "Juniors", "Seniors")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, levels)

        val studentLevelAutoTv = findViewById<AutoCompleteTextView>(R.id.studentLevelAutoTv)
        studentLevelAutoTv.setAdapter(adapter)

        val faculties = arrayOf("Architecture & Design", "Computer Science", "Biomedical Engineering", "Industrial Engineering")
        val facultyAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, faculties)
        val facultyAutoTv = findViewById<AutoCompleteTextView>(R.id.facultyAutoTv)
        facultyAutoTv.setAdapter(facultyAdapter)
    }
}