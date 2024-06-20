package com.capstone.skinenthusiast.data.models

import com.google.gson.annotations.SerializedName

data class UpdateResponse(

    @field:SerializedName("data")
    val data: DataUpdate,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataUpdate(

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)
