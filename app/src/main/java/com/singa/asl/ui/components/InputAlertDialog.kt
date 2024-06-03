package com.singa.asl.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBluePastelBackground

@Composable
fun PopupInputAlertDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color1
            )
        },
        text = {
            OutlinedTextField(
                value = value,
                placeholder = { Text(text = "Enter title", color = Color.Gray) },
                onValueChange = {
                    onValueChange(it)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color1,
                    unfocusedBorderColor = Color1,
                    cursorColor = Color1
                ),
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