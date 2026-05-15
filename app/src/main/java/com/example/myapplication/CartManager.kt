package com.example.myapplication

object CartManager {
    private val cartItems = mutableListOf<CartProduct>()

    fun getCart(): List<CartProduct> = cartItems

    fun addProduct(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartProduct(product, 1))
        }
    }

    fun removeProduct(product: Product) {
        cartItems.removeAll { it.product.id == product.id }
    }

    fun updateQuantity(product: Product, quantity: Int) {
        val item = cartItems.find { it.product.id == product.id }
        if (item != null) {
            if (quantity <= 0) {
                cartItems.remove(item)
            } else {
                item.quantity = quantity
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotal(): Double = cartItems.sumOf { it.getTotalPrice() }
    
    fun getItemCount(): Int = cartItems.sumOf { it.quantity }

    fun setCart(items: List<CartProduct>) {
        cartItems.clear()
        cartItems.addAll(items)
    }
}
