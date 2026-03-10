package com.example.globalfugitive

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.globalfugitive.BuildConfig.APP_CLIENT_ID
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun GoogleSignInButton(
    userViewModel: UserViewModel,
    onGetCredentialResponse: (GetCredentialResponse) -> Unit
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    Box(
        modifier = Modifier
            .clickable {

                // Build the Google sign-in option
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(APP_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()

                val signInWithGoogleOption: GetSignInWithGoogleOption =
                    GetSignInWithGoogleOption.Builder(APP_CLIENT_ID)
                    .build()

                // Create a GetCredentialRequest and add the googleIdOption
                val credentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                // Use coroutine to call CredentialManager.getCredential()
                coroutineScope.launch {
                    try {
                        // Get credentials asynchronously
                        val result = credentialManager.getCredential(
                            request = credentialRequest,
                            context = context
                        )

                        // Pass the result to your handler
                        onGetCredentialResponse(result)
                    } catch (e: GetCredentialException) {
                        if (e is NoCredentialException) {
                            // Inform the user that no credentials were found and they might need to add an account
                            userViewModel.setErrorMessage("No credentials available. Please add a Google account to your device.")
                        } else {
                            userViewModel.setErrorMessage("Get credentials failed")
                            e.printStackTrace()
                        }
                        println("Get credentials failed")
                        e.printStackTrace()
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_button), // Replace with your image resource
            contentDescription = "Sign in with Google",
            modifier = Modifier.width(200.dp) // You can adjust the size as per your requirement
        )
    }
}