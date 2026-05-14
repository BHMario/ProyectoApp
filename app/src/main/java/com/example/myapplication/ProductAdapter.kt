package com.example.myapplication

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.productName)
        private val priceTextView: TextView = itemView.findViewById(R.id.productPrice)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryBadge)
        private val addButton: Button = itemView.findViewById(R.id.addToCartButton)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val placeholder: View = itemView.findViewById(R.id.productImagePlaceholder)

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text = "$${String.format("%.2f", product.price)}"
            descriptionTextView.text = product.description
            categoryTextView.text = product.category
            
            itemView.setOnClickListener {
                onProductClick(product)
            }

            addButton.setOnClickListener {
                onAddToCart(product)
            }

            loadImage(product.imageUri)
        }

        private fun loadImage(uri: String) {
            if (uri.isEmpty()) {
                showPlaceholder()
                return
            }

            if (uri.startsWith("assets://")) {
                val assetPath = uri.removePrefix("assets://")
                try {
                    val stream = itemView.context.assets.open(assetPath)
                    val bitmap = BitmapFactory.decodeStream(stream)
                    stream.close()
                    productImage.setImageBitmap(bitmap)
                    productImage.background = null // Quitar color gris
                    placeholder.visibility = View.GONE
                } catch (e: Exception) {
                    showPlaceholder()
                }
            } else {
                try {
                    productImage.setImageURI(Uri.parse(uri))
                    productImage.background = null
                    placeholder.visibility = View.GONE
                } catch (e: Exception) {
                    showPlaceholder()
                }
            }
        }

        private fun showPlaceholder() {
            productImage.setImageDrawable(null)
            productImage.setBackgroundColor(itemView.context.getColor(R.color.lightGray))
            placeholder.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}
