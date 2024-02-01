package com.schonke.plutonke.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schonke.plutonke.screens.AllExpensesScreen
import com.schonke.plutonke.screens.ConfigurationScreen
import com.schonke.plutonke.screens.HomeScreen
import com.schonke.plutonke.viewModels.AddExpensesViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Drawer(navController = navController) { navController, drawerProperties ->
        NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route) {
            composable(route = AppScreens.HomeScreen.route) {
                HomeScreen(navController = navController, drawerProperties = drawerProperties, addExpensesViewModel = AddExpensesViewModel())
            }

            composable(route = AppScreens.AllExpensesScreen.route) {
                AllExpensesScreen(navController = navController, drawerProperties = drawerProperties)
            }

            composable(route = AppScreens.ConfigurationScreen.route){
                ConfigurationScreen(navController = navController, drawerProperties = drawerProperties)
            }
        }
    }
}
