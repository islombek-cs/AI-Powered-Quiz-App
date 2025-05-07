package com.example.ai_poweredquizapp.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorQuizItemBinding

class ProfessorQuizItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorQuizItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorQuizItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button action
        binding.toolbarBackBtn.setOnClickListener { finish() }
    }
}