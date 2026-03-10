package com.example.globalfugitive

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DrawerNavigation(
    parentNavController: NavController,
    nestedNavController: NavHostController,
    userViewModel: UserViewModel,
    retrofitViewModel: RetrofitViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()

    // Scaffold setup for material3
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus) { route ->
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    when(route) {
                        "SignInScreen", "Landing" -> {
                            userViewModel.signOut()
                            parentNavController.navigate(route)
                        }
                        else -> nestedNavController.navigate(route)
                    }
                }
            }
        }
    ) {

        // The main navigation host for the drawer content
        NavHost(
            navController = nestedNavController,
            startDestination = "MainMenu" // Default start screen inside drawer menu
        ) {
            composable("MainMenu") { MainMenu(parentNavController, retrofitViewModel) }
            composable("UserProfile") { UserProfile(userViewModel, parentNavController) }
            composable("SignInScreen") { SignInScreen(userViewModel, parentNavController) }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {

            // Burger Icon
            IconButton(
                onClick = {
                    scope.launch { drawerState.open() }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Align the icon to the top end (top right corner)
                    .padding(16.dp) // Optional padding for spacing
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black, // Optional icon color
                    modifier = Modifier
                        .padding(top = 16.dp)
                )
            }

        }


    }
}
