package com.schonke.plutonke

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.schonke.plutonke.ui.theme.PlutonkeTheme
import androidx.lifecycle.lifecycleScope
import com.schonke.plutonke.data.BackendFactory
import com.schonke.plutonke.navigation.AppNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backend = BackendFactory.makeBackend()

        lifecycleScope.launch(Dispatchers.IO) {
            val call = backend.getAllExpenses()
            val expenses : List<Expense>? = call.body()
            if (!call.isSuccessful) {
                println("Error! Can't connect to backend")
            }
        }

        setContent {
            PlutonkeTheme {
                Surface (modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                    ) {
                    AppNavigation()
                }
            }
        }
    }
}



