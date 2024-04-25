package com.schonke.plutonke.navigation

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schonke.plutonke.screens.AllExpensesScreen
import com.schonke.plutonke.screens.EditCategoriesScreen
import com.schonke.plutonke.screens.ConfigurationScreen
import com.schonke.plutonke.screens.HomeScreen
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.viewModels.ExpensesViewModel
import com.schonke.plutonke.viewModels.AllExpensesScreenViewModel
import com.schonke.plutonke.viewModels.CategoriesScreenViewModel
import com.schonke.plutonke.viewModels.CategoriesViewModel
import com.schonke.plutonke.viewModels.HomeScreenViewModel
import com.schonke.plutonke.viewModels.SharedDataViewModel

@Composable
fun AppNavigation() {
    val dataViewModel = viewModel<SharedDataViewModel>()
    val data by dataViewModel.loadingState.collectAsState()

    when (data) {
        is LoadDataState.Loading -> LinearProgressIndicator()
        is LoadDataState.Success -> {
            DrawerNavigation(dataViewModel = dataViewModel)
        }

        is LoadDataState.Error -> Text(text = "Error: ${(data as LoadDataState.Error).msg}")
        is LoadDataState.ErrorValidating -> Text(text = "VALIDATION Error: ${(data as LoadDataState.Error).msg}")
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
                    expensesViewModel = ExpensesViewModel(dataViewModel)
                )
            }

            composable(route = AppScreens.AllExpensesScreen.route) {
                AllExpensesScreen(
                    navController = navController,
                    drawerProperties = drawerProperties,
                    allExpensesScreenViewModel = AllExpensesScreenViewModel(dataViewModel),
                    expensesViewModel = ExpensesViewModel(dataViewModel)
                )
            }

            composable(route = AppScreens.ConfigurationScreen.route) {
                ConfigurationScreen(
                    navController = navController,
                    drawerProperties = drawerProperties
                )
            }

            composable(route = AppScreens.CategoriesScreen.route) {
                EditCategoriesScreen(
                    navController = navController,
                    drawerProperties = drawerProperties,
                    categoriesScreenViewModel = CategoriesScreenViewModel(dataViewModel),
                    categoriesViewModel = CategoriesViewModel(dataViewModel)
                )
            }
        }
    }
}
