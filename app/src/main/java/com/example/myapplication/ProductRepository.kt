package com.example.myapplication

object ProductRepository {
    val products: MutableList<Product> = mutableListOf(
        Product(1, "Manga Jujutsu Kaisen Vol 1", 12.99, "Manga", "Primer volumen de la popular serie Jujutsu Kaisen"),
        Product(2, "Figura Akatsuki", 24.99, "Figuras", "Figura articulada de personaje Akatsuki"),
        Product(3, "Camiseta Naruto", 18.99, "Merchandising", "Camiseta 100% algodón con diseño de Naruto"),
        Product(4, "Manga One Piece Vol 5", 13.99, "Manga", "Quinto volumen de la saga One Piece"),
        Product(5, "Figura Demon Slayer", 29.99, "Figuras", "Figura premium de Tanjiro en acción"),
        Product(6, "Mochila Anime", 32.99, "Merchandising", "Mochila con diseños exclusivos anime"),
        Product(7, "Manga My Hero Vol 3", 12.99, "Manga", "Tercer volumen de My Hero Academia"),
        Product(8, "Gorro Sailor Moon", 16.99, "Merchandising", "Gorro de invierno con logo Sailor Moon")
    )

    fun addProduct(product: Product) {
        products.add(product)
    }

    fun getNextId(): Int = (products.maxOfOrNull { it.id } ?: 0) + 1
}
