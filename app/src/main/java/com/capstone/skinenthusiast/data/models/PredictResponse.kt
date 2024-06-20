package com.capstone.skinenthusiast.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PredictResponse(

    @field:SerializedName("data")
    val data: Data,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

@Parcelize
data class Data(

    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("bahan")
    val bahan: String? = null,

    @field:SerializedName("usage")
    val usage: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("benefit")
    val benefit: String? = null
) : Parcelable
