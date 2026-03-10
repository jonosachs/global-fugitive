package com.example.globalfugitive

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePlayScreen(
    navController: NavController,
    gameViewModel: GameViewModel,
    userViewModel: UserViewModel
) {

    //start a new game
    LaunchedEffect(Unit) {
        gameViewModel.startNewGame()
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val placesClient: PlacesClient = remember { Places.createClient(context) }
    var searchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) } // Shared selected location
    val coroutineScope = rememberCoroutineScope()
    var lazyColumnVisible by remember { mutableStateOf(true) }
    val guesses by gameViewModel.guesses
    val mysteryCountry by gameViewModel.mysteryCountry
    val guessDistance by gameViewModel.guessDistance
    var isLoading by remember { mutableStateOf(true) }

    // Debugging
    println("mysteryCountry = $mysteryCountry")

    // Define a CameraPositionState to control the map camera
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(1.3521, 103.8198), 1f) // Initial position
    }

    // Update predictions whenever the search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            predictions = getCountryPredictions(placesClient, searchQuery)
        } else {
            predictions = emptyList()
        }
    }

    //Maps box
    Box(
        modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        //Google maps row
        //TODO: Fit google maps screen to canvas to show entire world
        GoogleMapsScreen(
            cameraPositionState = cameraPositionState,
            selectedLocation = selectedLocation,
            viewModel = gameViewModel,
            onMapLoaded = {
                isLoading = false
            }
        )

        // Show loading indicator while the map is loading
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

    }

    //Footer box
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter), // Aligns the column to the bottom center of the box
            verticalArrangement = Arrangement.Bottom

        ) {
            //Footer
            Image(
                painter = painterResource(id = R.drawable.heatmap_footer),
                contentDescription = "Footer image",
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = 1.1f,
                        scaleY = 1.1f
                    )
            )
        }

    } //close footer box

    //Main content box
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.global_fugitive_text_transp_white),
                contentDescription = "Global Fugitive text",
                modifier = Modifier
                    .height(100.dp)
                    .align(Alignment.Top)
                    .offset(y = 25.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center),
        )

        //Face response and text row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 135.dp)
                .align(Alignment.BottomCenter),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically

        ) {

            //TODO: Face response based on distance from mystery country
            var imgId: Int? = null
            imgId = when {
                guessDistance != null && guessDistance!! > 12000 -> R.drawable.authority_face_4_transp
                guessDistance != null && guessDistance!! > 10000 -> R.drawable.authority_face_4_transp
                guessDistance != null && guessDistance!! > 5000 -> R.drawable.authority_face_3_transp
                guessDistance != null && guessDistance!! > 2500 -> R.drawable.authority_face_2_transp
                else -> {
                    R.drawable.authority_face_1_transp
                }
            }

            //Face image
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imgId),
                    contentDescription = "Authority face",
                    modifier = Modifier
                        .height(175.dp)
                        .offset(x = 30.dp)
                )
            }

            //Text
            Column(
                modifier = Modifier.width(225.dp)
            ) {
                val xoffset = 40.dp
                val textStyle = MaterialTheme.typography.bodyLarge

                if (guesses.isNotEmpty()) {
//                    Text(
//                        text = "Targets",
//                        style = textStyle,
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        textDecoration = TextDecoration.Underline,
//                        modifier = Modifier
//                            .offset(x = xoffset)
//
//                    )
                } else {
                    Column(modifier = Modifier.width(150.dp)) {
                        Text(
                            text = "\"The fugitive is on the run... Go find them, time is running out!\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier
                                .offset(x = xoffset)
                        )
                    }

                }

                guesses.forEachIndexed { index, target ->

                    // Captialise country names
                    val regex = """[A-Za-zÀ-ÿ0-9]+|[^\w\s]+|\s+""".toRegex()

                    val capitalizedResult = regex.findAll(target)
                        .joinToString("") { matchResult ->
                            val part = matchResult.value
                            if (part.first().isLetter()) {
                                part.replaceFirstChar { it.uppercaseChar() }
                            } else {
                                part // Keep delimiters as they are
                            }
                        }

                    Text(
                        style = textStyle,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        text = "${index + 1}. $capitalizedResult",
                        modifier = Modifier
                            .offset(x = xoffset)

                    )
                }
            }
        } // Close face response and text row

        //Guess entry row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    lazyColumnVisible = true
                    searchQuery = newValue
                },
                placeholder = {
                    Text(
                        text = "Select Country..",
                        style = TextStyle(
                            color = Color.White
                        )
                    )
                },
                textStyle = TextStyle(
                    color = Color.White
                ),
            )

            TextButton(
                onClick = {
                    coroutineScope.launch {
                        // Perform the search or update predictions based on searchQuery
                        if (searchQuery.isNotBlank()) {
                            predictions = getCountryPredictions(placesClient, searchQuery)
                            println("Search submitted with text: $searchQuery")

                            // Update the selected location with the first prediction (if available)
                            if (predictions.isNotEmpty()) {
                                val firstPrediction = predictions[0]
                                val latLng = getLatLngFromPrediction(context, firstPrediction)

                                if (latLng != null && gameViewModel.validGuess(searchQuery)) {

                                    println("Updating selected location to: $latLng")
                                    selectedLocation = latLng // Update the selected location

                                    // Add guess to list of prior guesses if valid
                                    gameViewModel.addGuess(searchQuery, latLng)

                                    // Check if game over conditions met
                                    if (gameViewModel.gameEnd(searchQuery)) {
                                        println("gameWon value @ GamePlayScreen: ${gameViewModel.gameWon.value}")
                                        navController.navigate("EndGame")
                                    }

                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Invalid guess: Please try again!",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Country is not recognised!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }

                            // Reset text field to blank after guess
                            searchQuery = ""

                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "No search query: Please choose a country!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
            ) {
                Text(
                    text = "✈",
                    style = TextStyle(
                        fontSize = 50.sp,
                        color = Color.White
                    )
                )
            }

        }

        //Lazy column row
        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(bottom = 50.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

        ) {
            //Display predictions
            if (lazyColumnVisible) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(predictions.size) { index ->
                        val prediction = predictions[index]
                        Text(
                            text = prediction.getFullText(null).toString(),
                            style = TextStyle(color = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    coroutineScope.launch {
                                        // Handle the click event, e.g., update map position
                                        val latLng = getLatLngFromPrediction(context, prediction)
                                        println("Selected LatLng: $latLng")
                                        if (latLng != null) {
                                            //Update camera position
                                            //selectedLocation = latLng
                                            searchQuery = prediction
                                                .getPrimaryText(null)
                                                .toString()
                                            lazyColumnVisible = false

                                        }

                                    }
                                }
                                .padding(3.dp),
                        )
                    }
                }
            }
        } //close lazy column row
    } //close main content box
} //close composable


