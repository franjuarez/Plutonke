package com.schonke.plutonke.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schonke.plutonke.screens.HomeScreen
import com.schonke.plutonke.screens.allExpensesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Drawer(navController = navController)
}