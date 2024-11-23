package com.example.hayet

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hayet.ui.theme.HayetTheme

class MainActivity5 : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve product details from the intent
        val productId = intent.getIntExtra("productId", -1)
        val productName = intent.getStringExtra("productName") ?: "Unknown Product"
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productDescription = intent.getStringExtra("productDescription") ?: "No description available"
        val productImage = intent.getIntExtra("productImageUrl", R.drawable.product)

        setContent {
            HayetTheme {
                Scaffold {
                    ProductDetailScreen(
                        id = productId,
                        name = productName,
                        price = productPrice.toString(),
                        description = productDescription,
                        imageUrl = productImage,
                        onProductUpdated = { finishWithResult() },
                        onProductDeleted = { finishWithResult() }
                    )
                }
            }
        }
    }

    // Notify MainActivity4 that data has changed
    private fun finishWithResult() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}

@Composable
fun ProductDetailScreen(
    id: Int,
    name: String,
    price: String,
    description: String,
    imageUrl: Int,
    onProductUpdated: () -> Unit,
    onProductDeleted: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)

    // States to handle the dialog visibility and input fields
    var showDialog by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf(name) }
    var editablePrice by remember { mutableStateOf(price) }
    var editableDescription by remember { mutableStateOf(description) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Name
        Text(text = name, fontSize = 24.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))

        // Product Image
        Image(
            painter = painterResource(id = imageUrl),
            contentDescription = name,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Product Price
        Text(text = "$price €", fontSize = 20.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Product Description
        Text(text = description, fontSize = 16.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        // Update Product Button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Product")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Product Button
        Button(
            onClick = {
                val deleted = dbHelper.deleteProduct(id)
                if (deleted > 0) {
                    Toast.makeText(context, "Product deleted successfully!", Toast.LENGTH_SHORT).show()
                    onProductDeleted() // Notify MainActivity4
                } else {
                    Toast.makeText(context, "Failed to delete product!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Product", color = Color.White)
        }
    }

    // Update Product Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Product") },
            text = {
                Column {
                    // Editable Product Name
                    TextField(
                        value = editableName,
                        onValueChange = { editableName = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Editable Product Price
                    TextField(
                        value = editablePrice,
                        onValueChange = { editablePrice = it },
                        label = { Text("Product Price") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Editable Product Description
                    TextField(
                        value = editableDescription,
                        onValueChange = { editableDescription = it },
                        label = { Text("Product Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val priceValue = editablePrice.toDoubleOrNull()
                    if (editableName.isNotBlank() && priceValue != null && priceValue > 0 && editableDescription.isNotBlank()) {
                        val updated = dbHelper.updateProduct(
                            id = id,
                            name = editableName,
                            price = priceValue,
                            description = editableDescription,
                            imageUrl = imageUrl
                        )
                        if (updated > 0) {
                            Toast.makeText(context, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                            showDialog = false
                            onProductUpdated() // Notify MainActivity4
                        } else {
                            Toast.makeText(context, "Failed to update product!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "All fields must be valid!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
