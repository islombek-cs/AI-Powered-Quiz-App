package com.example.ai_poweredquizapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityStudentQuizListBinding
import com.example.ai_poweredquizapp.fragments.AssessmentFragment

class StudentQuizListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentQuizListBinding
    private lateinit var countDownTimer: CountDownTimer
    private var quizFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentQuizListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Timer + button logic
        binding.submitQuizBtn.setOnClickListener {
            if (!quizFinished) {
                showConfirmDialog()
            }
        }

        startTimer()
    }

    private fun showConfirmDialog() {
        val dialogView = layoutInflater.inflate(R.layout.student_quiz_confirm_dialog, null)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val okBtn = dialogView.findViewById<Button>(R.id.okBtn)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelBtn)

        okBtn.setOnClickListener {
            quizFinished = true
            dialog.dismiss()
            openAssessmentFragment()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(10 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.timerTv.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                if (!quizFinished) {
                    quizFinished = true
                    openAssessmentFragment()
                }
            }
        }
        countDownTimer.start()
    }

    private fun openAssessmentFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openAssessment", true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
