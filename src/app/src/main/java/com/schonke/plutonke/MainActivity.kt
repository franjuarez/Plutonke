package com.schonke.plutonke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val expense = Expense("Mac", "01-01-2024", 234, "Pinga")
        setContent {
            PlutonkeTheme {
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("YA VOLVI")
//                }
                Expense(expense)
                }
            }
        }
    }

@Composable
fun Expense(expense: Expense, modifier: Modifier = Modifier){
    //TODO Cambiar category por su tipo

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .size(width = 240.dp, height = 100.dp)
            ) {
                Text(
                    text = "Filled",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        ElevatedCard(
                 elevation = CardDefaults.cardElevation(
                     defaultElevation = 6.dp
                 ),
                 modifier = Modifier
                     .size(width = 240.dp, height = 100.dp)
             ) {
                 Text(
                     text = "Elevated",
                     modifier = Modifier
                         .padding(16.dp),
                     textAlign = TextAlign.Center,
                 )
             }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,


    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlutonkeTheme {
        Greeting("Android")
    }
}