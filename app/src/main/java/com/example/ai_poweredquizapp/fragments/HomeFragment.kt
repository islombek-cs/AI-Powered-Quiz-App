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
import com.example.ai_poweredquizapp.MyUtils
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.activities.ChangePasswordActivity
import com.example.ai_poweredquizapp.activities.MainActivity
import com.example.ai_poweredquizapp.activities.ProfessorFirstFormActivity
import com.example.ai_poweredquizapp.activities.ProfileEditActivity
import com.example.ai_poweredquizapp.activities.StudentFirstFormActivity
import com.example.ai_poweredquizapp.databinding.FragmentHomeBinding
import com.example.ai_poweredquizapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val TAG = "HOME_PAGE_TAG"

    private lateinit var mContext: Context

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadMyInfo()

        binding.cardProfessor.setOnClickListener {
            val intent = Intent(mContext, ProfessorFirstFormActivity::class.java)
            startActivity(intent)
        }

        binding.cardStudent.setOnClickListener {
            val intent = Intent(mContext, StudentFirstFormActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadMyInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange: ")
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    val name = "${snapshot.child("name").value}"
                    binding.fullNameTv.text = name

                    // Set profile image to profileIv
                    try {
                        Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.person_black_profile)
                            .into(binding.profileIv)
                    } catch (e: Exception) {
                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error here if needed
                }
            })
    }

}