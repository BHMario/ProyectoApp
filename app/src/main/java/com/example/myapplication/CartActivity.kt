package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var backButton: Button
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // No necesitamos recibir cartItems por Intent, usamos CartManager

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.totalPrice)
        checkoutButton = findViewById(R.id.checkoutButton)
        backButton = findViewById(R.id.backButton)
        bottomNav = findViewById(R.id.bottomNavigation)

        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(CartManager.getCart().toMutableList(), { updateTotal() }, { openProductDetail(it) })
        recyclerView.adapter = cartAdapter

        updateTotal()

        checkoutButton.setOnClickListener {
            if (CartManager.getCart().isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val total = CartManager.getTotal()
            val orderId = System.currentTimeMillis().toString().takeLast(6)
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val userId = UserSession.getUserId(this)
            if (userId == -1) {
                Toast.makeText(this, "Error de sesión, inicia sesión de nuevo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DatabaseHelper(this).saveOrder(userId, CartManager.getCart(), total, date)

            CartManager.clearCart()
            updateTotal()
            // Actualizar la lista interna del adaptador también si es necesario, 
            // o simplemente volver a cargarla. Como el adaptador usa una copia mutable, debemos limpiarla.
            (recyclerView.adapter as? CartAdapter)?.notifyDataSetChanged() 
            // Nota: En una implementación real, el adaptador debería observar el CartManager
            Toast.makeText(this, "¡Pedido #$orderId realizado con éxito!", Toast.LENGTH_LONG).show()
        }

        backButton.setOnClickListener {
            finish()
        }

        bottomNav.selectedItemId = R.id.nav_cart
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish()
                    true
                }
                R.id.nav_cart -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updateTotal() {
        val total = CartManager.getTotal()
        totalTextView.text = "Total: $${String.format("%.2f", total)}"
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_EXTRA", product)
        startActivity(intent)
    }
}

