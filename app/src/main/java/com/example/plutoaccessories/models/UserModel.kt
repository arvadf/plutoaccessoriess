package com.example.plutoaccessories.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    @SerializedName("id_user") val idUser: Int? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null, // Password should not be stored here
    @SerializedName("alamat") val alamat: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("role") val role: String? = null
) : Parcelable
