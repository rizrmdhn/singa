package com.singa.asl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBluePastelBackground

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info icon",
                tint = Color1
            )
        },
        title = {
            Text(
                title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color1
            )
        },
        text = {
            Text(
                text,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color1
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    "Cancel",
                    color = Color1
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = confirmButton
            ) {
                Text(
                    "Confirm",
                    color = Color1
                )
            }
        },
        containerColor = ColorBluePastelBackground
    )
}