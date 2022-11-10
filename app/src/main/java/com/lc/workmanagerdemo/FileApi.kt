package com.lc.workmanagerdemo

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {

    @GET("/wp-content/uploads/bees-working-on-a-big-colorful-sunflower-1080x720.jpg")
    suspend fun downloadImage(): Response<ResponseBody>

    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://picjumbo.com")
                .build()
                .create(FileApi::class.java)
        }
    }
}