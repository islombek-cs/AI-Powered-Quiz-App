package com.example.ai_poweredquizapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_poweredquizapp.databinding.ActivityMainBinding
import com.example.ai_poweredquizapp.fragments.AssessmentFragment
import com.example.ai_poweredquizapp.fragments.HomeFragment
import com.example.ai_poweredquizapp.fragments.ProfileFragment
import com.example.ai_poweredquizapp.fragments.TaskFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            startLoginOptionsActivity()
        }*/

        firebaseAuth = FirebaseAuth.getInstance()
        val prefs = getSharedPreferences("appPrefs", MODE_PRIVATE)
        val isGuest = prefs.getBoolean("isGuest", false)

        if (firebaseAuth.currentUser == null && !isGuest) {
            startLoginOptionsActivity()
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        By default (when app opens) show HomeFragment
        showHomeFragment()

        // Handle BottomNavigationView item clicks
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId

            when (itemId) {
                R.id.item_home -> {
                    showHomeFragment()
                }
                R.id.item_assessment -> {
                    showAssessmentFragment()
                }
                R.id.item_task -> {
                    showTaskFragment()
                }
                R.id.item_profile -> {
                    showProfileFragment()
                }
            }
            true
        }
    }

    private fun showHomeFragment() {
        binding.toolbarTitleTv.text = "Home"

        val homeFragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, homeFragment, "HomeFragment")
        fragmentTransaction.commit()
    }

    private fun showAssessmentFragment() {
        binding.toolbarTitleTv.text = "Assessment"

        val assessmentFragment = AssessmentFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, assessmentFragment, "AssessmentFragment")
        fragmentTransaction.commit()
    }

    private fun showTaskFragment() {
        binding.toolbarTitleTv.text = "Task"

        val taskFragment = TaskFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, taskFragment, "TaskFragment")
        fragmentTransaction.commit()
    }

    private fun showProfileFragment() {
        binding.toolbarTitleTv.text = "Profile"

        val profileFragment = ProfileFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, profileFragment, "ProfileFragment")
        fragmentTransaction.commit()
    }

    private fun startLoginOptionsActivity() {
        val intent = Intent(this, LoginOptionsActivity::class.java)
        startActivity(intent)
        finish()
    }

}