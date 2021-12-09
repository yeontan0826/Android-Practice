package com.example.youtube.service

import com.example.youtube.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("/v3/83981114-6e28-476d-a4e5-3b4dffd99714")
    fun listVideos(): Call<VideoDto>
}