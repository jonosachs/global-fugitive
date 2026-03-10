package com.example.globalfugitive

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun UserProfile(
    userViewModel: UserViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    val user = userViewModel.currentUser

    var showEditDialog by remember { mutableStateOf(false) }
    var fieldToEdit by remember { mutableStateOf("") }
    var editedValue by remember { mutableStateOf("") }

    val userId = user?.userId.orEmpty()
    val email = user?.email.orEmpty()
    val displayName = user?.displayName.orEmpty()
    val dateOfBirth = user?.dateOfBirth?.let { formatter.format(Date(it)) } ?: ""
    val gender = user?.gender.orEmpty()

    var showGenderMenu by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf(user?.gender) }

    val calendar = Calendar.getInstance()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateOfBirth by remember { mutableStateOf(user?.dateOfBirth ?: System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateOfBirth)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture and Header
        ProfilePicture(user)

        Spacer(modifier = Modifier.height(16.dp))

        // Display each field with an edit option if applicable
        UserProfileField("User ID", userId, isEditable = false)
        UserProfileField("Email", email, isEditable = false)
        UserProfileField("Display Name", displayName, isEditable = true) {
            fieldToEdit = "Display Name"
            editedValue = displayName
            showEditDialog = true
        }
        UserProfileField("Date of Birth", dateOfBirth, isEditable = true) {
            fieldToEdit = "Date of Birth"
            editedValue = selectedDateOfBirth.toString()
            showDatePicker = true
        }
        UserProfileField("Gender", gender, isEditable = true) {
            fieldToEdit = "Gender"
            editedValue = selectedGender.orEmpty()
            showGenderMenu = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Password and Delete Account
        ProfileActions(navController, userViewModel)

        // Edit Dialog
        if (showEditDialog) {
            EditDialog(
                field = fieldToEdit,
                value = editedValue,
                onDismiss = { showEditDialog = false },
                onSave = { newValue ->
                    when (fieldToEdit) {
                        "Display Name" -> userViewModel.updateUserField(userId, "displayName", newValue, {}, {})
                        "Date of Birth" -> userViewModel.updateUserField(userId, "dateOfBirth", newValue, {}, {})
                        "Gender" -> userViewModel.updateUserField(userId, "gender", newValue, {}, {})
                    }
                    showEditDialog = false
                }
            )
        }


        // DatePicker for Date of Birth
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        selectedDateOfBirth = datePickerState.selectedDateMillis ?: calendar.timeInMillis
                        userViewModel.updateUserField(
                            userId = user?.userId ?: "",
                            field = "dateOfBirth",
                            value = selectedDateOfBirth,
                            onSuccess = { /* Handle Success */ },
                            onFailure = { /* Handle Failure */ }
                        )
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
            ) {
                DatePicker( state = datePickerState )
            }
        }

        // Dropdown Menu for Gender
        if (showGenderMenu) {
            GenderDropdownMenu(
                selectedGender = selectedGender.orEmpty(),
                onGenderSelected = { gender ->
                    selectedGender = gender
                    showGenderMenu = false
                    userViewModel.updateUserField(
                        userId = user?.userId ?: "",
                        field = "gender",
                        value = gender,
                        onSuccess = { /* Handle Success */ },
                        onFailure = { /* Handle Failure */ }
                    )
                },
                onDismiss = { showGenderMenu = false }
            )
        }
    }



}

@Composable
fun UserProfileField(
    field: String,
    value: String,
    isEditable: Boolean,
    onEdit: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$field: $value", modifier = Modifier.weight(1f))

        if (isEditable && onEdit != null) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit $field")
            }
        }
    }
}


@Composable
fun EditDialog(
    field: String,
    value: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editedValue by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit $field") },
        text = {
            TextField(
                value = editedValue,
                onValueChange = { editedValue = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                onSave(editedValue)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun ProfilePicture(user: User?) {
    user?.photoUrl?.let {
        Image(
            painter = rememberAsyncImagePainter(it),
            contentDescription = "User Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .clickable { /* Handle profile picture click here */ }
        )
    } ?: run {
        Image(
            painter = painterResource(id = R.drawable.account_circle),
            contentDescription = "Default Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ProfileActions(navController: NavController, userViewModel: UserViewModel) {
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showConfirmResetEmail by remember { mutableStateOf(false) }

    Button(onClick = { showConfirmResetEmail = true }) {
        Text("Reset Password")
    }
    if (showConfirmResetEmail) {
        ConfirmDialog(
            text = "Reset email? A message will be sent to your registered email address.",
            onConfirm = {
                userViewModel.sendPasswordResetEmail(userViewModel.currentUser?.email ?: "")
                showConfirmResetEmail = false
            },
            onDismiss = { showConfirmResetEmail = false }
        )
    }

    Button(onClick = { showConfirmDelete = true }) {
        Text("Delete Account")
    }
    if (showConfirmDelete) {
        ConfirmDialog(
            text = "Are you sure you want to delete your account?",
            onConfirm = {
                userViewModel.deleteUser {
                    navController.navigate("SignInScreen") {
                        popUpTo("SignInScreen") { inclusive = true }
                    }
                }
                showConfirmDelete = false
            },
            onDismiss = { showConfirmDelete = false }
        )
    }
}

@Composable
fun ConfirmDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun GenderDropdownMenu(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val genders = listOf("Male", "Female", "Other")

    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss
    ) {
        genders.forEach { gender ->
            DropdownMenuItem(
                text = { Text(text = gender) },
                onClick = { onGenderSelected(gender) }
            )
        }
    }
}



