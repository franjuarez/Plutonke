package com.schonke.plutonke

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.schonke.plutonke.ui.theme.Purple40
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val expenses = listOf<Expense>(Expense("Mac", "01-01-2024", 234, "Pinga"),
            Expense("Comida", "01-02-2021", 534, "More"),
            Expense("Compu", "01-02-2021", 534, "More"),
            Expense("BK", "01-02-2021", 534, "More"),
            Expense("Steam", "01-02-2021", 534, "More"),
            Expense("Epic", "01-02-2021", 534, "More"),
            Expense("Coca cola", "01-02-2021", 534, "More"),
            Expense("Pepsi", "01-02-2021", 534, "More"),
            Expense("Messi", "01-02-2021", 534, "More"),
            Expense("Edward", "01-02-2021", 534, "More"),
            Expense("Anime", "01-02-2021", 534, "More"),
            Expense("Cuadro", "01-02-2021", 534, "More"),
            Expense("Haikyuu", "01-02-2021", 534, "More")
        )
        setContent {
            PlutonkeTheme {
                Scaffold {
                    showDrawer(expenses = expenses)
                }
//                showExpensesScreen(expenses = expenses)
                }
            }
        }
    }

@Composable
fun showDrawer(expenses: List<Expense>){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
        ModalDrawerSheet () {
            Text(text = "Options", modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Bold /*TODO: cambiar fuente*/)
            Divider()
            drawerHomeScreen()
            Divider()
            drawerExpensesScreen()
        }
    },
        gesturesEnabled = true
    ) {
        showExpensesScreen(expenses = expenses)
    }
}

@Composable
fun drawerHomeScreen(){
    NavigationDrawerItem(label = { Text("Home") }, selected = false, onClick = { /*TODO: change screen*/ })
}

@Composable
fun drawerExpensesScreen(){
    NavigationDrawerItem(label = {Text("All Expenses")}, selected = true, onClick = { /*TODO: change screen*/ })
}

@Composable
fun showExpensesScreen(expenses: List<Expense>){
    Column {
        topBar()
        showAllExpenses(expenses = expenses)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topBar(){
    TopAppBar(title = { Text(text = "All Expenses") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
}

@Composable
fun showAllExpenses(expenses: List<Expense>){
    LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
        items(expenses){ expense ->
            showExpense(expense = expense)

        }
    }
}

@Composable
fun showExpense(expense: Expense) {
    //TODO Cambiar category por su tipo
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        ListItem(
            headlineContent = { Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                Text(text = expense.name, fontSize = 15.sp) } },
            supportingContent = {
                Text(
                    text = expense.category.toString(),
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 12.sp, fontWeight = FontWeight.ExtraBold
                )},
            trailingContent = { Column {
                Text(
                    text = "$" + expense.price.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 18.sp
                )
                Text(text = expense.date.toString(), fontSize = 12.sp)
            }
        },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )
    }
}
