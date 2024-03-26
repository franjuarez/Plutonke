package com.schonke.plutonke.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.viewModels.CategoriesScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoriesScreen(
    navController: NavController,
    drawerProperties: DrawerProperties,
    categoriesScreenViewModel: CategoriesScreenViewModel
) {

    val categories = categoriesScreenViewModel.sharedCategories.value
    val dataUpdated by categoriesScreenViewModel.dataUpdated.observeAsState()
    if (dataUpdated == true) {/*For recomposition*/}

    Scaffold (topBar = { CategoriesScreenTopBar(drawerProperties) }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column {
                categories?.forEach { category ->
                    ShowEditCategory(category)
                }
            }
        }
    }
}

@Composable
fun ShowEditCategory(category: Category) {
    Text(text = "name: " + category.name + "Spent Amount: " + category.spentAmount + "Max Amount: " + category.maxAmount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreenTopBar(drawerProperties: DrawerProperties) {
    TopAppBar(
        title = { Text(text = "Categories") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        navigationIcon = {
            IconButton(onClick = {
                drawerProperties.scope.launch {
                    drawerProperties.state.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }
    )
}