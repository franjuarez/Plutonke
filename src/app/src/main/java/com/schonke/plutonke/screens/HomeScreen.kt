@file:OptIn(ExperimentalMaterial3Api::class)

package com.schonke.plutonke.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

import com.schonke.plutonke.Category
import com.schonke.plutonke.Expense
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.viewModels.AddExpensesViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun HomeScreen(navController: NavController, drawerProperties: DrawerProperties, addExpensesViewModel: AddExpensesViewModel) {
    val comida = Category(name = "a", maxAmount = 50000)
    val diversion = Category(name = "Diversionaaaaaaa", maxAmount = 150000)
    val salidas = Category(name = "Salidas", maxAmount = 200000)
    val cumpleanios_de_urko = Category(name = "Cumples de Urko", maxAmount = 999999)
    val otra_categoria = Category(name = "otra categoria", maxAmount = 10000)
    val steam = Category(name = "steam", maxAmount = 25000)


    val expenses = listOf<Expense>(
        Expense("1", "Mac", "01-01-2024", 234, comida),
        Expense("1", "Minecraft Premium", "01-03-2021", 20000, diversion),
        Expense("1", "Parque de la costa", "01-02-2021", 90000, diversion),
        Expense("1", "Cumple de Monke", "12-01-2004", 10000, salidas)
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
        topBar = { HomeScreenTopBar(drawerProperties = drawerProperties) },
        floatingActionButton = { HomeScreenAddExpenseButton(addExpensesViewModel) }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenAddExpenseButton(addExpensesViewModel: AddExpensesViewModel) {
    var isDialogVisible by remember { mutableStateOf(false) } //TODO: ver si agregar al viewmodel o no

    ExtendedFloatingActionButton(
        text = { Text("Add an expense") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add an expense") },
        onClick = { isDialogVisible = true }
    )
    AddExpenseDialog(addExpensesViewModel, isDialogVisible, onDismiss = { isDialogVisible = false })
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpenseDialog(addExpensesViewModel: AddExpensesViewModel, isDialogVisible: Boolean, onDismiss: () -> Unit) {

    val expenseName :String by addExpensesViewModel.expenseName.observeAsState(initial = "")
    val expenseDate :String by addExpensesViewModel.expenseDate.observeAsState(initial = "")
    val expensePrice :String by addExpensesViewModel.expensePrice.observeAsState(initial = "")
    val expenseCategory :String by addExpensesViewModel.expenseCategory.observeAsState(initial = "")

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
                    AddExpenseCategoryField(expenseCategory) {
                        addExpensesViewModel.onCategoryChanged(
                            it
                        )
                    }
                    AddExpenseFinalizeButtons(onDismiss) {
                        if(addExpensesViewModel.onConfirmPressed()){
                            onDismiss()
                        }
                    }
                }
            }
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
private fun AddExpenseCategoryField(expenseCategory: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = expenseCategory,
        onValueChange = { onValueChange(it) },
        label = { Text("Category") }
    )
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
    Column(){
        CategoriesHeadlineText(Modifier.align(alignment = Alignment.CenterHorizontally))
        LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories){ category ->
                ShowCategory(category = category)
            }
        }
    }
}

@Composable
private fun CategoriesHeadlineText(modifier: Modifier) {
    Text(
        text = "Spent by Categories", modifier = modifier
            .padding(vertical = 4.dp)
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