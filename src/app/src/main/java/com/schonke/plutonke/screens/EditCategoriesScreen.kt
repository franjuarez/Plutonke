package com.schonke.plutonke.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import com.schonke.plutonke.Category
import com.schonke.plutonke.navigation.DrawerProperties

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
    val categories = listOf<Category>(
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
    val openAlertDialog = remember { mutableStateOf(false) }
    var selectedCategory: Category? = null
    LazyColumn() {
        items(categories){ category ->
            ElevatedButton(onClick =  {
                openAlertDialog.value = true
                selectedCategory = category
            }) {
                Text(category.name)
            }
        }
    }
    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    println("Confirmation registered") // Add logic here to handle confirmation.
                },
                dialogTitle = "Edit ${selectedCategory?.name}",
                dialogText = "This is an example of an alert dialog with buttons.",


            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,

) {
    AlertDialog(

        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }

    )
}



