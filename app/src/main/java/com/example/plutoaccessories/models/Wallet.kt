package com.example.plutoaccessories.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wallet(
    @SerializedName("id_wallet") val id: Long? = null,
    @SerializedName("id_user") val userId: Int,
    @SerializedName("saldo") val balance: Double? = null,
    @SerializedName("last_update") val lastUpdate: String? = null
) : Parcelable
