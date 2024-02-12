package com.schonke.plutonke.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.viewModels.AllExpensesScreenViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllExpensesScreen(navController: NavController, drawerProperties: DrawerProperties, allExpensesScreenViewModel: AllExpensesScreenViewModel){
    val expenses = allExpensesScreenViewModel.sharedExpenses.value

    Scaffold (topBar = { AllExpensesScreenTopBar(drawerProperties = drawerProperties) } ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()){
            ShowAllExpenses(expenses = expenses ?: emptyList())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreenTopBar(drawerProperties: DrawerProperties){
    TopAppBar(
        title = { Text(text = "All Expenses") },
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
fun ShowAllExpenses(expenses: List<Expense>){
    if(expenses.isEmpty()){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "No expenses!",
                color = Color(0xFF8a969c)
                )
        }
    } else{
        LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            items(expenses){ expense ->
                ShowExpense(expense = expense)
            }
        }
    }
}

@Composable
fun ShowExpense(expense: Expense) {
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