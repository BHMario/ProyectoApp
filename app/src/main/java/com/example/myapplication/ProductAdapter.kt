package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.productName)
        private val priceTextView: TextView = itemView.findViewById(R.id.productPrice)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryBadge)
        private val addButton: Button = itemView.findViewById(R.id.addToCartButton)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text = "$${String.format("%.2f", product.price)}"
            descriptionTextView.text = product.description
            categoryTextView.text = product.category
            addButton.setOnClickListener {
                onAddToCart(product)
            }
            if (product.imageUri.isNotEmpty()) {
                productImage.setImageURI(Uri.parse(product.imageUri))
            } else {
                productImage.setImageDrawable(null)
                productImage.setBackgroundColor(
                    itemView.context.getColor(R.color.lightGray)
                )
            }
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
