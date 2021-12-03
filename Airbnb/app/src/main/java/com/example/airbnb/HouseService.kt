package com.example.airbnb

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/c58cf17c-f4a2-4ee5-a65c-5d0a91c49e2b")
    fun getHouseList(): Call<HouseDto>
}