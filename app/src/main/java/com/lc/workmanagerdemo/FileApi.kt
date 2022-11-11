package com.lc.workmanagerdemo

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {

    @GET("wp-content/uploads/bees-working-on-a-big-colorful-sunflower-1080x720.jpg")
    suspend fun downloadImage(): Response<ResponseBody>

    //https://mazwai.com/videvo_files/video/free/2017-05/small_watermarked/170411A_05_Blossom_1080p_preview.webm

    @GET("/videvo_files/video/free/2017-05/small_watermarked/170411A_05_Blossom_1080p_preview.webm")
    suspend fun downloadVideo(): Response<ResponseBody>

    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://picjumbo.com/")
                //.baseUrl("https://mazwai.com")
                .build()
                .create(FileApi::class.java)
        }
    }
}