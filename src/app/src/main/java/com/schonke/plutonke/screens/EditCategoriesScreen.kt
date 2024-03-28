package com.schonke.plutonke.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.viewModels.CategoriesScreenViewModel
import com.schonke.plutonke.viewModels.CategoriesViewModel
import com.schonke.plutonke.viewModels.ExpensesViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun EditCategoriesScreen(
    navController: NavController,
    drawerProperties: DrawerProperties,
    categoriesScreenViewModel: CategoriesScreenViewModel,
    categoriesViewModel: CategoriesViewModel
) {

    val categories = categoriesScreenViewModel.sharedCategories.value
    val dataUpdated by categoriesScreenViewModel.dataUpdated.observeAsState()
    if (dataUpdated == true) {/*For recomposition*/
    }

    var isEditCategoryDialogVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(topBar = { EditCategoriesScreenTopBar(drawerProperties) },
        floatingActionButton = { EditCategoriesAddCategoryButton(categoriesViewModel) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isEditCategoryDialogVisible) {
//                EditExpenseOnClickDialog(
//                    expensesViewModel,
//                    selectedExpense!!,
//                    onDismiss = {
//                        isDialogVisible = false
//                        expensesViewModel.resetExpense()
//                    },
//                    categories ?: emptyList()
//                )
            } else {
                ShowAllEditCategories(categories ?: emptyList()) { clickedCategory ->
                    selectedCategory = clickedCategory
                    isEditCategoryDialogVisible = true
                }
            }
        }
    }
}

@Composable
fun EditCategoriesAddCategoryButton(categoriesViewModel: CategoriesViewModel) {
    var isAddCategoryDialogVisible by remember { mutableStateOf(false) }

    ExtendedFloatingActionButton(
        text = { Text("Add a Category") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add a Category") },
        onClick = { isAddCategoryDialogVisible = true }
    )

//    val expenseName: String by expensesViewModel.expenseName.observeAsState(initial = "")
//    val expenseDate: String by expensesViewModel.expenseDate.observeAsState(initial = "")
//    val expensePrice: String by expensesViewModel.expensePrice.observeAsState(initial = "")
//    val expenseCategoryID: UInt by expensesViewModel.expenseCategoryID.observeAsState(initial = 0U)
//    val expenseValid by expensesViewModel.expenseValidState.collectAsState()

    if (isAddCategoryDialogVisible) {

//        EditExpenseDialog(
//            title = "Add an expense",
//            expenseName = expenseName,
//            expensePrice = expensePrice,
//            expenseDate = expenseDate,
//            expenseCategoryID = expenseCategoryID,
//            categories = categories,
//            onNameChanged = { expensesViewModel.onNameChanged(it) },
//            onPriceChanged = { expensesViewModel.onPriceChanged(it) },
//            onDateChanged = { expensesViewModel.onDateChanged(it) },
//            onCategoryChanged = { expensesViewModel.onCategoryChanged(it) },
//            onConfirmPressed = {
//                expensesViewModel.onConfirmPressed()
//            },
//            onDismiss = {
//                isDialogVisible = false
//                expensesViewModel.resetExpense()
//            },
//            expenseValidState = expenseValid,
//            resetExpenseValidState = { expensesViewModel.resetExpenseValidState() }
//        )
    }
}

@Composable
fun ShowAllEditCategories(categories: List<Category>, onCategoryClick: (Category) -> Unit) {
    if (categories.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No categories!",
                color = Color(0xFF8a969c)
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            items(categories) { category ->
                ShowEditCategory(category, onCategoryClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowEditCategory(category: Category, onCategoryClick: (Category) -> Unit) {
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        onClick = { onCategoryClick(category) }
    ) {
        ListItem(
            headlineContent = {
                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    Text(text = category.name, fontSize = 18.sp)
                }
            },
            trailingContent = {
                Text(
                    text = category.maxAmount.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 20.sp
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        )
    }
}


//@Composable
//fun ShowEditCategory(category: Category) {
//    Text(text = "name: " + category.name + "Spent Amount: " + category.spentAmount + "Max Amount: " + category.maxAmount)
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoriesScreenTopBar(drawerProperties: DrawerProperties) {
    TopAppBar(
        title = { Text(text = "Categories") },
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