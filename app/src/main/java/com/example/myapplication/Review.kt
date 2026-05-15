package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

data class Review(
    val id: Int = 0,
    val productId: Int,
    val userId: Int,
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(productId)
        parcel.writeInt(userId)
        parcel.writeString(userName)
        parcel.writeInt(rating)
        parcel.writeString(comment)
        parcel.writeString(date)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel) = Review(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Review>(size)
    }
}
