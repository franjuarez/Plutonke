package com.schonke.plutonke.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.viewModels.AddExpensesViewModel

@Composable
fun EditExpenseDialog(
    title: String,
    categories: List<Category>?,
    expenseName: String,
    expensePrice: String,
    expenseDate: String,
    expenseCategory: String,
    onNameChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onConfirmPressed: () -> Unit,
    showDeleteOption: Boolean = false,
    onDeletePressed: () -> Unit = {},
    onDismiss: () -> Unit,
    expenseValidState: LoadDataState,
    resetExpenseValidState: () -> Unit
) {

    ExpenseValidation(expenseValidState, onDismiss, resetExpenseValidState)

    Dialog(onDismissRequest = onDismiss) {
        Card() {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                AddExpenseHeadlineText(title)
                AddExpenseNameField(expenseName) { onNameChanged(it) }
                AddExpensePriceField(expensePrice) { onPriceChanged(it) }
                AddExpenseDateField(expenseDate) { onDateChanged(it) }
                AddExpenseCategoryField(expenseCategory, categories) { onCategoryChanged(it) }
                AddExpenseFinalizeButtons(onDismiss, onConfirmPressed, showDeleteOption, onDeletePressed)
            }
        }
    }
}

@Composable
fun ExpenseValidation(
    expenseValidState: LoadDataState,
    onDismiss: () -> Unit,
    resetExpenseValidState: () -> Unit
) {

    val context = LocalContext.current
    when (expenseValidState) {
        is LoadDataState.Loading -> {}
        is LoadDataState.Success -> {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Expense added", Toast.LENGTH_SHORT).show()
            }
            onDismiss()
        }

        is LoadDataState.Error -> {
            Toast.makeText(context, "Invalid expense!!", Toast.LENGTH_SHORT).show()
            resetExpenseValidState()
        }
    }
}
@Composable
private fun AddExpenseFinalizeButtons(
        onDismiss: () -> Unit, onConfirm: () -> Unit,
        showDeleteOption: Boolean = false,
        onDeletePressed: () -> Unit)
    {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        AddExpenseDismissButton(onDismiss)
        AddExpenseConfirmButton(onConfirm)
        if(showDeleteOption){
            AddExpenseDeleteButton(onDeletePressed)
        }
    }
}

@Composable
fun AddExpenseDeleteButton(onDeletePressed: () -> Unit) {
    TextButton(
        onClick = {
            onDeletePressed()
        },
        modifier = Modifier.padding(8.dp),
    ) {
        Text("Delete", color = Color.Red)
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
@OptIn(ExperimentalMaterial3Api::class)
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
private fun AddExpenseHeadlineText(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(vertical = 10.dp)
    )
}