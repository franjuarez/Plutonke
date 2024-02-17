@file:OptIn(ExperimentalMaterial3Api::class)

package com.schonke.plutonke.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.schonke.plutonke.types.Category
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.viewModels.ExpensesViewModel
import com.schonke.plutonke.viewModels.HomeScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    drawerProperties: DrawerProperties,
    homeScreenViewModel: HomeScreenViewModel,
    expensesViewModel: ExpensesViewModel
) {
    val categories by homeScreenViewModel.sharedCategories.observeAsState()
    val dataUpdated by homeScreenViewModel.dataUpdated.observeAsState()

    if (dataUpdated == true) {/*For recomposition*/
    }


    Scaffold(
        topBar = { HomeScreenTopBar(drawerProperties = drawerProperties) },
        floatingActionButton = { HomeScreenAddExpenseButton(expensesViewModel, categories) }
    )
    { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ShowCategories(categories = categories ?: emptyList())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(drawerProperties: DrawerProperties) {
    TopAppBar(
        title = { Text(text = "Plutonke 🤑 🤑 🤑") },
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
            }
        }
    )
}

@Composable
fun HomeScreenAddExpenseButton(expensesViewModel: ExpensesViewModel, categories: List<Category>?) {
    var isDialogVisible by remember { mutableStateOf(false) }

    ExtendedFloatingActionButton(
        text = { Text("Add an expense") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add an expense") },
        onClick = { isDialogVisible = true }
    )

    val expenseName: String by expensesViewModel.expenseName.observeAsState(initial = "")
    val expenseDate: String by expensesViewModel.expenseDate.observeAsState(initial = "")
    val expensePrice: String by expensesViewModel.expensePrice.observeAsState(initial = "")
    val expenseCategoryID: UInt by expensesViewModel.expenseCategoryID.observeAsState(initial = 0U)
    val expenseValid by expensesViewModel.expenseValidState.collectAsState()

    if (isDialogVisible) {
        EditExpenseDialog(
            title = "Add an expense",
            expenseName = expenseName,
            expensePrice = expensePrice,
            expenseDate = expenseDate,
            expenseCategoryID = expenseCategoryID,
            categories = categories,
            onNameChanged = { expensesViewModel.onNameChanged(it) },
            onPriceChanged = { expensesViewModel.onPriceChanged(it) },
            onDateChanged = { expensesViewModel.onDateChanged(it) },
            onCategoryChanged = { expensesViewModel.onCategoryChanged(it) },
            onConfirmPressed = {
                expensesViewModel.onConfirmPressed()
            },
            onDismiss = {
                isDialogVisible = false
                expensesViewModel.resetExpense()
            },
            expenseValidState = expenseValid,
            resetExpenseValidState = { expensesViewModel.resetExpenseValidState() }
        )
    }
}

@Composable
fun ShowCategories(categories: List<Category>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 9.dp),
    ) {
        CategoriesHeadlineText(Modifier.align(alignment = Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(8.dp))
        if (categories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No categories",
                    color = Color(0xFF8a969c)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(categories) { category ->
                    Column(){
                        ShowCategory(category = category)
                        Spacer(modifier = Modifier.height(5.dp))
//                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesHeadlineText(modifier: Modifier) {
    Text(
        text = "Spent by Categories", modifier = modifier
            .padding(vertical = 4.dp),
        style = TextStyle(fontSize = 18.sp)
    )
}

@Composable
fun ShowCategory(category: Category) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryNameText(
                category = category,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            CategorySpentAmount(
                category = category,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        CategoryProgressBar(category = category)
    }
}

@Composable
private fun CategorySpentAmount(category: Category, modifier: Modifier) {
    Text(
        text = "$" + category.spentAmount.toString(),
        modifier = modifier.padding(top = 8.dp),
        fontSize = 15.sp
    )
}

@Composable
private fun CategoryNameText(category: Category, modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = category.name, fontSize = 18.sp)
    }
}

@Composable
private fun CategoryProgressBar(category: Category) {
    val progress = category.spentAmount / category.maxAmount
    val color = if (progress >= 1) Color.Red else MaterialTheme.colorScheme.primary
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .padding(top = 6.dp),
        color = color
    )
}
