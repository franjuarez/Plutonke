package com.schonke.plutonke.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.schonke.plutonke.navigation.DrawerProperties
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.viewModels.CategoriesScreenViewModel
import com.schonke.plutonke.viewModels.CategoriesViewModel
import kotlinx.coroutines.launch

@Composable
fun EditCategoriesScreen(
    navController: NavController,
    drawerProperties: DrawerProperties,
    categoriesScreenViewModel: CategoriesScreenViewModel,
    categoriesViewModel: CategoriesViewModel
) {

    val categories = categoriesScreenViewModel.sharedCategories.value
    val dataUpdated by categoriesScreenViewModel.dataUpdated.observeAsState()
    if (dataUpdated == true) { /*For recomposition*/}

    var isEditCategoryDialogVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(topBar = { EditCategoriesScreenTopBar(drawerProperties) },
        floatingActionButton = { EditCategoriesAddCategoryButton(categoriesViewModel) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ShowAllCategories(categories ?: emptyList()) { clickedCategory ->
                selectedCategory = clickedCategory
                categoriesViewModel.resetCategoryValidState()
                isEditCategoryDialogVisible = true
                categoriesViewModel.onNameChanged(clickedCategory.name)
                categoriesViewModel.onMaxAmountChanged(clickedCategory.maxAmount.toString())
            }
            if (isEditCategoryDialogVisible) {
                EditCategoryOnClickDialog(
                    categoriesViewModel = categoriesViewModel,
                    selectedCategory = selectedCategory!!,
                    onDismiss = { isEditCategoryDialogVisible = false }
                )
            }
        }
    }
}

@Composable
fun EditCategoriesAddCategoryButton(categoriesViewModel: CategoriesViewModel) {
    var isAddCategoryDialogVisible by remember { mutableStateOf(false) }

    ExtendedFloatingActionButton(
        text = { Text("Add a Category") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add a Category") },
        onClick = { isAddCategoryDialogVisible = true }
    )

    val categoryName: String by categoriesViewModel.categoryName.observeAsState(initial = "")
    val categoryMaxAmount: String by categoriesViewModel.categoryMaxAmount.observeAsState(initial = "")
    val categoryValid by categoriesViewModel.categoryValidState.collectAsState()

    if (isAddCategoryDialogVisible) {

        AddOrEditCategoryDialog(
            title = "Add a category",
            categoryName = categoryName,
            categoryMaxAmount = categoryMaxAmount,
            onNameChanged = { categoriesViewModel.onNameChanged(it) },
            onMaxAmountChanged = { categoriesViewModel.onMaxAmountChanged(it) },
            onConfirmPressed = { categoriesViewModel.onConfirmPressed() },
            onDismiss = { isAddCategoryDialogVisible = false; categoriesViewModel.resetCategory() },
            categoryValidState = categoryValid,
            resetCategoryValidState = { categoriesViewModel.resetCategoryValidState() }
        )
    }
}

@Composable
private fun ShowAllCategories(categories: List<Category>, onCategoryClick: (Category) -> Unit) {
    if (categories.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No categories!",
                color = Color(0xFF8a969c)
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            items(categories) { category ->
                ShowCategory(category, onCategoryClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowCategory(category: Category, onCategoryClick: (Category) -> Unit) {
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        onClick = { onCategoryClick(category) }
    ) {
        ListItem(
            headlineContent = {
                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    Text(text = category.name, fontSize = 18.sp)
                }
            },
            trailingContent = {
                Text(
                    text = category.maxAmount.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 20.sp
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCategoriesScreenTopBar(drawerProperties: DrawerProperties) {
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

@Composable
fun EditCategoryOnClickDialog(
    categoriesViewModel: CategoriesViewModel,
    selectedCategory: Category,
    onDismiss: () -> Unit
) {
    val categoryName: String by categoriesViewModel.categoryName.observeAsState(initial = selectedCategory.name)
    val categoryMaxAmount: String by categoriesViewModel.categoryMaxAmount.observeAsState(initial = selectedCategory.maxAmount.toString())

    val categoryValid by categoriesViewModel.categoryValidState.collectAsState()

    AddOrEditCategoryDialog(
        title = "Edit a Category",
        categoryName = categoryName,
        categoryMaxAmount = categoryMaxAmount,
        onNameChanged = { categoriesViewModel.onNameChanged(it) },
        onMaxAmountChanged = { categoriesViewModel.onMaxAmountChanged(it) },
        onConfirmPressed = { categoriesViewModel.onModifiedPressed(selectedCategory.id);  onDismiss() },
        onDismiss = { onDismiss() },
        categoryValidState = categoryValid,
        showDeleteOption = true,
        onDeletePressed = { categoriesViewModel.onDeletePressed(selectedCategory.id) },
        resetCategoryValidState = { categoriesViewModel.resetCategoryValidState() }
    )
}