package com.example.music_streaming.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {

    @GET("/v3/432b51bf-e2f2-40a0-9daf-1121783fd286")
    fun listMusics(): Call<MusicDto>
}