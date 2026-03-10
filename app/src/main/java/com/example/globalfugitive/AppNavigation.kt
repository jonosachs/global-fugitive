package com.example.globalfugitive

import SignUpScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavigation(
    countryViewModel: CountryViewModel,
    gameViewModel: GameViewModel,
    userViewModel: UserViewModel,
    startDestination: String,
    navController: NavHostController,
    retrofitViewModel: RetrofitViewModel
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("Landing") { Landing(navController) }
        composable("SignInScreen") { SignInScreen(userViewModel, navController) }
        composable("SignUpScreen") { SignUpScreen(userViewModel, navController) }
        composable("DrawerMenu") {
            // Create a nested NavController for DrawerMenu's internal navigation
            val nestedNavController = rememberNavController()
            DrawerNavigation(
                parentNavController = navController, // Pass parent NavController
                nestedNavController = nestedNavController, // Nested NavController for the drawer menu
                userViewModel = userViewModel,
                retrofitViewModel = retrofitViewModel
            )
        }
        composable("GamePlayScreen") { GamePlayScreen(navController, gameViewModel, userViewModel) }
        composable("EndGame") { EndGame(navController, gameViewModel, userViewModel) }
    }


}


