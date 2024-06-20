package com.singa.asl.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBluePastelBackground
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun PopupInputAlertDialog(
    title: String,
    value: String,
    isLoading: MutableStateFlow<Boolean>,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
) {

    isLoading.collectAsState(initial = false).value.let { loading ->
        AlertDialog(
            title = {
                if (!loading) {
                    Text(
                        title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color1
                    )
                }

            },
            text = {
                if (loading) {
                    CircularProgressIndicator()
                } else {
                    OutlinedTextField(
                        value = value,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Enter title", color = Color.Gray) },
                        onValueChange = {
                            onValueChange(it)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color1,
                            unfocusedBorderColor = Color1,
                            cursorColor = Color1,
                            focusedTextColor = Color1
                        ),
                    )
                }
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
}