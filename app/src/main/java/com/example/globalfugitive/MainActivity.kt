package com.example.globalfugitive

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.globalfugitive.ui.theme.AppTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val countryViewModel: CountryViewModel by viewModels()
    private lateinit var gameViewModel: GameViewModel

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
        FirebaseApp.initializeApp(this)

        // Initialise places API
        initializePlacesAPI()

        // Initialise GameViewModel using GameViewModelFactory
        initialiseGameViewModel()

        val db = Firebase.firestore

        // Create ViewModels and Repositories in the Activity scope
        val userViewModel = UserViewModel()
        val retrofitViewModel = RetrofitViewModel()


        enableEdgeToEdge()

        setContent {
            AppTheme(dynamicColor = false){
                val navController = rememberNavController()

                // Check if the user is signed in
                val currentUser = auth.currentUser

                // Set start destination based on user authentication
                var startDestination = "Landing"
                if (currentUser != null) {
                    println("User @ MainActivity: $currentUser")
                    userViewModel.setCurrentUserFromFireStore()
                    startDestination = "DrawerMenu" // Navigate directly to DrawerMenu if user is signed in
                }

                AppNavigation(
                    countryViewModel = countryViewModel,
                    gameViewModel = gameViewModel,
                    userViewModel = userViewModel,
                    startDestination = startDestination,
                    retrofitViewModel = retrofitViewModel,
                    navController = navController
                )
            }
        }
    }

    private fun initializePlacesAPI() {
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")

        // Initialize Places SDK with the API key from meta-data
        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }
    }

    private fun initialiseGameViewModel() {
        // Access the CountryDao from the Room database
        val dao = CountryDatabase.getDatabase(application).countryDao()

        // Create an instance of GameViewModelFactory and pass the necessary dependencies
        val factory = GameViewModelFactory(dao, application)

        // Initialize GameViewModel using the factory
        gameViewModel = ViewModelProvider(this, factory).get(GameViewModel::class.java)

        // Fetch countries
        gameViewModel.getCountries()

    }


}

