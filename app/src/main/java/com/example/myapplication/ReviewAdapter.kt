package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.reviewUserName)
        val date: TextView = view.findViewById(R.id.reviewDate)
        val rating: RatingBar = view.findViewById(R.id.reviewRating)
        val comment: TextView = view.findViewById(R.id.reviewComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.userName.text = review.userName
        holder.date.text = review.date
        holder.rating.rating = review.rating.toFloat()
        holder.comment.text = review.comment
    }

    override fun getItemCount(): Int = reviews.size
}
