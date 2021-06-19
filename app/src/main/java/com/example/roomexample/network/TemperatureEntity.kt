package com.example.roomexample.network

data class TemperatureEntity(
    val city: String,
    val minT: String,
    val maxT: String,
    val rainProb: String,
    val summary: String,
    val feeling: String
)
