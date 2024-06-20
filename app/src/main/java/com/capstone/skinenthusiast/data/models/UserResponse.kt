package com.capstone.skinenthusiast.data.models

import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("image_url")
	val image: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	var token: String? = null
)
