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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.schonke.plutonke.states.LoadDataState

const val CATEGORY_NAME_FIELD_ERR = "name"
const val CATEGORY_MAX_AMOUNT_FIELD_ERR = "maxAmount"
const val CATEGORY_HAS_EXPENSES_ERR = "category has expenses"

@Composable
fun AddOrEditCategoryDialog(
    title: String,
    categoryName: String,
    categoryMaxAmount: String,
    onNameChanged: (String) -> Unit,
    onMaxAmountChanged: (String) -> Unit,
    onConfirmPressed: () -> Unit,
    showDeleteOption: Boolean = false,
    onDeletePressed: () -> Unit = {},
    onDismiss: () -> Unit,
    categoryValidState: LoadDataState,
    resetCategoryValidState: () -> Unit
) {

    val nameError = remember { mutableStateOf("") }
    val maxAmountError = remember { mutableStateOf("") }

    CategoryValidation(
        categoryValidState,
        onDismiss,
        resetCategoryValidState,
        nameError,
        maxAmountError,
    )

    Dialog(onDismissRequest = onDismiss) {
        Card() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AddOrEditCategoryHeadlineText(title)
                AddCategoryNameField(categoryName, { onNameChanged(it) }, nameError)
                AddCategoryMaxAmountField(categoryMaxAmount, { onMaxAmountChanged(it) }, maxAmountError)
                AddCategoryFinalizeButtons(
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
fun CategoryValidation(
    categoryValidState: LoadDataState,
    onDismiss: () -> Unit,
    resetCategoryValidState: () -> Unit,
    nameError: MutableState<String>,
    maxAmountError: MutableState<String>,
) {

    val context = LocalContext.current
    when (categoryValidState) {
        is LoadDataState.Success -> {
            Toast.makeText(context, categoryValidState.msg, Toast.LENGTH_SHORT).show()
            onDismiss()
        }

        is LoadDataState.Error -> {
            Toast.makeText(context, categoryValidState.msg, Toast.LENGTH_SHORT).show()
            resetCategoryValidState()
        }

        is LoadDataState.ErrorValidating -> {
            nameError.value = ""
            maxAmountError.value = ""

            categoryValidState.errors.forEach { error ->
                when (error.field) {
                    CATEGORY_NAME_FIELD_ERR -> nameError.value = error.message
                    CATEGORY_MAX_AMOUNT_FIELD_ERR -> maxAmountError.value = error.message
                    CATEGORY_HAS_EXPENSES_ERR -> Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        is LoadDataState.Loading -> {}
    }
}

@Composable
private fun AddCategoryFinalizeButtons(
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
                AddCategoryDeleteButton(onDeletePressed)
            } else {
                Spacer(modifier = Modifier.matchParentSize())
            }
        }
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            AddCategoryDismissButton(onDismiss)
            AddCategoryConfirmButton(onConfirm)
        }
    }
}

@Composable
fun AddCategoryDeleteButton(onDeletePressed: () -> Unit) {
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
private fun AddCategoryConfirmButton(onConfirm: () -> Unit) {
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
private fun AddCategoryDismissButton(onDismiss: () -> Unit) {
    TextButton(
        onClick = { onDismiss() },
        modifier = Modifier.padding(8.dp),
    ) {
        Text("Dismiss")
    }
}

@Composable
private fun AddCategoryMaxAmountField(
    categoryMaxAmount: String,
    onValueChange: (String) -> Unit,
    maxAmountError: MutableState<String>
) {
    Column {
        OutlinedTextField(
            value = categoryMaxAmount,
            onValueChange = { onValueChange(it) },
            label = { Text("Max amount $") },
            isError = maxAmountError.value.isNotEmpty(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OnErrorShowMessage(maxAmountError)
    }
}

@Composable
fun AddCategoryNameField(
    categoryName: String,
    onNameChanged: (String) -> Unit,
    nameError: MutableState<String>
) {
    Column {
        OutlinedTextField(
            value = categoryName,
            onValueChange = { onNameChanged(it) },
            label = { Text("Name") },
            isError = nameError.value.isNotEmpty()
        )
        OnErrorShowMessage(nameError)
    }
}

@Composable
private fun OnErrorShowMessage(error: MutableState<String>) {
    if (error.value.isNotEmpty()) {
        Text(
            text = error.value,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 2.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

@Composable
private fun AddOrEditCategoryHeadlineText(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(vertical = 10.dp)
    )
}