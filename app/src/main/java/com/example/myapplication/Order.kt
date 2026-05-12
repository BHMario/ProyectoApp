package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

data class Order(
    val id: String,
    val date: String,
    val description: String,
    val total: Double,
    val status: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(date)
        parcel.writeString(description)
        parcel.writeDouble(total)
        parcel.writeString(status)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel) = Order(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Order>(size)
    }
}
