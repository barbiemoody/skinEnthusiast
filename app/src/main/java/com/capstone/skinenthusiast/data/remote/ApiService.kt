package com.capstone.skinenthusiast.data.remote

import com.capstone.skinenthusiast.data.models.AuthResponse
import com.capstone.skinenthusiast.data.models.PredictResponse
import com.capstone.skinenthusiast.data.models.UpdateResponse
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("gender") gender: String,
        @Field("role") role: String = "USER",
    ): AuthResponse

    @GET("account")
    suspend fun getAccountData(
        @Header("Authorization") token: String,
    ): AuthResponse

    @Multipart
    @POST("predict")
    suspend fun predictSkin(
        @Part file: MultipartBody.Part,
    ): PredictResponse

    @POST("account")
    @FormUrlEncoded
    suspend fun updateData(
        @Field("name") name: String? = null,
        @Field("email") email: String? = null,
        @Field("password") password: String? = null,
        @Field("gender") gender: String,
    ): UpdateResponse

    @Multipart
    @POST("upload/image")
    suspend fun updateProfileImage(
        @Part file: MultipartBody.Part,
    ): UpdateResponse
}