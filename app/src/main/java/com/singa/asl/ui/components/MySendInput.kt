package com.singa.asl.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.singa.asl.ui.theme.Color1

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MySendInput(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    placeHolder: String,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    isLoading: Boolean,
    isFocused: Boolean,
    setInputFocus: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    OutlinedTextField(
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth(
               if (!isFocused) 0.65f else 1f
            )
            .onFocusChanged {
                setInputFocus(it.isFocused)
            },
        value = text,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        onValueChange = {
            onTextChange(it)
        },
        placeholder = {
            Text(
                text = placeHolder,
                fontWeight = FontWeight.Normal
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Color.Black,
            focusedBorderColor = Color1,
            unfocusedBorderColor = Color(0xFFEFEFEF),
            focusedTextColor = Color1,
            unfocusedTextColor = Color1

        ),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onClick()
                },
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        },
        maxLines = 4
    )
}