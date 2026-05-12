package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductCatalogActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartButton: ImageButton
    private lateinit var adminButton: ImageButton
    private lateinit var cartCount: TextView
    private lateinit var searchEditText: EditText
    private lateinit var filterChipGroup: RadioGroup
    private lateinit var emptyResultsText: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var viewModel: ProductViewModel

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

        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        recyclerView      = findViewById(R.id.productsRecyclerView)
        cartButton        = findViewById(R.id.cartButton)
        cartCount         = findViewById(R.id.cartCount)
        searchEditText    = findViewById(R.id.searchEditText)
        filterChipGroup   = findViewById(R.id.filterChipGroup)
        emptyResultsText  = findViewById(R.id.emptyResultsText)
        bottomNav         = findViewById(R.id.bottomNavigation)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        applyFilters()

        cartButton.setOnClickListener { openCart() }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearch(s?.toString()?.trim() ?: "")
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            val category = when (checkedId) {
                R.id.chipManga          -> "Manga"
                R.id.chipFiguras        -> "Figuras"
                R.id.chipMerchandising  -> "Merchandising"
                else                    -> "Todos"
            }
            viewModel.setFilter(category)
            applyFilters()
        }

        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> true
                R.id.nav_cart    -> { openCart(); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else             -> false
            }
        }
    }

    private fun applyFilters() {
        val products = viewModel.getFilteredProducts()
        if (products.isEmpty()) {
            recyclerView.visibility     = android.view.View.GONE
            emptyResultsText.visibility = android.view.View.VISIBLE
        } else {
            recyclerView.visibility     = android.view.View.VISIBLE
            emptyResultsText.visibility = android.view.View.GONE
            recyclerView.adapter = ProductAdapter(products) { addToCart(it) }
        }
    }

    private fun addToCart(product: Product) {
        val existing = cart.find { it.product.id == product.id }
        if (existing != null) existing.quantity++ else cart.add(CartProduct(product, 1))
        updateCartButton()
    }

    private fun openCart() {
        val intent = Intent(this, CartActivity::class.java)
        intent.putParcelableArrayListExtra("cartItems", ArrayList(cart))
        startActivityForResult(intent, CART_REQUEST_CODE)
    }

    private fun updateCartButton() {
        cartCount.text = cart.sumOf { it.quantity }.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CART_REQUEST_CODE && data != null) {
            @Suppress("UNCHECKED_CAST")
            val updated = data.getParcelableArrayListExtra<CartProduct>("cartItems") as? ArrayList<CartProduct> ?: ArrayList()
            cart.clear()
            cart.addAll(updated)
            updateCartButton()
        }
    }

    companion object {
        private const val CART_REQUEST_CODE = 100
    }
}


