package com.example.myapplication

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var db: DatabaseHelper
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var emptyReviewsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        
        // Obtener el producto del Intent
        product = intent.getParcelableExtra("PRODUCT_EXTRA") ?: return finish()

        db = DatabaseHelper(this)

        setupToolbar()
        bindProductData()
        setupAddToCartButton()
        setupReviews()
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

        loadImage(product.imageUri, productImage)
    }

    private fun loadImage(uri: String, imageView: ImageView) {
        if (uri.isEmpty()) {
            imageView.setBackgroundColor(getColor(R.color.lightGray))
            return
        }

        if (uri.startsWith("assets://")) {
            val assetPath = uri.removePrefix("assets://")
            try {
                val stream = assets.open(assetPath)
                val bitmap = BitmapFactory.decodeStream(stream)
                stream.close()
                imageView.setImageBitmap(bitmap)
                imageView.background = null
            } catch (e: Exception) {
                imageView.setBackgroundColor(getColor(R.color.lightGray))
            }
        } else {
            try {
                imageView.setImageURI(Uri.parse(uri))
                imageView.background = null
            } catch (e: Exception) {
                imageView.setBackgroundColor(getColor(R.color.lightGray))
            }
        }
    }

    private fun setupAddToCartButton() {
        val btnAddToCart: Button = findViewById(R.id.btnAddToCartDetail)
        val btnBack: View = findViewById(R.id.btnBackContainer)
        val btnWriteReview: Button = findViewById(R.id.btnWriteReview)
        
        btnAddToCart.setOnClickListener {
            addToCart()
        }
        
        btnBack.setOnClickListener {
            finish()
        }

        btnWriteReview.setOnClickListener {
            showWriteReviewDialog()
        }
    }

    private fun setupReviews() {
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView)
        emptyReviewsText = findViewById(R.id.emptyReviewsText)

        reviewsRecyclerView.layoutManager = LinearLayoutManager(this)
        loadReviews()
    }

    private fun loadReviews() {
        val reviews = db.getReviewsByProduct(product.id)
        if (reviews.isEmpty()) {
            emptyReviewsText.visibility = View.VISIBLE
            reviewsRecyclerView.visibility = View.GONE
        } else {
            emptyReviewsText.visibility = View.GONE
            reviewsRecyclerView.visibility = View.VISIBLE
            reviewsRecyclerView.adapter = ReviewAdapter(reviews)
        }
    }

    private fun showWriteReviewDialog() {
        val userId = UserSession.getUserId(this)
        if (userId == -1) {
            Toast.makeText(this, "Debes iniciar sesión para dejar una reseña", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Verificar si ha comprado el producto
        if (!db.hasUserPurchasedProduct(userId, product.id)) {
            Toast.makeText(this, "Debes comprar el producto para dejar una reseña", Toast.LENGTH_LONG).show()
            return
        }

        // 2. Verificar si ya tiene una reseña
        val existingReview = db.getUserReviewForProduct(userId, product.id)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_write_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.dialogRatingBar)
        val commentInput = dialogView.findViewById<EditText>(R.id.dialogCommentInput)

        // Pre-cargar datos si existe
        if (existingReview != null) {
            ratingBar.rating = existingReview.rating.toFloat()
            commentInput.setText(existingReview.comment)
        }

        AlertDialog.Builder(this)
            .setTitle(if (existingReview == null) "Escribir reseña" else "Editar reseña")
            .setView(dialogView)
            .setPositiveButton(if (existingReview == null) "Enviar" else "Actualizar") { _, _ ->
                val rating = ratingBar.rating.toInt()
                val comment = commentInput.text.toString().trim()

                if (rating == 0) {
                    Toast.makeText(this, "Por favor selecciona una puntuación", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (comment.isEmpty()) {
                    Toast.makeText(this, "Por favor escribe un comentario", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                if (existingReview == null) {
                    // Insertar nueva
                    val userName = UserSession.getName(this)
                    val review = Review(
                        productId = product.id,
                        userId = userId,
                        userName = userName,
                        rating = rating,
                        comment = comment,
                        date = date
                    )

                    if (db.insertReview(review) != -1L) {
                        Toast.makeText(this, "¡Reseña enviada con éxito!", Toast.LENGTH_SHORT).show()
                        loadReviews()
                    } else {
                        Toast.makeText(this, "Error al enviar la reseña", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Actualizar existente
                    val updatedReview = existingReview.copy(
                        rating = rating,
                        comment = comment,
                        date = date
                    )

                    if (db.updateReview(updatedReview)) {
                        Toast.makeText(this, "Reseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                        loadReviews()
                    } else {
                        Toast.makeText(this, "Error al actualizar la reseña", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
