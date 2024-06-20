package com.capstone.skinenthusiast.data.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("user")
    val user: User? = null
)
