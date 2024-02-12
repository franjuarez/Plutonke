@file:OptIn(ExperimentalMaterial3Api::class)

package com.schonke.plutonke.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

import com.schonke.plutonke.types.Category
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.viewModels.AddExpensesViewModel
import com.schonke.plutonke.viewModels.HomeScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, drawerProperties: DrawerProperties, homeScreenViewModel: HomeScreenViewModel, addExpensesViewModel: AddExpensesViewModel) {
    val categories by homeScreenViewModel.sharedCategories.observeAsState()
    val dataUpdated by homeScreenViewModel.dataUpdated.observeAsState()

    if(dataUpdated == true){/*For recomposition*/ }


    Scaffold (
        topBar = { HomeScreenTopBar(drawerProperties = drawerProperties) },
        floatingActionButton = { HomeScreenAddExpenseButton(addExpensesViewModel, categories) }
    )
    {innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            ShowCategories(categories = categories ?: emptyList())
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
fun HomeScreenAddExpenseButton(addExpensesViewModel: AddExpensesViewModel, categories: List<Category>?) {
    var isDialogVisible by remember { mutableStateOf(false) }

    ExtendedFloatingActionButton(
        text = { Text("Add an expense") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add an expense") },
        onClick = { isDialogVisible = true }
    )
    AddExpenseDialog(addExpensesViewModel, categories, isDialogVisible, onDismiss = {
        isDialogVisible = false
        addExpensesViewModel.resetExpense()})
}


@Composable
fun AddExpenseDialog(addExpensesViewModel: AddExpensesViewModel,
                     categories: List<Category>?,
                     isDialogVisible: Boolean, onDismiss: () -> Unit) {

    val expenseName :String by addExpensesViewModel.expenseName.observeAsState(initial = "")
    val expenseDate :String by addExpensesViewModel.expenseDate.observeAsState(initial = "")
    val expensePrice :String by addExpensesViewModel.expensePrice.observeAsState(initial = "")
    val expenseCategory :String by addExpensesViewModel.expenseCategory.observeAsState(initial = "")

    ExpenseValidation(addExpensesViewModel, onDismiss)

    if(isDialogVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card() {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ){
                    AddExpenseHeadlineText()
                    AddExpenseNameField(expenseName) { addExpensesViewModel.onNameChanged(it) }
                    AddExpensePriceField(expensePrice) { addExpensesViewModel.onPriceChanged(it) }
                    AddExpenseDateField(expenseDate) { addExpensesViewModel.onDateChanged(it) }
                    AddExpenseCategoryField(expenseCategory, categories) {
                        addExpensesViewModel.onCategoryChanged(
                            it
                        )
                    }
                    AddExpenseFinalizeButtons(onDismiss) { addExpensesViewModel.onConfirmPressed() }
                }
            }
        }
    }
}

@Composable
private fun ExpenseValidation(
    addExpensesViewModel: AddExpensesViewModel,
    onDismiss: () -> Unit
) {
    val expenseValid by addExpensesViewModel.expenseValidState.collectAsState()

    val context = LocalContext.current
    when (expenseValid) {
        is LoadDataState.Loading -> {}
        is LoadDataState.Success -> {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Expense added", Toast.LENGTH_SHORT).show()
            }
            onDismiss()
        }

        is LoadDataState.Error -> {
            Toast.makeText(context, "Invalid expense!!", Toast.LENGTH_SHORT).show()
            addExpensesViewModel.resetExpenseValidState()
        }
    }
}

@Composable
private fun AddExpenseFinalizeButtons(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        AddExpenseDismissButton(onDismiss)
        AddExpenseConfirmButton(onConfirm)
    }
}

@Composable
private fun AddExpenseConfirmButton(onConfirm: () -> Unit) {
    TextButton(
        onClick = {
            onConfirm()
                  },
        modifier = Modifier.padding(8.dp),
    ) {
        Text("Confirm")
    }
}

@Composable
private fun AddExpenseDismissButton(onDismiss: () -> Unit) {
    TextButton(
        onClick = { onDismiss() },
        modifier = Modifier.padding(8.dp),
    ) {
        Text("Dismiss")
    }
}
@Composable
private fun AddExpenseCategoryField(expenseCategory: String, categories: List<Category>?, onValueChange: (String) -> Unit) {
    var currentCategory by remember { mutableStateOf("Category") }
    var isExpanded by remember { mutableStateOf(false) }
    Box () {
        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            TextField(value = currentCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                categories?.forEach { category ->
                    val categoryName = category.toString()
                    DropdownMenuItem(text = { Text(text = categoryName) },
                    onClick = {
                        currentCategory = categoryName
                        onValueChange(categoryName)
                        isExpanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun AddExpenseDateField(expenseDate: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = expenseDate,
        onValueChange = { onValueChange(it) },
        label = { Text("dd/mm/yyyy") },
    )
}

@Composable
private fun AddExpensePriceField(expensePrice: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = expensePrice,
        onValueChange = { onValueChange(it) },
        label = { Text("Price $") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun AddExpenseNameField(expenseName: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = expenseName,
        onValueChange = { onValueChange(it) },
        label = { Text("Name") }
    )
}

@Composable
private fun AddExpenseHeadlineText() {
    Text(
        text = "Add an expense",
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

@Composable
fun ShowCategories(categories: List<Category>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
        ) {
            CategoriesHeadlineText(Modifier.align(alignment = Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            if(categories.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "No categories",
                        color = Color(0xFF8a969c)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        ShowCategory(category = category)
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
    ElevatedButton(onClick = { /*TODO: mostrar gastos de la categoria especifica*/println("Click") },
        modifier = Modifier
            .fillMaxSize()
            .height(70.dp)) {
        CategoryData(category)
    }
}

@Composable
private fun CategoryData(category: Category) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryNameText(category, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1.2f)) {
            CategoryProgressBar(category)
            CategorySpentAmount(category, Modifier.align(alignment = Alignment.CenterHorizontally))
        }
        CategoryMaxAmount(category, Modifier.weight(1f))
    }
}

@Composable
private fun CategoryMaxAmount(category: Category, modifier: Modifier) {
    val numberColor = if (category.spentAmount > category.maxAmount) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
    Text(
        "$" + category.maxAmount.toString(),
        color = numberColor,
        modifier = modifier,
        textAlign = TextAlign.End
    )
}

@Composable
private fun CategorySpentAmount(category: Category, modifier: Modifier) {
    Text(
        text = "$" + category.spentAmount.toString(),
        modifier = modifier.padding(top = 6.dp),
        fontSize = 12.sp
    )
}

@Composable
private fun CategoryProgressBar(category: Category) {
    LinearProgressIndicator(
        progress = category.spentAmount / category.maxAmount.toFloat(),
        modifier = Modifier
            .height(25.dp)
            .padding(top = 6.dp)
    )
}

@Composable
private fun CategoryNameText(category: Category, modifier: Modifier) {
    Text(category.toString(), modifier = modifier)
}