package com.example.globalfugitive

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Landing (navController: NavController) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Global Fugitive image",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = 2.25f,
                    scaleY = 2.25f
                )
                .alpha(0.9f)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Image(
                painter = painterResource(id = R.drawable.global_fugitive_text_transp_white),
                contentDescription = "",
            )

            Button(
                onClick = { navController.navigate("SignInScreen") },
                modifier = Modifier
                    .width(200.dp)
            ) {
                Text("Start")
            }
        }

    }
}