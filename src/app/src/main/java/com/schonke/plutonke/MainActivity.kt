package com.schonke.plutonke

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.schonke.plutonke.ui.theme.PlutonkeTheme
import androidx.compose.material3.Scaffold
import com.schonke.plutonke.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlutonkeTheme {
                Scaffold {
                    AppNavigation()
                }
            }
        }
    }
}




