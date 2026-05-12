package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderIdText: TextView = itemView.findViewById(R.id.orderIdText)
        private val orderDateText: TextView = itemView.findViewById(R.id.orderDateText)
        private val orderDescText: TextView = itemView.findViewById(R.id.orderDescText)
        private val orderTotalText: TextView = itemView.findViewById(R.id.orderTotalText)
        private val orderStatusText: TextView = itemView.findViewById(R.id.orderStatusText)

        fun bind(order: Order) {
            orderIdText.text = "Pedido #${order.id}"
            orderDateText.text = order.date
            orderDescText.text = order.description
            orderTotalText.text = "$${String.format("%.2f", order.total)}"
            orderStatusText.text = order.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size
}
