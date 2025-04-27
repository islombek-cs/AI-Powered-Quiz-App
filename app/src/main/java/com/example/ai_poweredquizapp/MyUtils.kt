package com.example.ai_poweredquizapp

import android.content.Context
import android.widget.Toast

object MyUtils {

    const val USER_TYPE_GOOGLE = "Google"
    const val USER_TYPE_EMAIL = "Email"
    const val USER_TYPE_PHONE = "Phone"

    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun timestamp(): Long{
        return System.currentTimeMillis()
    }
}