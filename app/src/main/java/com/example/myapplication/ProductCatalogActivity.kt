package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private lateinit var adminButton: ImageButton
    private lateinit var cartCount: TextView
    private val cart = mutableListOf<CartProduct>()

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
        adminButton = findViewById(R.id.adminButton)
        cartCount = findViewById(R.id.cartCount)

        // Configurar visibilidad del botón de admin
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        if (isAdmin) {
            adminButton.visibility = View.VISIBLE
        } else {
            adminButton.visibility = View.GONE
        }

        adminButton.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ProductAdapter(ProductRepository.products) { product ->
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
