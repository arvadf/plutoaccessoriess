package com.example.plutoaccessories.models

data class LoginResponse(
    val success: Int,
    val message: String,
    val user: UserModel?
)
