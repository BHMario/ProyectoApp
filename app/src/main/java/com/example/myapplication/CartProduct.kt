package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

data class CartProduct(
    val product: Product,
    var quantity: Int = 1
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Product::class.java.classLoader) ?: Product(0, "", 0.0, "", ""),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(product, flags)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int = 0

    fun getTotalPrice(): Double = product.price * quantity

    companion object CREATOR : Parcelable.Creator<CartProduct> {
        override fun createFromParcel(parcel: Parcel) = CartProduct(parcel)
        override fun newArray(size: Int) = arrayOfNulls<CartProduct>(size)
    }
}
