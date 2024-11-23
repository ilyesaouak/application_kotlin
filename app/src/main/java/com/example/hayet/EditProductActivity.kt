package com.example.hayet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hayet.data.Product
import com.example.hayet.ui.theme.HayetTheme

class EditProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get product details from the Intent
        val productId = intent.getIntExtra("productId", -1)
        val productName = intent.getStringExtra("productName") ?: ""
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productDescription = intent.getStringExtra("productDescription") ?: ""
        val productImageUrl = intent.getIntExtra("productImageUrl", 0)

        setContent {
            HayetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EditProductScreen(
                        productId = productId,
                        initialName = productName,
                        initialPrice = productPrice.toString(),
                        initialDescription = productDescription,
                        imageUrl = productImageUrl,
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EditProductScreen(
    productId: Int,
    initialName: String,
    initialPrice: String,
    initialDescription: String,
    imageUrl: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)

    var name by remember { mutableStateOf(initialName) }
    var price by remember { mutableStateOf(initialPrice) }
    var description by remember { mutableStateOf(initialDescription) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input fields for product details
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Product Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Product Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Update Button
        Button(
            onClick = {
                if (name.isNotBlank() && price.isNotBlank() && description.isNotBlank()) {
                    // Create updated product object
                    val updatedProduct = Product(
                        id = productId, // Existing product ID
                        name = name,
                        price = price.toDoubleOrNull() ?: 0.0,
                        description = description,
                        imageUrl = imageUrl // Use the same image resource as before
                    )

                    // Update the product in the SQLite database
                    val updated = dbHelper.updateProduct(
                        id = updatedProduct.id,
                        name = updatedProduct.name,
                        price = updatedProduct.price,
                        description = updatedProduct.description,
                        imageUrl = updatedProduct.imageUrl
                    )

                    // Show feedback
                    if (updated > 0) {
                        Toast.makeText(context, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                        (context as ComponentActivity).finish() // Close the activity
                    } else {
                        Toast.makeText(context, "Failed to update product!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Product")
        }
    }
}
