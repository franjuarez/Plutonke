package com.schonke.plutonke

import android.os.Bundle
import android.preference.PreferenceActivity
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val comida = Category(name = "Comida", spentAmount = 234, maxAmount = 50000)
        val diversion = Category(name = "Diversion", spentAmount = 110000, maxAmount = 150000)
        val salidas = Category(name = "Salidas", spentAmount = 10000, maxAmount = 200000)
        
        val expenses = listOf<Expense>(Expense("Mac", "01-01-2024", 234, comida),
            Expense("Minecraft Premium", "01-03-2021", 20000, diversion),
            Expense("Parque de la costa", "01-02-2021", 90000, diversion),
            Expense("Cumple de Monke", "12-01-2004", 10000, salidas))
    
        val categories = listOf<Category>(
                comida,
                diversion,
                salidas
        )
        
        setContent {
            PlutonkeTheme {
                Surface() {
                    ShowHomeScreen(categories = categories)

                }
                }
            }
        }
    }


@Composable
fun ShowHomeScreen(categories: List<Category>) {
    LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(categories){ category ->
            ShowCategory(category = category)

        }
    }
}
@Composable
fun ShowCategory(category: Category) {
    ElevatedButton(onClick = { println("Click") },
            modifier = Modifier.fillMaxSize()) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category.toString() , modifier = Modifier.weight(1f))
            LinearProgressIndicator(progress = category.spentAmount / category.maxAmount.toFloat(),
                    modifier = Modifier.size(150.dp, 30.dp)
            )

            Text(category.spentAmount.toString() + "/" + category.maxAmount.toString())


        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowAllExpenses(expenses: List<Expense>){
    LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(expenses){ expense ->
            ShowExpense(expense = expense)
            Divider()
        }
    }
}

@Composable
fun ShowExpense(expense: Expense){
    //TODO Cambiar category por su tipo
    ListItem(
        headlineContent = { Text(text = expense.name) },
        supportingContent = { Text(text = expense.category.toString()) },
        trailingContent = { Text(text = "$" + expense.price.toString()) },
            )
}
