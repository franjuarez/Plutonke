package com.schonke.plutonke.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
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

@Composable
fun EditExpenseDialog(
    title: String,
    categories: List<Category>?,
    expenseName: String,
    expensePrice: String,
    expenseDate: String,
    expenseCategoryID: UInt,
    onNameChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onCategoryChanged: (UInt) -> Unit,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AddExpenseHeadlineText(title)
                AddExpenseNameField(expenseName) { onNameChanged(it) }
                AddExpensePriceField(expensePrice) { onPriceChanged(it) }
                AddExpenseDateField(expenseDate) { onDateChanged(it) }
                AddExpenseCategoryField(expenseCategoryID, categories) { onCategoryChanged(it) }
                AddExpenseFinalizeButtons(
                    onDismiss,
                    onConfirmPressed,
                    showDeleteOption,
                    onDeletePressed
                )
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
            Toast.makeText(context, expenseValidState.msg, Toast.LENGTH_SHORT).show()
            onDismiss()
        }

        is LoadDataState.Error -> {
            Toast.makeText(context, expenseValidState.msg, Toast.LENGTH_SHORT).show()
            resetExpenseValidState()
        }
    }
}

@Composable
private fun AddExpenseFinalizeButtons(
    onDismiss: () -> Unit, onConfirm: () -> Unit,
    showDeleteOption: Boolean = false,
    onDeletePressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(IntrinsicSize.Min)) {
            if (showDeleteOption) {
                AddExpenseDeleteButton(onDeletePressed)
            } else {
                Spacer(modifier = Modifier.matchParentSize())
            }
        }
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            AddExpenseDismissButton(onDismiss)
            AddExpenseConfirmButton(onConfirm)
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
private fun AddExpenseCategoryField(
    expenseCategoryID: UInt,
    categories: List<Category>?,
    onValueChange: (UInt) -> Unit
) {
    var currentCategory by remember { mutableStateOf(
            if (expenseCategoryID == 0U) "Category"
            else categories?.find { it.id == expenseCategoryID })
    }
    var isExpanded by remember { mutableStateOf(false) }
    Box() {
        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            TextField(
                value = currentCategory.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                categories?.forEach { category ->
                    var categoryName = category.name
                    DropdownMenuItem(text = { Text(text = categoryName) },
                        onClick = {
                            currentCategory = categoryName
                            onValueChange(category.id)
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