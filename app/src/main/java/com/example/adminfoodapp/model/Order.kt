package com.example.adminfoodapp.model

data class Order(
    val id: Int,
    val foodName: String,
    val count: String,
    val imageUrl: String?,
    var state: String
)
