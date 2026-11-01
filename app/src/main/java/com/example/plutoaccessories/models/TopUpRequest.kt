package com.example.plutoaccessories.models

import com.google.gson.annotations.SerializedName

data class TopUpRequest(
    @SerializedName("p_user_id")
    val userId: Int,
    @SerializedName("p_amount")
    val amount: Double
)
