package com.singa.asl.ui.screen.login

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singa.asl.BuildConfig
import com.singa.asl.R
import com.singa.asl.ui.components.FormComp
import com.singa.asl.ui.theme.AuthBackground
import com.singa.asl.ui.theme.Color1
import com.singa.core.domain.model.FormItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    email: String,
    isEmailError: Boolean,
    emailError: String,
    onChangeEmail: (String) -> Unit,
    password: String,
    isPasswordError: Boolean,
    passwordError: String,
    onChangePassword: (String) -> Unit,
    isLoginLoading: Boolean,
    onLogin: () -> Unit,
    navigateToRegister: () -> Unit,
    setSocialLoginUrl: (String) -> Unit,
    viewModel: LoginScreenViewModel = koinViewModel()
) {
    val isGithubLoginLoading by viewModel.githubLoginIsLoading.collectAsState()
    val isGoogleLoginLoading by viewModel.googleLoginIsLoading.collectAsState()

    LoginContent(
        email = email,
        isEmailError = isEmailError,
        emailError = emailError,
        onChangeEmail = onChangeEmail,
        password = password,
        isPasswordError = isPasswordError,
        passwordError = passwordError,
        onChangePassword = onChangePassword,
        isLoginLoading = isLoginLoading,
        onLogin = onLogin,
        navigateToRegister = navigateToRegister,
        isGithubLoginLoading = isGithubLoginLoading,
        setIsGithubLoginLoading = viewModel::setGithubLoginIsLoading,
        isGoogleLoginLoading = isGoogleLoginLoading,
        setIsGoogleLoginLoading = viewModel::setGoogleLoginIsLoading,
        setSocialLoginUrl = setSocialLoginUrl
    )
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    email: String,
    isEmailError: Boolean,
    emailError: String,
    onChangeEmail: (String) -> Unit,
    password: String,
    isPasswordError: Boolean,
    passwordError: String,
    onChangePassword: (String) -> Unit,
    isLoginLoading: Boolean,
    onLogin: () -> Unit,
    navigateToRegister: () -> Unit,
    isGithubLoginLoading: Boolean,
    setIsGithubLoginLoading: (Boolean) -> Unit,
    isGoogleLoginLoading: Boolean,
    setIsGoogleLoginLoading: (Boolean) -> Unit,
    setSocialLoginUrl: (String) -> Unit
) {
    var showPassword by remember {
        mutableStateOf(false)
    }

    val formList = listOf(
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
            onClickButton = onLogin,
            isLoading = isLoginLoading,
            buttonText = "Login",
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(1.dp),
            )
            Text(
                text = "OR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(1.dp),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color1),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White
                ),
                onClick = {
                    MainScope().launch {
                        setIsGithubLoginLoading(true)
                        delay(1000)
                        setSocialLoginUrl(
                            if (BuildConfig.PRODUCTION_MODE) {
                                BuildConfig.BASE_URL_PROD.plus("login/github")
                            } else {
                                BuildConfig.BASE_URL.plus("login/github")
                            }
                        )
                        setIsGithubLoginLoading(false)
                    }
                }
            ) {
                if (isGithubLoginLoading) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                } else {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.mdi_github),
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.github),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color1),
                onClick = {
                    MainScope().launch {
                        setIsGoogleLoginLoading(true)
                        delay(1000)
                        setSocialLoginUrl(
                            if (BuildConfig.PRODUCTION_MODE) {
                                BuildConfig.BASE_URL_PROD.plus("login/google")
                            } else {
                                BuildConfig.BASE_URL.plus("login/google")
                            }
                        )
                        setIsGoogleLoginLoading(false)
                    }
                }
            ) {
                if (isGoogleLoginLoading) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                } else {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.mdi_google),
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.google),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = stringResource(R.string.don_t_have_an_account),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
            Text(
                text = stringResource(R.string.register),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color1,
                modifier = Modifier.clickable {
                    navigateToRegister()
                }
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        email = "",
        onChangeEmail = {},
        password = "",
        onChangePassword = {},
        onLogin = {},
        isEmailError = false,
        emailError = "",
        isPasswordError = false,
        passwordError = "",
        isLoginLoading = true,
        navigateToRegister = {},
        setSocialLoginUrl = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    LoginScreen(
        email = "",
        onChangeEmail = {},
        password = "",
        onChangePassword = {},
        onLogin = {},
        isEmailError = false,
        emailError = "",
        isPasswordError = false,
        passwordError = "",
        isLoginLoading = false,
        navigateToRegister = {},
        setSocialLoginUrl = {}
    )
}