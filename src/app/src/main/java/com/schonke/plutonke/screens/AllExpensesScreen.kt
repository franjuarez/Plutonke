package com.schonke.plutonke.screens

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.schonke.plutonke.Category
import com.schonke.plutonke.Expense

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun allExpensesScreen(navController: NavController){

    //DATA PARA EJEMPLOS
    val comida = Category(name = "Comida", maxAmount = 50000)
    val diversion = Category(name = "Diversion", maxAmount = 150000)
    val salidas = Category(name = "Salidas", maxAmount = 200000)

    val expenses = listOf<Expense>(Expense("Mac", "01-01-2024", 234, comida),
        Expense("Minecraft Premium", "01-03-2021", 20000, diversion),
        Expense("Parque de la costa", "01-02-2021", 90000, diversion),
        Expense("Cumple de Monke", "12-01-2004", 10000, salidas))

    val categories = listOf<Category>(
        comida,
        diversion,
        salidas
    )

    Scaffold (topBar = { topBar() } ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            showAllExpenses(expenses = expenses)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topBar(){
    TopAppBar(
        title = { Text(text = "All Expenses") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        navigationIcon = {
            IconButton(onClick = { /* TODO: abrir drawer*/ }) {
                Icon(Icons.Default.Menu, contentDescription = "Options")
            }
        }
    )

}

@Composable
fun showAllExpenses(expenses: List<Expense>){
    LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        items(expenses){ expense ->
            showExpense(expense = expense)

        }
    }
}

@Composable
fun showExpense(expense: Expense) {
    //TODO Cambiar category por su tipo
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        ListItem(
            headlineContent = { Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                Text(text = expense.name, fontSize = 15.sp) } },
            supportingContent = {
                Text(
                    text = expense.category.toString(),
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 12.sp, fontWeight = FontWeight.ExtraBold
                )
            },
            trailingContent = { Column {
                Text(
                    text = "$" + expense.price.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 18.sp
                )
                Text(text = expense.date.toString(), fontSize = 12.sp)
            }
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )
    }
}