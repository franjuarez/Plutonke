package com.schonke.plutonke.navigation

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schonke.plutonke.screens.AllExpensesScreen
import com.schonke.plutonke.screens.ConfigurationScreen
import com.schonke.plutonke.screens.HomeScreen
import com.schonke.plutonke.viewModels.AddExpensesViewModel
import com.schonke.plutonke.viewModels.AllExpensesScreenViewModel
import com.schonke.plutonke.viewModels.HomeScreenViewModel
import com.schonke.plutonke.viewModels.SharedDataViewModel

@Composable
fun AppNavigation() {
    val dataViewModel = viewModel<SharedDataViewModel>()

    val isLoading by dataViewModel.isLoading.observeAsState(initial = true)

    if (isLoading) {
        LinearProgressIndicator()
    } else {
        DrawerNavigation(dataViewModel = dataViewModel)
    }
}

@Composable
private fun DrawerNavigation(dataViewModel: SharedDataViewModel) {
    val navHostController = rememberNavController()

    Drawer(navController = navHostController) { navController, drawerProperties ->
        NavHost(navController = navController, startDestination = AppScreens.AllExpensesScreen.route) {
                composable(route = AppScreens.HomeScreen.route) {
                    HomeScreen(
                        navController = navController,
                        drawerProperties = drawerProperties,
                        homeScreenViewModel = HomeScreenViewModel(dataViewModel),
                        addExpensesViewModel = AddExpensesViewModel(dataViewModel)
                    )
                }

                composable(route = AppScreens.AllExpensesScreen.route) {
                    AllExpensesScreen(
                        navController = navController,
                        drawerProperties = drawerProperties,
                        allExpensesScreenViewModel = AllExpensesScreenViewModel(dataViewModel)
                    )
                }

                composable(route = AppScreens.ConfigurationScreen.route) {
                    ConfigurationScreen(
                        navController = navController,
                        drawerProperties = drawerProperties
                    )
                }
            }
        }
    }
