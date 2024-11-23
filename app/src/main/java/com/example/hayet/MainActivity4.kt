package com.example.hayet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hayet.ui.theme.HayetTheme

class MainActivity4 : ComponentActivity() {
    private val requestCodeUpdateProduct = 1001
    private val requestCodeAddProduct = 1002

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database helper and insert default products if the database is empty
        val dbHelper = DatabaseHelper(this)
        dbHelper.insertDefaultProducts()

        setContent {
            HayetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    ProductListScreen(
                        onProductClick = { productId, productName, productPrice, productDescription, productImage ->
                            val intent = Intent(this, MainActivity5::class.java).apply {
                                putExtra("productId", productId)
                                putExtra("productName", productName)
                                putExtra("productPrice", productPrice)
                                putExtra("productDescription", productDescription)
                                putExtra("productImageUrl", productImage)
                            }
                            startActivityForResult(intent, requestCodeUpdateProduct)
                        },
                        onAddProduct = {
                            val intent = Intent(this, AddProductActivity::class.java)
                            startActivityForResult(intent, requestCodeAddProduct)
                        },
                        onLogout = {
                            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putBoolean("isLoggedIn", false)
                                apply()
                            }
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && (requestCode == requestCodeUpdateProduct || requestCode == requestCodeAddProduct)) {
            // Refresh the product list after update, delete, or add
            setContent {
                HayetTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) {
                        ProductListScreen(
                            onProductClick = { productId, productName, productPrice, productDescription, productImage ->
                                val intent = Intent(this, MainActivity5::class.java).apply {
                                    putExtra("productId", productId)
                                    putExtra("productName", productName)
                                    putExtra("productPrice", productPrice)
                                    putExtra("productDescription", productDescription)
                                    putExtra("productImageUrl", productImage)
                                }
                                startActivityForResult(intent, requestCodeUpdateProduct)
                            },
                            onAddProduct = {
                                val intent = Intent(this, AddProductActivity::class.java)
                                startActivityForResult(intent, requestCodeAddProduct)
                            },
                            onLogout = {
                                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                with(sharedPreferences.edit()) {
                                    putBoolean("isLoggedIn", false)
                                    apply()
                                }
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListScreen(
    onProductClick: (Int, String, Double, String, Int) -> Unit,
    onAddProduct: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)

    // MutableState to track product list
    var productList by remember { mutableStateOf(dbHelper.getAllProducts()) }

    // Automatically refresh the product list when the screen is revisited
    LaunchedEffect(Unit) {
        productList = dbHelper.getAllProducts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(productList) { product ->
                ProductCard(
                    product = product,
                    onViewDetails = {
                        onProductClick(
                            product.id,
                            product.name,
                            product.price,
                            product.description,
                            product.imageUrl
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Product Button
        Button(
            onClick = onAddProduct,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Product")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun ProductCard(product: Product, onViewDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onViewDetails() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Image(
                painter = painterResource(id = product.imageUrl),
                contentDescription = product.name,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                // Product Name
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                // Product Price
                Text(text = "${product.price} €", style = MaterialTheme.typography.bodySmall)
                // Product Description
                Text(text = product.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
