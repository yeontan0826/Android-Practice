package com.example.airbnb

data class HouseModel(
    val id: String,
    val title: String,
    val price: String,
    val imgUrl: String,
    val lat: Double,
    val lng: Double
)