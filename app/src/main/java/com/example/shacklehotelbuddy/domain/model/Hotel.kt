package com.example.shacklehotelbuddy.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: String,
    val location: String,
    val star: String
)