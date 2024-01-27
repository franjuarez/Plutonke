@file:OptIn(ExperimentalMaterial3Api::class)

package com.schonke.plutonke.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.schonke.plutonke.Category
import com.schonke.plutonke.Expense
import com.schonke.plutonke.navigation.DrawerProperties
import kotlinx.coroutines.launch

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun HomeScreen(navController: NavController){
//    Scaffold {
//        Column (
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(text = "Home screen")
//            Button(onClick = { navController.navigate(route = AppScreens.AllExpensesScreen.route) }) {
//                Text(text = "See all expenses")
//            }
//        }
//    }
//}

@Composable
fun HomeScreen(navController: NavController, drawerProperties: DrawerProperties) {
    val comida = Category(name = "a", maxAmount = 50000)
    val diversion = Category(name = "Diversionaaaaaaa", maxAmount = 150000)
    val salidas = Category(name = "Salidas", maxAmount = 200000)
    val cumpleanios_de_urko = Category(name = "Cumples de Urko", maxAmount = 999999)
    val otra_categoria = Category(name = "otra categoria", maxAmount = 10000)
    val steam = Category(name = "steam", maxAmount = 25000)


    val expenses = listOf<Expense>(
        Expense("Mac", "01-01-2024", 234, comida),
        Expense("Minecraft Premium", "01-03-2021", 20000, diversion),
        Expense("Parque de la costa", "01-02-2021", 90000, diversion),
        Expense("Cumple de Monke", "12-01-2004", 10000, salidas)
    )
    comida.spentAmount = 25000
    diversion.spentAmount = 200000
    val categories = listOf<Category>(
        comida,
        diversion,
        salidas,
        cumpleanios_de_urko,
        otra_categoria,
        steam
    )

    Scaffold (
        topBar = { HomeScreenTopBar(drawerProperties = drawerProperties) }
    )
    {innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            ShowCategories(categories = categories)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(drawerProperties: DrawerProperties){
    TopAppBar(
        title = { Text(text = "Plutonke 🤑 🤑 🤑")},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        navigationIcon = {
            IconButton(onClick = {
                drawerProperties.scope.launch {
                    drawerProperties.state.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }}
    )
}

@Composable
fun ShowCategories(categories: List<Category>) {
    Column(){
        Text(text = "Spent by Categories", modifier = Modifier
            .padding(vertical = 4.dp)
            .align(alignment = Alignment.CenterHorizontally))
        LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories){ category ->
                ShowCategory(category = category)

            }
        }
    }
}
@Composable
fun ShowCategory(category: Category) {
    val numberColor = if (category.spentAmount > category.maxAmount) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
    ElevatedButton(onClick = { println("Click") },
        modifier = Modifier
            .fillMaxSize()
            .height(70.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category.toString(), modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1.2f)) {
                LinearProgressIndicator(progress = category.spentAmount / category.maxAmount.toFloat(),
                modifier = Modifier
                    .height(25.dp)
                    .padding(top = 6.dp))

                Text(
                    text = "$" + category.spentAmount.toString(),
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(top = 6.dp),
                    fontSize = 12.sp)
            }

            Text("$" + category.maxAmount.toString(),
                color = numberColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End)
        }
    }
}