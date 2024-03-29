package com.schonke.plutonke.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.schonke.plutonke.screens.HomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost

data class DrawerProperties(
    val scope: CoroutineScope,
    val state: DrawerState
)

@Composable
fun Drawer(navController: NavHostController, mainScreen: @Composable (NavHostController, DrawerProperties) -> Unit) {
    val drawerProperties = DrawerProperties(scope = rememberCoroutineScope(), state = rememberDrawerState(initialValue = DrawerValue.Closed))
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    ModalNavigationDrawer(
    drawerContent = {
        ModalDrawerSheet {
            Spacer(modifier = Modifier.height(16.dp))
            items.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    label = {
                        Text(text = item.title)
                    },
                    selected = index == selectedItemIndex,
                    onClick = {
                        navController.navigate(item.route)
                        selectedItemIndex = index
                        drawerProperties.scope.launch {
                            drawerProperties.state.close()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    },
    drawerState = drawerProperties.state){
        mainScreen(navController, drawerProperties)
    }
}
