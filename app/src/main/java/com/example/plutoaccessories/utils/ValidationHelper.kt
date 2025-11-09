package com.example.plutoaccessories.utils

import android.util.Patterns

object ValidationHelper {

    fun isValidName(name: String?): Boolean {
        return !name.isNullOrEmpty() && name.length >= 3
    }

    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        return !password.isNullOrEmpty() && password.length >= 6
    }

    fun isValidPhone(phone: String?): Boolean {
        return !phone.isNullOrEmpty() && phone.length >= 10 && phone.all { it.isDigit() }
    }

    fun isValidAddress(address: String?): Boolean {
        return !address.isNullOrEmpty() && address.length >= 5
    }

    fun isFormValid(
        name: String?,
        email: String?,
        password: String?,
        phone: String?,
        address: String?
    ): Boolean {
        return isValidName(name)
                && isValidEmail(email)
                && isValidPassword(password)
                && isValidPhone(phone)
                && isValidAddress(address)
    }
}
