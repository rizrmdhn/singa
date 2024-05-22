package com.singa.core.domain.model

import android.graphics.drawable.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

data class FormItem(
    val title: String,
    val placeholder: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val visualTransformation: VisualTransformation,
    val leadingIcon: @Composable (() -> Unit),
    val trailingIcon: @Composable (() -> Unit)? = null,
    val keyboardOptions: KeyboardOptions,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val shape: Shape = RoundedCornerShape(8.dp),
    val modifier: Modifier = Modifier,
    val colors: TextFieldColors? = null,
)