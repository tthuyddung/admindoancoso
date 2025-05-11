package com.example.adminfoodapp.model

data class FoodItem(
    val food_name: String,
    val price: String,
    val image_url: String,
    val description: String,
    val ingredients: String,
    val id: Int
)
