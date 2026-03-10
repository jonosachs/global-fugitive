import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.globalfugitive.R
import com.example.globalfugitive.UserViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.style.TextDecoration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val currentUser = userViewModel.currentUser
    var displayName by remember { mutableStateOf("") }
    var dateOfBirth = remember { mutableStateOf("") }

    // Observe error messages from UserViewModel
    val errorMessage by userViewModel.errorMessage.observeAsState()

    //Drop down menu for gender
    val gender = listOf("Male", "Female", "Other")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf(gender[0]) }

    //Date picker
    val calendar = Calendar.getInstance()
    // Create a DatePicker state and initialize it with the selected date
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    var showDatePicker by remember { mutableStateOf(false) }


        // Observe the errorMessage and stop loading if there's an error
    if (errorMessage != null) {
        isLoading = false
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            isLoading = false
            // Navigate to MainMenu when the user is successfully logged in
            navController.navigate("DrawerMenu")
        }
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
    }


    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "New User",
            style = MaterialTheme.typography.titleLarge,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                userViewModel.clearErrorMessage()
                email = it
            },
            label = { Text("Email") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                userViewModel.clearErrorMessage()
                password = it
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date picker for date of birth
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        val formattedDate = formatter.format(Date(selectedDate))

        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            label = { Text("Date of Birth") },
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
           },
            modifier = Modifier
                .clickable {
                    showDatePicker = true // Show DatePicker when clicked
                },
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            userViewModel.clearErrorMessage()
                            showDatePicker = false
                            selectedDate = datePickerState.selectedDateMillis ?: calendar.timeInMillis
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {showDatePicker = false}
                    ) {
                        Text(text = "Cancel")
                    }
                }
            ) {
                DatePicker( state = datePickerState )
            }
        }// end of if


        Spacer(modifier = Modifier.height(16.dp))

        // Exposed Dropdown Menu for selecting sex
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .focusProperties {
                        canFocus = false
                    },
                readOnly = true,
                value = selectedGender,
                onValueChange = {},
                label = { Text("Gender") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isExpanded)
               },
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                gender.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            userViewModel.clearErrorMessage()
                            selectedGender = selectionOption
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                val activity = context as Activity
                userViewModel.createUserWithEmailAndPassword(
                    activity,
                    email,
                    password,
                    selectedDate,
                    selectedGender
                ) { navController.navigate("DrawerMenu") }
            },
            modifier = Modifier.width(200.dp),
//            enabled = !isLoading
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                userViewModel.clearErrorMessage()
                navController.navigate("SignInScreen")
            },
            modifier = Modifier.width(200.dp),
        ) {
            Text("Cancel")
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        // Error message display
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

    }
}
