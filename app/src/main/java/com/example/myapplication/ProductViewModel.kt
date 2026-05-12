package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * ProductViewModel acts as the "Controller" layer between the UI and the database.
 * It holds filter/search state so configuration changes (rotation) don't reset them.
 */
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseHelper(application)

    var currentFilter = "Todos"
        private set
    var currentSearch = ""
        private set

    // ── Read ──────────────────────────────────────────────────────────────── //

    fun getFilteredProducts(): List<Product> {
        val base = if (currentFilter == "Todos") db.getAllProducts()
                   else db.getProductsByCategory(currentFilter)
        return if (currentSearch.isBlank()) base
               else base.filter {
                   it.name.contains(currentSearch, ignoreCase = true) ||
                   it.description.contains(currentSearch, ignoreCase = true) ||
                   it.category.contains(currentSearch, ignoreCase = true)
               }
    }

    // ── Filter / Search state ─────────────────────────────────────────────── //

    fun setFilter(category: String) {
        currentFilter = category
    }

    fun setSearch(query: String) {
        currentSearch = query
    }

    // ── CRUD (used by AdminActivity) ──────────────────────────────────────── //

    fun insertProduct(product: Product): Long = db.insertProduct(product)

    fun updateProduct(product: Product): Int = db.updateProduct(product)

    fun deleteProduct(productId: Int): Int = db.deleteProduct(productId)

    fun getAllForAdmin(): List<Product> = db.getAllProducts()
}
