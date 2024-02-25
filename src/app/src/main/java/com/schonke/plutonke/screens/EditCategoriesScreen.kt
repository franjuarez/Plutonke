package com.schonke.plutonke.screens

import com.schonke.plutonke.viewModels.CategoriesViewModel

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.schonke.plutonke.viewModels.AllExpensesScreenViewModel
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import kotlin.math.round

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditCategoriesScreen(
    navController: NavController,
    drawerProperties: DrawerProperties,
    allExpensesScreenViewModel: AllExpensesScreenViewModel,
    categoriesViewModel: CategoriesViewModel
) {

    val categories = allExpensesScreenViewModel.sharedCategories.value

    val dataUpdated by allExpensesScreenViewModel.dataUpdated.observeAsState()

    if (dataUpdated == true) {/*For recomposition*/
    }

    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    Scaffold(topBar = { EditCategoriesScreenTopBar(drawerProperties = drawerProperties) }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isDialogVisible) {
                EditCategoryOnClickDialog(
                    categoriesViewModel,
                    selectedCategory!!,
                    onDismiss = {
                        isDialogVisible = false
                        categoriesViewModel.resetExpense()
                    },
                    categories ?: emptyList()
                )
            }
            ShowAllCategories(categories ?: emptyList()) { clickedCategory ->
                selectedCategory = clickedCategory
                isDialogVisible = true
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoriesScreenTopBar(drawerProperties: DrawerProperties) {
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
            }
        }
    )

}

@Composable
fun EditCategoryOnClickDialog(
    categoriesViewModel: CategoriesViewModel,
    selectedExpense: Category,
    onDismiss: () -> Unit,
    categories: List<Category>
) {
    val expenseDate: String by categoriesViewModel.categoryName.observeAsState(initial = "")
    val expenseName: String by categoriesViewModel.categorySpentAmount.observeAsState(initial = "")
    val expensePrice: String by categoriesViewModel.categoryMaxAmount.observeAsState(initial = "")

    categoriesViewModel.onNameChanged(expenseName)
    categoriesViewModel.onSpentAmountChanged(expenseDate)
    categoriesViewModel.onMaxAmountChanged(expensePrice)

    val expenseValid by categoriesViewModel.categoryValidState.collectAsState()

//    EditExpenseDialog(
//        title = "Edit an expense",
//        categories = categories,
//        expenseName = expenseName,
//        expensePrice = expensePrice,
//        expenseDate = expenseDate,
//
//        onNameChanged = { categoriesViewModel.onNameChanged(it) },
//        onPriceChanged = { categoriesViewModel.onPriceChanged(it) },
//        onDateChanged = { categoriesViewModel.onDateChanged(it) },
//        onCategoryChanged = { categoriesViewModel.onCategoryChanged(it) },
//        onConfirmPressed = {
//            categoriesViewModel.onModifiedPressed(selectedExpense.id) //Todo: cambiar a edit
//            onDismiss()
//        },
//        showDeleteOption = true,
//        onDeletePressed = { categoriesViewModel.onDeletePressed(selectedExpense.id) },
//        onDismiss = { onDismiss() },
//        expenseValidState = expenseValid,
//        resetExpenseValidState = { categoriesViewModel.resetExpenseValidState() }
//    )
}

@Composable
fun ShowAllCategories(categories: List<Category>, onCategoryClick: (Category) -> Unit) {
    if (categories.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No categories yet! Tap to create.",
                color = Color(0xFF8a969c)
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            items(categories) { expense ->
                ShowCategory(expense, onCategoryClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowCategory(category: Category, onCategoryClick: (Category) -> Unit) {
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp), // Agrega padding vertical para hacer más grande el Card
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        onClick = { onCategoryClick(category) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.name, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(50.dp))
            Box () {
                CircularProgressIndicator(
                    progress = category.spentAmount / category.maxAmount,
                    modifier = Modifier.width(50.dp)
                )
                Text(
                    text = "%" + (round((category.spentAmount / category.maxAmount)*100)).toString(),
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                )
            }


            Spacer(modifier = Modifier.width(30.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$" + category.spentAmount.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,

                    fontSize = 15.sp
                )
                Text(
                    text = "$" + category.maxAmount.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,

                    fontSize = 14.sp
                )
            }
        }

    }

}