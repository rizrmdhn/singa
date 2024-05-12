package com.singa.asl.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.singa.asl.R
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.Color2

@Composable
fun FloatingButton(
    modalButtonNavigation:()->Unit,
) {
    FloatingActionButton(
        modifier = Modifier
            .offset(y = 60.dp)
            .size(80.dp)
            .border(
                width = 4.dp,
                color = Color2,
                shape = CircleShape
            ),
        shape = CircleShape,
        onClick = {
            modalButtonNavigation()
        },
        containerColor = Color1,
        contentColor = Color.White
    ) {
        Icon(
            painter = painterResource(id = R.drawable.lucide_scan_line),
            contentDescription = "Home",
            modifier = Modifier.size(40.dp),
            tint = Color.White
        )
    }
}