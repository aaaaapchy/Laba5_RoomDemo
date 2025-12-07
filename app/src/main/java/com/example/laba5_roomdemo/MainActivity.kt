package com.example.laba5_roomdemo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laba5_roomdemo.ui.theme.Laba5_RoomDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laba5_RoomDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val owner = LocalViewModelStoreOwner.current
                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext
                                        as Application
                            )
                        )
                        ScreenSetup(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenSetup(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val allProducts by viewModel.allProducts.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())
    MainScreen(
        modifier = modifier,
        allProducts = allProducts,
        searchResults = searchResults,
        viewModel = viewModel
    )
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    allProducts: List<Product>,
    searchResults: List<Product>,
    viewModel: MainViewModel
) {
    var productName by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    val onProductTextChange = { text: String ->
        productName = text
    }
    val onQuantityTextChange = { text: String ->
        productQuantity = text
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Поля ввода
        CustomTextField(
            title = "Product Name",
            textState = productName,
            onTextChange = onProductTextChange,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            title = "Quantity",
            textState = productQuantity,
            onTextChange = onQuantityTextChange,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Button(onClick = {
                if (productQuantity.isNotEmpty()) {
                    viewModel.insertProduct(
                        Product(
                            productName,
                            productQuantity.toInt()
                        )
                    )
                    searching = false
                }
            }) {
                Text("Add")
            }

            Button(onClick = {
                searching = true
                viewModel.findProduct(productName)
            }) {
                Text("Search")
            }

            Button(onClick = {
                searching = false
                viewModel.deleteProduct(productName)
            }) {
                Text("Delete")
            }

            Button(onClick = {
                searching = false
                productName = ""
                productQuantity = ""
            }) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Список продуктов
        Text(
            text = if (searching) "Search Results" else "All Products",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val list = if (searching) searchResults else allProducts

            item {
                TitleRow(head1 = "ID", head2 = "Product", head3 = "Quantity")
            }

            if (list.isEmpty()) {
                item {
                    Text(
                        text = if (searching) "No results found" else "No products yet",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(list) { product ->
                    ProductRow(
                        id = product.id,
                        name = product.productName,
                        quantity = product.quantity
                    )
                }
            }
        }
    }
}

@Composable
fun TitleRow(head1: String, head2: String, head3: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            head1,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            head2,
            color = Color.White,
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 4.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            head3,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProductRow(id: Int, name: String, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp)
    ) {
        Text(
            text = id.toString(),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = name,
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 4.dp)
        )
        Text(
            text = quantity.toString(),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = textState,
        onValueChange = { onTextChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        label = { Text(title) },
        modifier = modifier,
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
}

class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}