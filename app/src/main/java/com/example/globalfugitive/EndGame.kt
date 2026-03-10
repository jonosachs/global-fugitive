package com.example.globalfugitive

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EndGame(
    navController: NavController,
    gameViewModel: GameViewModel,
    userViewModel: UserViewModel
) {
    val mysteryCountry = gameViewModel.mysteryCountry.value
    val mysteryCountryFlag = gameViewModel.mysteryFlag.value
    val gameWon = gameViewModel.gameWon.value
    val targets by gameViewModel.guesses
    val context = LocalContext.current


    println("gameWon value @ EndGame: $gameWon")

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        // Set images and text based on game outcome
        var winLoseFace: Int? = null
        var winLoseImg: Int? = null
        var winLoseTxt: String? = null
        when (gameWon) {
            true -> {
                winLoseFace = R.drawable.authority_face_1_transp
                winLoseImg = R.drawable.fugitive_found
                winLoseTxt = "You found the fugitive! They were hiding in $mysteryCountry! $mysteryCountryFlag"
            }
            else -> {
                winLoseFace = R.drawable.authority_face_4_transp
                winLoseImg = R.drawable.fugitive_escaped
                winLoseTxt = "The fugitive got away!..They were hiding in $mysteryCountry! $mysteryCountryFlag"
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {

            val xoffset = 30.dp

            //Face image
            Column(
            ) {
                Image(
                    painter = painterResource(id = winLoseFace),
                    contentDescription = "Authority face",
                    modifier = Modifier
                        .height(175.dp)
                        .offset(x = xoffset)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            //Guesses Text
            Column(
                modifier = Modifier.width(250.dp)
            ) {
                Text(
                    text = "Targets",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .offset(x = xoffset)
                )

                targets.forEachIndexed { index, target ->

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
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        text = "${index + 1}. $capitalizedResult",
                        modifier = Modifier
                            .offset(x = xoffset)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = painterResource(id = winLoseImg),
            contentDescription = "Fugitive found image",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        //TODO: Center text for end game message
        Column (
          modifier = Modifier.width(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = winLoseTxt,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("GamePlayScreen") },
            modifier = Modifier.width(200.dp),
        ) {
            Text("Play again")
        }
        Button(
            onClick = {
                (context as? Activity)?.finishAffinity()
            },
            modifier = Modifier.width(200.dp)
        ) {
            Text("Quit")
        }
    }
}

