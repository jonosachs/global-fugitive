package com.example.globalfugitive

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class UserViewModel() : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val errorMessage = MutableLiveData<String?>(null)
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun signInWithEmailAndPassword(
        activity: Activity,
        email: String,
        password: String,
        onSignInSuccess: () -> Unit
    ) {

        // Validate input before making the Firebase call
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Email and password cannot be empty."
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    println("userName @ SignInViewModel: ${auth.currentUser}")
                    setCurrentUserFromFireStore()
                    clearErrorMessage() // Clear the error message
                    onSignInSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    errorMessage.value = generateErrorMessage(task.exception)
                }
            }
    }

    fun addUserToFirestore(user: FirebaseUser, dateOfBirth: Long?, gender: String?) {
        val userData = hashMapOf(
            "userId" to user.uid,
            "email" to user.email,
            "displayName" to user.displayName,
            "photoUrl" to user.photoUrl?.toString(),
            "dateOfBirth" to dateOfBirth,
            "gender" to gender.orEmpty()
        )

        // Add a new document with a generated ID
        db.collection("users")
            .document(user.uid ?: "")
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User data added successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


    fun setCurrentUserFromFireStore() {
        val user = FirebaseAuth.getInstance().currentUser

        // If user is null, return early
        if (user == null) {
            Log.e("Firestore", "User is not authenticated")
            return
        }

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentUser = User(
                        userId = document.getString("userId") ?: user.uid,
                        email = document.getString("email") ?: user.email,
                        displayName = document.getString("displayName") ?: user.displayName,
                        photoUrl = document.getString("PhotoUrl") ?: user.photoUrl.toString(),
                        dateOfBirth = document.getLong("dateOfBirth"),
                        gender = document.getString("gender")
                        )
                    Log.d("Firestore", "Retrieved user data: $currentUser")
                } else {
                    addUserToFirestore(user, null, null)
                    //Call recursively to set user from firestore after data is uploaded
                    setCurrentUserFromFireStore()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user data", e)
            }
    }


    fun signInWithGoogle(result: GetCredentialResponse, onSignInSuccess: () -> Unit) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        signInWithFirebase(googleIdTokenCredential.idToken, onSignInSuccess = onSignInSuccess)

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }
        }
    }


    fun signInWithFirebase(idToken: String, onSignInSuccess: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
            } catch (e: Exception) {
                println("Could not pass Google credentials to Firebase")
            }
            setCurrentUserFromFireStore()
            clearErrorMessage()
            onSignInSuccess()
        }
    }

    fun deleteUser(onSuccess: () -> Unit) {
        val user = Firebase.auth.currentUser!!

        //Delete user from Firestore
        db.collection("users").document(user.uid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "User deleted from Firestore.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error, could not delete account from Firestore", e)
            }

        //Delete user from Firebase
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted from Firebase.")
                    onSuccess()
                } else {
                    Log.d(TAG, "Error, could not delete account from Firebase.")
                }
            }

        signOut()
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
        println("Logged out")
    }

    fun createUserWithEmailAndPassword(
        activity: Activity,
        email: String,
        password: String,
        selectedDate: Long,
        selectedGender: String,
        onSignInSuccess: () -> Unit
    ) {


        // Validate email address
        if (!isEmailValid(email)) {
            errorMessage.value = "Please enter a valid email address."
            return
        }
        // Validate password
        if (!isPasswordValid(password)) {
            errorMessage.value = "Password must be at least 6 characters long and include at least one special character."
            return
        }
        // Validate input not blank
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Email and password cannot be empty."
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    addUserToFirestore(
                        auth.currentUser!!,
                        dateOfBirth = selectedDate,
                        gender = selectedGender
                    )
                    setCurrentUserFromFireStore()
                    clearErrorMessage()
                    onSignInSuccess()
                } else {
                    errorMessage.value = generateErrorMessage(task.exception)
                }
            }
    }

    fun sendPasswordResetEmail(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email sent successfully
                    Log.d("UserProfile", "Password reset email sent.")
                } else {
                    // Handle failure
                    Log.e("UserProfile", "Error sending password reset email.", task.exception)
                }
            }
    }

    // Validate email address
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate password
    private fun isPasswordValid(password: String): Boolean {
        // One special character
        // Minimum 6 characters
        val passwordPattern = "^(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}$"
        val passwordMatcher = Regex(passwordPattern)
        return passwordMatcher.matches(password)
    }

    // Generate error message
    private fun generateErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password must be at least 6 characters."
            is FirebaseAuthInvalidCredentialsException -> {
                if (exception.errorCode == "ERROR_INVALID_EMAIL") {
                    "Invalid email address format. Please enter a valid email."
                } else {
                    "Invalid credentials, please try again."
                }
            }
            is FirebaseAuthUserCollisionException -> "The email address is already in use by another account."
            is FirebaseAuthInvalidUserException -> "User does not exist. Please sign up first."
            is FirebaseAuthRecentLoginRequiredException -> "You need to re-authenticate to complete that action."
            else -> "Action failed. Please try again."
        }
    }

    fun setErrorMessage(error: String) {
        errorMessage.value = error
    }

    fun clearErrorMessage() {
        errorMessage.value = null
    }

    fun updateUserField(
        userId: String,
        field: String,
        value: Any,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users").document(userId)
            .update(field, value)
            .addOnSuccessListener {
                when (field) {
                    "displayName" -> currentUser = currentUser?.copy(displayName = value as String)
                    "photoUrl" -> currentUser = currentUser?.copy(photoUrl = value as String)
                    "dateOfBirth" -> currentUser = currentUser?.copy(dateOfBirth = value as Long)
                    "gender" -> currentUser = currentUser?.copy(gender = value as String)

                }

                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


}
