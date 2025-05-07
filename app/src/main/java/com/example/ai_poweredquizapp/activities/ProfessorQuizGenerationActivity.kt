package com.example.ai_poweredquizapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorFirstFormBinding
import com.example.ai_poweredquizapp.databinding.ActivityProfessorQuizGenerationBinding

class ProfessorQuizGenerationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorQuizGenerationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorQuizGenerationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button action
        binding.toolbarBackBtn.setOnClickListener { finish() }

        binding.viewStudent1QuizBtn.setOnClickListener {
            val intent = Intent(this, ProfessorQuizItemActivity::class.java)
            startActivity(intent)
        }

    }
}