package com.example.hayet.data

data class Product(
    val id: Int = 0,           // Unique ID for SQLite (default to 0 for new entries)
    val name: String,          // Product name
    val price: Double,         // Product price
    val description: String,   // Product description
    val imageUrl: Int          // Resource ID for product image
)
