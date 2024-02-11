package com.schonke.plutonke.navigation

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schonke.plutonke.screens.AllExpensesScreen
import com.schonke.plutonke.screens.ConfigurationScreen
import com.schonke.plutonke.screens.HomeScreen
import com.schonke.plutonke.states.LoadMainDataState
import com.schonke.plutonke.viewModels.AddExpensesViewModel
import com.schonke.plutonke.viewModels.AllExpensesScreenViewModel
import com.schonke.plutonke.viewModels.HomeScreenViewModel
import com.schonke.plutonke.viewModels.SharedDataViewModel

@Composable
fun AppNavigation() {
    val dataViewModel = viewModel<SharedDataViewModel>()
    val data by dataViewModel.loadingState.collectAsState()

    when(data){
        is LoadMainDataState.Loading -> LinearProgressIndicator()
        is LoadMainDataState.Success -> { DrawerNavigation(dataViewModel = dataViewModel) }
        is LoadMainDataState.Error -> Text(text = "Error: ${(data as LoadMainDataState.Error).msg}")
    }

}

@Composable
private fun DrawerNavigation(dataViewModel: SharedDataViewModel) {
    val navHostController = rememberNavController()

    Drawer(navController = navHostController) { navController, drawerProperties ->
        NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route) {
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
