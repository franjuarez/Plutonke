package com.schonke.plutonke.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.dialog.Dialog
import com.schonke.plutonke.Category
import com.schonke.plutonke.navigation.DrawerProperties
import androidx.compose.ui.window.Dialog
import com.schonke.plutonke.components.CategoryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditCategoriesScreen(navController: NavController, drawerProperties: DrawerProperties) {
    // aca se lo pediriamos a la bd
    val comida = Category(name = "a", maxAmount = 50000)
    val diversion = Category(name = "Diversionaaaaaaa", maxAmount = 150000)
    val salidas = Category(name = "Salidas", maxAmount = 200000)
    val cumpleanios_de_urko = Category(name = "Cumples de Urko", maxAmount = 999999)
    val otra_categoria = Category(name = "otra categoria", maxAmount = 10000)
    val steam = Category(name = "steam", maxAmount = 25000)
    val categories = listOf(
        comida,
        diversion,
        salidas,
        cumpleanios_de_urko,
        otra_categoria,
        steam
    )

    Scaffold(
        topBar = { TopBarConfiguration(drawerProperties = drawerProperties) }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Categories(categories)
        }
    }
}

@Composable
fun Categories(categories: List<Category>) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0])} // medio nefasto pero bueno

    LazyColumn() {
        items(categories){ category ->
            ElevatedButton(
                onClick =  {
                    isDialogVisible = true
                    selectedCategory = category
                },
                modifier = Modifier.fillMaxSize())
            {
                Text(category.name)
            }
        }
    }

    AlertDialogExample(
        onDismissRequest = {isDialogVisible = false},
        onConfirmation = {isDialogVisible = false},
        category = selectedCategory,
        isDialogVisible = isDialogVisible)
}


@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    category: Category,
    isDialogVisible: Boolean,

) {
    if (isDialogVisible) {
        val viewModel = CategoryViewModel()
        Dialog(onDismissRequest = { onDismissRequest()}) {
            Card {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(15.dp)
                ){
                    Text(
                        text = "Edit " + category.name + " properties",
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    //EditCatNameField(category = category)

                    var categoryName by remember { mutableStateOf(category.name)}
                    var categorySpentAmount by remember { mutableIntStateOf(category.spentAmount) }
                    var categoryMaxAmount by remember { mutableIntStateOf(category.maxAmount) }


                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = {categoryName = it},
                        label = { Text("Name") },
                        placeholder = { Text(category.name) }
                    )
                    OutlinedTextField(
                        value = categorySpentAmount.toString(),
                        onValueChange = { categorySpentAmount = it.toInt() },
                        label = { Text("Spent") },
                        placeholder = { Text(category.spentAmount.toString()) }
                    )
                    OutlinedTextField(
                        value = categoryMaxAmount.toString(),
                        onValueChange = { categoryMaxAmount = it.toInt() },
                        label = { Text("Max") },
                        placeholder = { Text(category.maxAmount.toString()) }
                    )


                    Row() {
                        TextButton(onClick = { onDismissRequest() }) {
                            Text("Dismiss")
                        }
                        Spacer(modifier = Modifier.padding(5.dp))
                        TextButton(onClick = {
                            viewModel.updateCategoryProperties(
                                category = category,
                                newName = categoryName,
                                newMaxAmount = categoryMaxAmount,
                                newSpentAmount = categorySpentAmount)
                            onConfirmation()
                        }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun EditCatNameField(category: Category, categoryName: MutableState<String>) {
    OutlinedTextField(
        value = categoryName.value,
        onValueChange = {categoryName.value = it},
        label = { Text("Name") },
        placeholder = { Text(category.name) }
    )

}

@Composable
private fun EditCatSpentAmount (category: Category) {
    var categorySpentAmount by remember { mutableIntStateOf(category.spentAmount) }
    OutlinedTextField(
        value = categorySpentAmount.toString(),
        onValueChange = { categorySpentAmount = it.toInt() },
        label = { Text("Spent") },
        placeholder = { Text(category.spentAmount.toString()) }
    )

}

@Composable
private fun EditCatMaxAmount (category: Category) {
    var categoryMaxAmount by remember { mutableIntStateOf(category.spentAmount) }
    OutlinedTextField(
        value = categoryMaxAmount.toString(),
        onValueChange = { categoryMaxAmount = it.toInt() },
        label = { Text("Spent") },
        placeholder = { Text(category.maxAmount.toString()) }
    )
}




