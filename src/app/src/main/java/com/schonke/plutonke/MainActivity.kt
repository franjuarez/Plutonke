package com.schonke.plutonke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schonke.plutonke.ui.theme.PlutonkeTheme
import kotlin.math.exp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val expenses = listOf<Expense>(Expense("Mac", "01-01-2024", 234, "Pinga"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Valen", "01-02-2021", 534, "More"),
            Expense("Monke", "12-01-2004", 89434, "Vato"))

        setContent {
            PlutonkeTheme {
                showAllExpenses(expenses = expenses)
                }
            }
        }
    }

@Composable
fun showAllExpenses(expenses: List<Expense>){
    LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(expenses){ expense ->
            showExpense(expense = expense)

        }
    }
}

@Composable
fun showExpense(expense: Expense){
    //TODO Cambiar category por su tipo
    ListItem(
        headlineContent = { Text(text = expense.name) },
        supportingContent = { Text(text = "Category: " + expense.category) },
        trailingContent = { Text(text = "$" + expense.price.toString()) }
    )
}
