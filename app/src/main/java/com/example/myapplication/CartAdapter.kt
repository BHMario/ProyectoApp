package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.TextView

class CartAdapter(
    private val cartItems: MutableList<CartProduct>,
    private val onUpdateCart: () -> Unit,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.cartProductName)
        private val priceTextView: TextView = itemView.findViewById(R.id.cartProductPrice)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantityText)
        private val decrementButton: Button = itemView.findViewById(R.id.decrementButton)
        private val incrementButton: Button = itemView.findViewById(R.id.incrementButton)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(cartProduct: CartProduct) {
            nameTextView.text = cartProduct.product.name
            priceTextView.text = "$${String.format("%.2f", cartProduct.getTotalPrice())}"
            quantityTextView.text = cartProduct.quantity.toString()

            itemView.setOnClickListener {
                onProductClick(cartProduct.product)
            }

            decrementButton.setOnClickListener {
                if (cartProduct.quantity > 1) {
                    cartProduct.quantity--
                    notifyItemChanged(adapterPosition)
                    onUpdateCart()
                }
            }

            incrementButton.setOnClickListener {
                cartProduct.quantity++
                notifyItemChanged(adapterPosition)
                onUpdateCart()
            }

            removeButton.setOnClickListener {
                val itemToRemove = cartItems[adapterPosition]
                CartManager.removeProduct(itemToRemove.product)
                cartItems.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                onUpdateCart()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount() = cartItems.size
}
