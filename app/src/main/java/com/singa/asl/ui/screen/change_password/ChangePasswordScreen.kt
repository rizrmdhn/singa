package com.singa.asl.ui.screen.change_password

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.R
import com.singa.asl.ui.components.FormComp
import com.singa.asl.ui.components.shimmerBrush
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.asl.ui.theme.ColorBluePastelBackground
import com.singa.core.domain.model.FormItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangePasswordScreen(
    password: String,
    onChangePassword: (String) -> Unit,
    isPasswordError: Boolean,
    passwordError: String,
    confirmPassword: String,
    onChangeConfirmPassword: (String) -> Unit,
    isConfirmPasswordError: Boolean,
    confirmPasswordError: String,
    onUpdatePassword: (
        setUpdatePasswordLoading: (Boolean) -> Unit
    ) -> Unit,
    resetForm: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: ChangePasswordScreenViewModel = koinViewModel()
) {
    val isUpdatePasswordLoading by viewModel.isUpdatePasswordLoading.collectAsState()

    BackHandler {
        navigateBack()
        MainScope().launch {
            delay(500)
            resetForm()
        }
    }

    ChangePasswordContent(
        password = password,
        onChangePassword = onChangePassword,
        isPasswordError = isPasswordError,
        passwordError = passwordError,
        confirmPassword = confirmPassword,
        onChangeConfirmPassword = onChangeConfirmPassword,
        isConfirmPasswordError = isConfirmPasswordError,
        confirmPasswordError = confirmPasswordError,
        onUpdatePassword = onUpdatePassword,
        isUpdatePasswordLoading = isUpdatePasswordLoading,
        setUpdatePasswordLoading = viewModel::setUpdatePasswordLoading
    )
}

@Composable
fun ChangePasswordContent(
    password: String,
    onChangePassword: (String) -> Unit,
    isPasswordError: Boolean,
    passwordError: String,
    confirmPassword: String,
    onChangeConfirmPassword: (String) -> Unit,
    isConfirmPasswordError: Boolean,
    confirmPasswordError: String,
    onUpdatePassword: (
        setUpdatePasswordLoading: (Boolean) -> Unit
    ) -> Unit,
    isUpdatePasswordLoading: Boolean,
    setUpdatePasswordLoading: (Boolean) -> Unit
) {

    var showPassword by remember {
        mutableStateOf(false)
    }

    var showConfirmPassword by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = ColorBackgroundWhite,
        ),
        shape = RoundedCornerShape(
            topStart = 40.dp,
            topEnd = 40.dp,
        )
    ) {

        val formList = listOf(
            FormItem(
                title = stringResource(id = R.string.password),
                placeholder = stringResource(id = R.string.enter_your_password),
                value = password,
                onValueChange = {
                    onChangePassword(it)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_lock_24),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = ColorBluePastelBackground,
                    unfocusedIndicatorColor = ColorBluePastelBackground,
                    focusedContainerColor = ColorBluePastelBackground,
                    focusedIndicatorColor = Color1,
                )
            ),
            FormItem(
                title = stringResource(id = R.string.confirm_password),
                placeholder = stringResource(id = R.string.enter_your_confirm_password),
                value = confirmPassword,
                onValueChange = {
                    onChangeConfirmPassword(it)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_lock_24),
                        contentDescription = null,
                        tint = Color1
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            painter = painterResource(
                                if (showConfirmPassword) {
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
                isError = isConfirmPasswordError,
                errorMessage = confirmPasswordError,
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = ColorBluePastelBackground,
                    unfocusedIndicatorColor = ColorBluePastelBackground,
                    focusedContainerColor = ColorBluePastelBackground,
                    focusedIndicatorColor = Color1,
                )
            ),
        )

        Column(
            Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {
                FormComp(
                    formData = formList,
                    onClickButton = { },
                    needSubmitButton = false
                )
            }
            Button(
                enabled = !isUpdatePasswordLoading,
                onClick = {
                    onUpdatePassword(setUpdatePasswordLoading)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color1,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isUpdatePasswordLoading) {
                    Box(
                        modifier = Modifier
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = true
                                )
                            )
                            .fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Change",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }

            }
        }
    }
}