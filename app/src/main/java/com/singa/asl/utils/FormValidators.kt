package com.singa.asl.utils

import android.util.Patterns

object FormValidators {
    fun isNameValid(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 3 && name.matches(Regex("^[a-zA-Z ]+\$"))
    }
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()
    }
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 && password.isNotEmpty()
    }
}