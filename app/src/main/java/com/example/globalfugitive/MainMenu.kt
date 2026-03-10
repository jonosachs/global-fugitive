package com.example.globalfugitive

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun MainMenu(
    navController: NavController,
    retrofitViewModel: RetrofitViewModel,
) {

    // Get the weather data from the view model
    val weatherData by remember { retrofitViewModel.retrofitResponse }
    val context = LocalContext.current
    // Get random city for weather report
    val randomCityName = remember { getRandomCity(context) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.world_map),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = 3f,
                    scaleY = 3f
                )
        )
    }
    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            Image(
                painter = painterResource(id = R.drawable.global_fugitive_text_transp),
                contentDescription = "Global Fugitive text",
                modifier = Modifier
                    .height(100.dp)
                    .offset(y = 25.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate("GamePlayScreen") {
                        popUpTo("GamePlayScreen") { inclusive = true }
                    }
                },
                modifier = Modifier.width(200.dp),
            ) {
                Text("New Game")
            }
//            Button(
//                onClick = { navController.navigate("") },
//                modifier = Modifier.width(200.dp),
//            ) {
//                Text("2-Player")
//            }
            Button(
                onClick = {
                    (context as? Activity)?.finishAffinity()
                },
                modifier = Modifier.width(200.dp),
            ) {
                Text("Quit")
            }

            Spacer(modifier = Modifier.height(32.dp))


            LaunchedEffect(key1 = randomCityName) {
                retrofitViewModel.getResponse(randomCityName) // Fetch weather data for the selected country
            }

            weatherData?.let {
                Text(
                    text = "${it.location.name}, ${it.location.country}",
                    color = Color.Red
                )
                Text(
                    text = "${it.current.temp_c} °C",
                    color = Color.Red
                )
                Text(
                    text = "${it.current.condition.text}",
                    color = Color.Red
                )

            } ?: run {
                Text(
                    text = "Loading weather data...",
                    color = Color.Red
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.heatmap_footer),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = 1.1f,
                    scaleY = 1.1f
                )
                .align(Alignment.BottomCenter)
        )
    }
}

