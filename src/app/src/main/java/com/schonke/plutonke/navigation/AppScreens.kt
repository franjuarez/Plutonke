package com.schonke.plutonke.navigation

sealed class AppScreens(val route: String) {
    object AllExpensesScreen: AppScreens("all_expenses_screen")
    object HomeScreen: AppScreens("home_screen")
    object ConfigurationScreen: AppScreens("configuration_screen")

    object CategoriesScreen: AppScreens("categories_screen")
}