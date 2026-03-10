package com.example.globalfugitive

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerMenu(
    val icon: ImageVector,
    val title: String,
    val route: String)

val menus = arrayOf(
    DrawerMenu(Icons.Filled.Home, "Main Menu", "MainMenu"),
    DrawerMenu(Icons.Filled.AccountCircle, "Profile", "UserProfile"),
    DrawerMenu(Icons.Filled.Clear, "Log Out", "SignInScreen")
)