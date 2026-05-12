package com.example.myapplication

data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String = "",
    val isAdmin: Boolean = false
)
