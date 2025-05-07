package com.example.ai_poweredquizapp.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.activities.ProfessorFirstFormActivity
import com.example.ai_poweredquizapp.activities.StudentFirstFormActivity
import com.example.ai_poweredquizapp.databinding.FragmentAssessmentBinding
import com.example.ai_poweredquizapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AssessmentFragment : Fragment() {

    private lateinit var binding: FragmentAssessmentBinding

    private val TAG = "ASSESSMENT_PAGE_TAG"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAssessmentBinding.inflate(inflater, container, false)
        return binding.root
    }

}
