package com.example.plutoaccessories.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    @SerializedName("id_transaksi") val id: Long? = null,
    @SerializedName("id_user") val userId: Int,
    @SerializedName("tipe") val type: String,
    @SerializedName("jumlah") val amount: Double,
    @SerializedName("deskripsi") val description: String? = null,
    @SerializedName("tanggal") val createdAt: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("rejection_reason") val rejectionReason: String? = null,
    @SerializedName("users") @Expose(serialize = false) val user: UserModel? = null
) : Parcelable
