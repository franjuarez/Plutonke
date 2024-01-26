package com.schonke.plutonke.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schonke.plutonke.screens.HomeScreen
import com.schonke.plutonke.screens.allExpensesScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route) {
        composable(route = AppScreens.HomeScreen.route) { HomeScreen(navController = navController) }
        composable(route = AppScreens.AllExpensesScreen.route) { allExpensesScreen(navController = navController) }
    }
}