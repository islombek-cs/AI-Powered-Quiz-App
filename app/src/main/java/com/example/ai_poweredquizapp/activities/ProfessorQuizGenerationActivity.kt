package com.example.ai_poweredquizapp.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorQuizGenerationBinding

class ProfessorQuizGenerationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorQuizGenerationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorQuizGenerationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        binding.toolbarBackBtn.setOnClickListener { finish() }

        // View student quiz
        binding.viewStudent1QuizBtn.setOnClickListener {
            val intent = Intent(this, ProfessorQuizItemActivity::class.java)
            startActivity(intent)
        }

        // Share quiz button
        binding.finalShareBtn.setOnClickListener {
            showShareDialog()
        }
    }

    private fun showShareDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quiz_share, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Find views first
        val loadingIcon = dialogView.findViewById<ImageView>(R.id.loadingIcon)
        val completedIcon = dialogView.findViewById<ImageView>(R.id.completedIcon)
        val statusText = dialogView.findViewById<TextView>(R.id.statusText)

        // Animate the loading icon
        val rotation = ObjectAnimator.ofFloat(loadingIcon, View.ROTATION, 0f, 360f)
        rotation.duration = 1000
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.interpolator = LinearInterpolator()
        rotation.start()

        // Simulate loading and success
        Handler(Looper.getMainLooper()).postDelayed({
            rotation.cancel()
            loadingIcon.visibility = View.GONE
            completedIcon.visibility = View.VISIBLE
            statusText.text = "Successfully shared questions \nwith these students."

            // Close dialog and go to home after short wait
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                navigateToHomePage()
            }, 2500)
        }, 3000)
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
