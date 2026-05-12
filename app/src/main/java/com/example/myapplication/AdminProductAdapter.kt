package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminProductAdapter(
    private val products: List<Product>,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView     = view.findViewById(R.id.adminProductName)
        private val price: TextView    = view.findViewById(R.id.adminProductPrice)
        private val category: TextView = view.findViewById(R.id.adminProductCategory)
        private val stock: TextView    = view.findViewById(R.id.adminProductStock)
        private val editBtn: Button    = view.findViewById(R.id.adminEditButton)
        private val deleteBtn: Button  = view.findViewById(R.id.adminDeleteButton)

        fun bind(product: Product) {
            name.text     = product.name
            price.text    = "$${String.format("%.2f", product.price)}"
            category.text = product.category
            stock.text    = "Stock: ${product.stock}"
            editBtn.setOnClickListener   { onEdit(product) }
            deleteBtn.setOnClickListener { onDelete(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(products[position])

    override fun getItemCount() = products.size
}
