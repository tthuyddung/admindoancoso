package com.example.adminfoodapp.model

data class FoodItem(
    val food_name: String,
    val price: Double,
    val image_url: String,
    val description: String,
    val ingredients: String,
    val id: Int
)
