package com.example.plutoaccessories.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id_user") val idUser: Int? = null,
    // get_user.php sometimes returns key 'id', handle both
    @SerializedName("id") val idAlt: Int? = null,
    @SerializedName("nama") val nama: String,
    @SerializedName("email") val email: String,
    @SerializedName("alamat") val alamat: String?,
    @SerializedName("no_hp") val noHp: String?,
    @SerializedName("role") val role: String
)