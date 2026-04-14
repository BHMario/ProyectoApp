package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductCatalogActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartButton: ImageButton
    private lateinit var cartCount: TextView
    private val cart = mutableListOf<CartProduct>()
    private val products = listOf(
        Product(1, "Manga Jujutsu Kaisen Vol 1", 12.99, "Manga", "Primer volumen de la popular serie Jujutsu Kaisen"),
        Product(2, "Figura Akatsuki", 24.99, "Figuras", "Figura articulada de personaje Akatsuki"),
        Product(3, "Camiseta Naruto", 18.99, "Merchandising", "Camiseta 100% algodón con diseño de Naruto"),
        Product(4, "Manga One Piece Vol 5", 13.99, "Manga", "Quinto volumen de la saga One Piece"),
        Product(5, "Figura Demon Slayer", 29.99, "Figuras", "Figura premium de Tanjiro en acción"),
        Product(6, "Mochila Anime", 32.99, "Merchandising", "Mochila con diseños exclusivos anime"),
        Product(7, "Manga My Hero Vol 3", 12.99, "Manga", "Tercer volumen de My Hero Academia"),
        Product(8, "Gorro Sailor Moon", 16.99, "Merchandising", "Gorro de invierno con logo Sailor Moon")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_catalog)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.productsRecyclerView)
        cartButton = findViewById(R.id.cartButton)
        cartCount = findViewById(R.id.cartCount)

        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        recyclerView.adapter = ProductAdapter(products) { product ->
            addToCart(product)
        }

        cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putParcelableArrayListExtra("cartItems", ArrayList(cart))
            startActivityForResult(intent, CART_REQUEST_CODE)
        }
    }

    private fun addToCart(product: Product) {
        val existingItem = cart.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cart.add(CartProduct(product, 1))
        }
        updateCartButton()
    }

    private fun updateCartButton() {
        val totalItems = cart.sumOf { it.quantity }
        cartCount.text = totalItems.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CART_REQUEST_CODE && data != null) {
            @Suppress("UNCHECKED_CAST")
            val updatedCart = data.getParcelableArrayListExtra<CartProduct>("cartItems") as? ArrayList<CartProduct> ?: ArrayList()
            cart.clear()
            cart.addAll(updatedCart)
            updateCartButton()
        }
    }

    companion object {
        private const val CART_REQUEST_CODE = 100
    }
}
