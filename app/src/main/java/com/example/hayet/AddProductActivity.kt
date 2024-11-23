package com.example.hayet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hayet.ui.theme.HayetTheme

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HayetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddProductScreen(Modifier.padding(innerPadding)) {
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductScreen(modifier: Modifier = Modifier, onProductAdded: () -> Unit) {
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)

    // State variables for input fields
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf(R.drawable.product) }

    // Available images for the products
    val imageOptions = listOf(
        R.drawable.product1,
        R.drawable.product2,
        R.drawable.product3,
        R.drawable.product4,
        R.drawable.product5,
        R.drawable.product6,

    )

    Column(
        modifier = modifier
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

        // Image Selection
        Text(text = "Select Product Image", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            imageOptions.forEach { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { selectedImage = imageRes }
                        .padding(4.dp)
                        .border(
                            width = if (selectedImage == imageRes) 2.dp else 0.dp,
                            color = if (selectedImage == imageRes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Input validation
                if (name.isBlank() || price.isBlank() || description.isBlank()) {
                    Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val priceValue = price.toDoubleOrNull()
                if (priceValue == null || priceValue <= 0) {
                    Toast.makeText(context, "Price must be a valid positive number!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                try {
                    // Insert product into the database
                    val inserted = dbHelper.insertProduct(
                        name = name,
                        price = priceValue,
                        description = description,
                        imageUrl = selectedImage
                    )
                    if (inserted > 0) {
                        Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
                        onProductAdded() // Notify the parent activity to refresh the product list
                    } else {
                        Toast.makeText(context, "Failed to add product!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Handle unexpected errors
                    Log.e("AddProductActivity", "Error adding product: ${e.message}", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Product")
        }
    }
}
