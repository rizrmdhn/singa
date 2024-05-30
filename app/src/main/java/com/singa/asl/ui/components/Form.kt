package com.singa.asl.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.SingaTheme
import com.singa.core.domain.model.FormItem

@Composable
fun FormComp(
    formData: List<FormItem>,
    buttonText: String = "Login",
    needSubmitButton: Boolean = true,
    onClickButton: () -> Unit,
    isLoading: Boolean = false,
) {
    formData.forEach { data ->
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
            )
            (if (data.colors != null) data.colors else TextFieldDefaults.colors())?.let { colors ->
                TextField(
                    placeholder = {
                        Text(
                            text = data.placeholder
                        )
                    },
                    value = data.value,
                    onValueChange = {
                        data.onValueChange(it)
                    },
                    shape = data.shape,
                    singleLine = true,
                    keyboardOptions = data.keyboardOptions,
                    isError = data.isError,
                    leadingIcon = data.leadingIcon,
                    supportingText = {
                        if (data.isError) {
                            Text(
                                text = data.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("error_message")
                            )
                        }
                    },
                    visualTransformation = data.visualTransformation,
                    trailingIcon = {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            data.trailingIcon?.invoke()
                            if (data.isError) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = stringResource(R.string.error_icon),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    colors = colors,
                    modifier = data.modifier
                        .fillMaxWidth()
                )
            }
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
    if (needSubmitButton) {
        TextButton(
            enabled = !isLoading,
            onClick = {
                onClickButton()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(
                    if (isLoading) {
                        Color1.copy(alpha = 0.7f)
                    } else {
                        Color1
                    }
                )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.background,
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = buttonText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultLoginPreview() {
    SingaTheme {
        FormComp(
            formData = listOf(
                FormItem(
                    title = "Username",
                    placeholder = "Enter your username",
                    value = "",
                    onValueChange = {},
                    isError = false,
                    errorMessage = "",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email icon"
                        )
                    },
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions.Default,
                ),
            ),
            buttonText = "Login",
            onClickButton = {},
            isLoading = true,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultDarkLoginPreview() {
    SingaTheme {
        FormComp(
            formData = listOf(
                FormItem(
                    title = "Username",
                    placeholder = "Enter your username",
                    value = "",
                    onValueChange = {},
                    isError = false,
                    errorMessage = "",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email icon"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    visualTransformation = VisualTransformation.None,
                )
            ),
            buttonText = "Login",
            onClickButton = {},
            isLoading = true,
        )
    }
}