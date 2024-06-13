package com.singa.asl.ui.screen.register

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.components.FormComp
import com.singa.asl.ui.theme.AuthBackground
import com.singa.asl.ui.theme.Color1
import com.singa.core.domain.model.FormItem


@Composable
fun RegisterScreen(
    name: String,
    isNameError: Boolean,
    nameError: String,
    onNameEmail: (String) -> Unit,
    email: String,
    isEmailError: Boolean,
    emailError: String,
    onChangeEmail: (String) -> Unit,
    password: String,
    isPasswordError: Boolean,
    passwordError: String,
    onChangePassword: (String) -> Unit,
    isRegisterLoading: Boolean,
    onRegister: () -> Unit,
    navigateToLogin: () -> Unit
) {
    RegisterContent(
        name = name,
        isNameError = isNameError,
        nameError = nameError,
        onNameEmail = onNameEmail,
        email = email,
        isEmailError = isEmailError,
        emailError = emailError,
        onChangeEmail = onChangeEmail,
        password = password,
        isPasswordError = isPasswordError,
        passwordError = passwordError,
        onChangePassword = onChangePassword,
        isRegisterLoading = isRegisterLoading,
        onRegister = onRegister,
        navigateToLogin = navigateToLogin
    )
}

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    name: String,
    isNameError: Boolean,
    nameError: String,
    onNameEmail: (String) -> Unit,
    email: String,
    isEmailError: Boolean,
    emailError: String,
    onChangeEmail: (String) -> Unit,
    password: String,
    isPasswordError: Boolean,
    passwordError: String,
    onChangePassword: (String) -> Unit,
    isRegisterLoading: Boolean,
    onRegister: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    var showPassword by remember {
        mutableStateOf(false)
    }

    val formList = listOf(
        FormItem(
            title = stringResource(R.string.name),
            placeholder = stringResource(R.string.enter_your_name),
            value = name,
            onValueChange = {
                onNameEmail(it)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color1
                )
            },
            isError = isNameError,
            errorMessage = nameError,
            shape = RoundedCornerShape(10.dp),
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                errorContainerColor = Color.White,
                errorIndicatorColor = Color.Red,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = Color1,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color1,
            )
        ),
        FormItem(
            title = stringResource(R.string.email),
            placeholder = stringResource(R.string.enter_your_email),
            value = email,
            onValueChange = {
                onChangeEmail(it)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = null,
                    tint = Color1
                )
            },
            isError = isEmailError,
            errorMessage = emailError,
            shape = RoundedCornerShape(10.dp),
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                errorContainerColor = Color.White,
                errorIndicatorColor = Color.Red,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = Color1,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color1,
            )
        ),
        FormItem(
            title = stringResource(R.string.password),
            placeholder = stringResource(R.string.enter_your_password),
            value = password,
            onValueChange = {
                onChangePassword(it)
            },
            shape = RoundedCornerShape(10.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lock_outline_24),
                    contentDescription = null,
                    tint = Color1
                )
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) {
                                R.drawable.baseline_visibility_24
                            } else {
                                R.drawable.baseline_visibility_off_24
                            }
                        ),
                        contentDescription = null,
                        tint = Color1
                    )
                }
            },
            isError = isPasswordError,
            errorMessage = passwordError,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.colors(
                errorContainerColor = Color.White,
                errorIndicatorColor = Color.Red,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = Color1,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color1,
            )
        ),
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .background(
                AuthBackground
            )
            .padding(
                32.dp
            )

        ) {
        FormComp(
            formData = formList,
            onClickButton = {
                onRegister()
            },
            isLoading = isRegisterLoading,
            buttonText = "Register",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = stringResource(R.string.already_have_an_account),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
            Text(
                text = stringResource(R.string.login),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color1,
                modifier = Modifier.clickable {
                    navigateToLogin()
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RegisterScreen(
        name = "",
        isNameError = false,
        nameError = "",
        onNameEmail = {},
        email = "",
        isEmailError = false,
        emailError = "",
        onChangeEmail = {},
        password = "",
        isPasswordError = false,
        passwordError = "",
        onChangePassword = {},
        isRegisterLoading = false,
        onRegister = {},
        navigateToLogin = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    RegisterScreen(
        name = "",
        isNameError = false,
        nameError = "",
        onNameEmail = {},
        email = "",
        isEmailError = false,
        emailError = "",
        onChangeEmail = {},
        password = "",
        isPasswordError = false,
        passwordError = "",
        onChangePassword = {},
        isRegisterLoading = false,
        onRegister = {},
        navigateToLogin = {}
    )
}

