package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var backButton: Button
    private val cart = mutableListOf<CartProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("UNCHECKED_CAST")
        val cartItems = intent.getParcelableArrayListExtra<CartProduct>("cartItems") as? ArrayList<CartProduct> ?: ArrayList()
        cart.addAll(cartItems)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.totalPrice)
        checkoutButton = findViewById(R.id.checkoutButton)
        backButton = findViewById(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CartAdapter(cart) {
            updateTotal()
        }

        updateTotal()

        checkoutButton.setOnClickListener {
            // Aquí iría la lógica de compra
        }

        backButton.setOnClickListener {
            val intent = Intent()
            intent.putParcelableArrayListExtra("cartItems", cart as ArrayList<CartProduct>)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun updateTotal() {
        val total = cart.sumOf { it.getTotalPrice() }
        totalTextView.text = "Total: $${String.format("%.2f", total)}"
    }
}
