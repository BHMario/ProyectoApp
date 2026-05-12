package com.example.myapplication

data class OrderItem(
    val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
) {
    fun getTotalPrice(): Double = quantity * unitPrice
}
