package com.example.globalfugitive
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun GoogleMapsScreen(
    cameraPositionState: CameraPositionState,
    selectedLocation: LatLng?,
    viewModel: GameViewModel,
    onMapLoaded: () -> Unit
) {
    val context = LocalContext.current
    val targets by viewModel.guesses
    val guessLatLong by viewModel.guessesLatLng

    // Load map style from json
    val mapProperties = remember {
        MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.custom_map),
        )
    }

    // Define the MarkerState for the selected location
    val markerState = remember { MarkerState(position = selectedLocation ?: LatLng(0.0, 0.0)) }

    // Use LaunchedEffect to animate camera position on selected location change
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            markerState.position = location
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 1f),
                durationMs = 1000 // Smooth animation to the new location
            )
        }
    }

    // Composable UI with a map and button to launch search
    Box(modifier = Modifier
        .padding(0.dp)
        .fillMaxWidth()
    ) {
        // Google Map Composable
        GoogleMap(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                compassEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true,
                rotationGesturesEnabled = false,
                mapToolbarEnabled = false,
                scrollGesturesEnabled = true,
            ),
            onMapLoaded = {
                // Notify that the map has finished loading
                onMapLoaded()
            }

        ) {
            // Add a marker if a location is selected
            selectedLocation?.let {}
            guessLatLong.forEach { target ->
                Marker(
                    state = rememberUpdatedMarkerState(position = target),
                    title = "",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.crosshair_small),
                    alpha = 0.8f
                )
            }
        }
    }
}


