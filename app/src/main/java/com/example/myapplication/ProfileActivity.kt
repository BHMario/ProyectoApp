package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameText       = findViewById<TextView>(R.id.profileName)
        val emailText      = findViewById<TextView>(R.id.profileEmail)
        val phoneText      = findViewById<TextView>(R.id.profilePhone)
        val logoutButton   = findViewById<Button>(R.id.logoutButton)
        val adminButton    = findViewById<Button>(R.id.adminButton)
        val ordersRecycler = findViewById<RecyclerView>(R.id.ordersRecyclerView)
        val emptyOrdersText = findViewById<TextView>(R.id.emptyOrdersText)
        val bottomNav      = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        nameText.text  = UserSession.getName(this)
        emailText.text = UserSession.getEmail(this)
        val phone = UserSession.getPhone(this)
        phoneText.text = if (phone.isNotEmpty()) phone else "No especificado"

        // Load orders from SQLite
        val userId = UserSession.getUserId(this)
        val orders = if (userId != -1) DatabaseHelper(this).getOrdersForUser(userId) else emptyList()

        if (orders.isEmpty()) {
            emptyOrdersText.visibility = View.VISIBLE
            ordersRecycler.visibility  = View.GONE
        } else {
            emptyOrdersText.visibility = View.GONE
            ordersRecycler.visibility  = View.VISIBLE
            ordersRecycler.layoutManager = LinearLayoutManager(this)
            ordersRecycler.adapter = OrderAdapter(orders)
        }

        // Show admin button only for admin users
        if (UserSession.isAdmin(this)) {
            adminButton.visibility = View.VISIBLE
            adminButton.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            adminButton.visibility = View.GONE
        }

        logoutButton.setOnClickListener {
            UserSession.logout(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, ProductCatalogActivity::class.java)
                        .apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java)
                        .apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}

