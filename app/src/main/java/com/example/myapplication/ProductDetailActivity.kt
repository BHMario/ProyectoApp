package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        
        // Obtener el producto del Intent
        product = intent.getParcelableExtra("PRODUCT_EXTRA") ?: return finish()

        setupToolbar()
        bindProductData()
        setupAddToCartButton()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // Usaremos nuestro botón personalizado
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindProductData() {
        val productImage: ImageView = findViewById(R.id.detailProductImage)
        val productName: TextView = findViewById(R.id.detailProductName)
        val productCategory: TextView = findViewById(R.id.detailProductCategory)
        val productPrice: TextView = findViewById(R.id.detailProductPrice)
        val productStock: TextView = findViewById(R.id.detailProductStock)
        val productDescription: TextView = findViewById(R.id.detailProductDescription)

        productName.text = product.name
        productCategory.text = product.category.uppercase()
        productPrice.text = "$${String.format("%.2f", product.price)}"
        productStock.text = "Stock disponible: ${product.stock}"
        productDescription.text = product.description

        if (product.imageUri.isNotEmpty()) {
            productImage.setImageURI(Uri.parse(product.imageUri))
        } else {
            productImage.setBackgroundColor(getColor(R.color.lightGray))
        }
    }

    private fun setupAddToCartButton() {
        val btnAddToCart: Button = findViewById(R.id.btnAddToCartDetail)
        val btnBack: View = findViewById(R.id.btnBackContainer)
        
        btnAddToCart.setOnClickListener {
            addToCart()
        }
        
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun addToCart() {
        if (product.stock > 0) {
            CartManager.addProduct(product)
            Toast.makeText(this, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Producto sin stock", Toast.LENGTH_SHORT).show()
        }
    }
}
