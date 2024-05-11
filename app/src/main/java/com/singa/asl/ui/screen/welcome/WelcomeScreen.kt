package com.singa.asl.ui.screen.welcome

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.R

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuest: () -> Unit
) {
    WelcomeContent(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToGuest = onNavigateToGuest
    )
}

@Composable
fun WelcomeContent(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                Color(0xFF4BA6F8)
            )
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.welcome
            ),
            contentDescription = "Welcome Image",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.fillMaxHeight(0.1f)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .padding(
                    20.dp
                )
        ) {

            Text(
                text = "Welcome",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.fillMaxHeight(0.02f)
            )

            Text(
                text = "Login or Guest account to take apps, take part in challenge, and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.fillMaxHeight(0.1f)
            )

            Button(
                onClick = {
                    onNavigateToLogin()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4BA6F8),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }

            Spacer(
                modifier = Modifier.fillMaxHeight(0.05f)
            )

            Button(
                onClick = {
                    onNavigateToGuest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5EEFF),
                    contentColor = Color(0xFF4BA6F8)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Guest Account")

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onNavigateToLogin = {},
        onNavigateToGuest = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenDarkPreview() {
    WelcomeScreen(
        onNavigateToLogin = {},
        onNavigateToGuest = {}
    )
}