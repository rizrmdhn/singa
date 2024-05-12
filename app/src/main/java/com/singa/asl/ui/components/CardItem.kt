package com.singa.asl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItem(
    onClickCard: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onClickCard,
        border = BorderStroke(1.dp, color = Color1),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Surface(
                    color = Color3,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_library_books_24),
                        contentDescription = "logo",
                        modifier = Modifier.padding(10.dp),
                        tint = Color1
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Video Games",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "20 June 2023",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "transcript",
                modifier = Modifier.size(30.dp),
                tint = Color1
            )
        }
    }
}