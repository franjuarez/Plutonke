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
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.viewModels.AllExpensesScreenViewModel
import com.schonke.plutonke.viewModels.ExpensesViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllExpensesScreen(
    navController: NavController,
    drawerProperties: DrawerProperties,
    allExpensesScreenViewModel: AllExpensesScreenViewModel,
    expensesViewModel: ExpensesViewModel
) {
    val expenses = allExpensesScreenViewModel.sharedExpenses.value
    val categories = allExpensesScreenViewModel.sharedCategories.value

    val dataUpdated by allExpensesScreenViewModel.dataUpdated.observeAsState()

    if (dataUpdated == true) {/*For recomposition*/
    }

    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }
    Scaffold(topBar = { AllExpensesScreenTopBar(drawerProperties = drawerProperties) }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isDialogVisible) {
                EditExpenseOnClickDialog(
                    expensesViewModel,
                    selectedExpense!!,
                    onDismiss = {
                        isDialogVisible = false
                        expensesViewModel.resetExpense()
                    },
                    categories ?: emptyList()
                )
            }
            ShowAllExpenses(expenses ?: emptyList()) { clickedExpense ->
                selectedExpense = clickedExpense
                isDialogVisible = true
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreenTopBar(drawerProperties: DrawerProperties) {
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
fun EditExpenseOnClickDialog(
    expensesViewModel: ExpensesViewModel,
    selectedExpense: Expense,
    onDismiss: () -> Unit,
    categories: List<Category>
) {
    val expenseDate: String by expensesViewModel.expenseDate.observeAsState(initial = "")
    val expenseName: String by expensesViewModel.expenseName.observeAsState(initial = "")
    val expensePrice: String by expensesViewModel.expensePrice.observeAsState(initial = "")
    val expenseCategory: UInt by expensesViewModel.expenseCategoryID.observeAsState(initial = 0U)

    expensesViewModel.onNameChanged(expenseName)
    expensesViewModel.onDateChanged(expenseDate)
    expensesViewModel.onPriceChanged(expensePrice)
    expensesViewModel.onCategoryChanged(expenseCategory) //TODO: ver pq pinga anda
//    expensesViewModel.onNameChanged(selectedExpense.name)
//    expensesViewModel.onDateChanged(selectedExpense.date)
//    expensesViewModel.onPriceChanged(selectedExpense.price)
//    expensesViewModel.onCategoryChanged(selectedExpense.category)

    val expenseValid by expensesViewModel.expenseValidState.collectAsState()

    EditExpenseDialog(
        title = "Edit an expense",
        categories = categories,
        expenseName = expenseName,
        expensePrice = expensePrice,
        expenseDate = expenseDate,
        expenseCategoryID = expenseCategory,
        onNameChanged = { expensesViewModel.onNameChanged(it) },
        onPriceChanged = { expensesViewModel.onPriceChanged(it) },
        onDateChanged = { expensesViewModel.onDateChanged(it) },
        onCategoryChanged = { expensesViewModel.onCategoryChanged(it) },
        onConfirmPressed = {
            expensesViewModel.onModifiedPressed(selectedExpense.id)
            onDismiss()
        },
        showDeleteOption = true,
        onDeletePressed = { expensesViewModel.onDeletePressed(selectedExpense.id) },
        onDismiss = { onDismiss() },
        expenseValidState = expenseValid,
        resetExpenseValidState = { expensesViewModel.resetExpenseValidState() }
    )
}

@Composable
fun ShowAllExpenses(expenses: List<Expense>, onExpenseClick: (Expense) -> Unit) {
    if (expenses.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No expenses!",
                color = Color(0xFF8a969c)
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            items(expenses) { expense ->
                ShowExpense(expense, onExpenseClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowExpense(expense: Expense, onExpenseClick: (Expense) -> Unit) {
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
        elevation = CardDefaults.cardElevation(defaultElevation =  5.dp),
        onClick = { onExpenseClick(expense) }
    ) {
        ListItem(
            headlineContent = {
                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    Text(text = expense.name, fontSize =  18.sp)
                }
            },
            supportingContent = {
                Text(
                    text = expense.categoryID.toString(), //TODO: necesitamos categories
                    modifier = Modifier.wrapContentSize(),
                    fontSize =  14.sp, fontWeight = FontWeight.ExtraBold
                )
            },
            trailingContent = {
                Column {
                    Text(
                        text = "$" + expense.price.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.wrapContentSize(),
                        fontSize =  20.sp
                    )
                    Text(text = expense.date, fontSize =  14.sp)
                }
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        )
    }
}

