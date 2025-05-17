package com.example.adminfoodapp.model

data class Order(
    val id: Int,
    val user: String,
    val food_name: String,
    val count: String,
    val total_price: Double,
    val state: String,
    val imageUrl: String?
)
