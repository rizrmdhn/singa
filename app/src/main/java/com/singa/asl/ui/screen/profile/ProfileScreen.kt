package com.singa.asl.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.singa.asl.R
import com.singa.asl.ui.components.ConfirmationDialog
import com.singa.asl.ui.components.shimmerBrush
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBackgroundWhite
import com.singa.asl.ui.theme.ColorBluePastelBackground
import com.singa.asl.ui.theme.ColorDanger
import com.singa.asl.utils.Helpers
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileScreen(
    avatarUrl: String,
    usedQuota: Int,
    totalQuota: Int,
    onLogout: (
            (title: String, message: String) -> Unit,
    ) -> Unit,
    logoutIsLoading: Boolean,
    onConfirmLogout: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToPassword: () -> Unit,
    viewModel: ProfileScreenViewModel = koinViewModel()
) {
    val alertDialog by viewModel.alertDialog.collectAsState()
    val alertDialogTitle by viewModel.alertDialogTitle.collectAsState()
    val alertDialogMessage by viewModel.alertDialogMessage.collectAsState()

    ProfileContent(
        avatarUrl = avatarUrl,
        usedQuota = usedQuota,
        totalQuota = totalQuota,
        onLogout = {
            onLogout(
                viewModel::showAlert
            )
        },
        logoutIsLoading = logoutIsLoading,
        alertDialog = alertDialog,
        alertDialogTitle = alertDialogTitle,
        alertDialogMessage = alertDialogMessage,
        hideDialog = viewModel::hideAlert,
        confirmDialog = onConfirmLogout,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToPassword = onNavigateToPassword
    )
}

@Composable
fun ProfileContent(
    avatarUrl: String,
    usedQuota: Int,
    totalQuota: Int,
    onLogout: () -> Unit,
    logoutIsLoading: Boolean,
    alertDialog: Boolean,
    alertDialogTitle: String,
    alertDialogMessage: String,
    hideDialog: () -> Unit,
    confirmDialog: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToPassword: () -> Unit
) {
    val roundedUsedQuota = String.format("%.2f", Helpers.bytesToMB(usedQuota.toLong()))
    val roundedTotalQuota = String.format("%.2f", Helpers.bytesToMB(totalQuota.toLong()))

    Box(
        Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 100.dp,
                ),
            colors = CardDefaults.cardColors(
                containerColor = ColorBackgroundWhite,
            ),
            shape = RoundedCornerShape(
                topStart = 40.dp,
                topEnd = 40.dp,
            )
        ) {
            Column(
                Modifier.padding(top = 140.dp)
            ) {
                ButtonAction(
                    image = R.drawable.baseline_people_alt_24,
                    text = "Detail Users",
                    onNavigate = onNavigateToDetail
                )

                Spacer(modifier = Modifier.height(16.dp))

                ButtonAction(
                    image = R.drawable.baseline_lock_24,
                    text = "Change Password",
                    onNavigate = onNavigateToPassword
                )

                Spacer(modifier = Modifier.height(16.dp))

                ButtonAction(
                    image = R.drawable.hugeicons_limitation,
                    text = "$roundedUsedQuota MB / $roundedTotalQuota MB",
                    tooltipText = "Used / Total Quota",
                    needArrow = false
                )

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    enabled = !logoutIsLoading,
                    onClick = {
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorDanger,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (logoutIsLoading) {
                        Box(
                            modifier = Modifier
                                .background(
                                    shimmerBrush(
                                        targetValue = 1300f,
                                        showShimmer = true
                                    )
                                )
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        )
                    } else {
                        Text(
                            text = "Logout",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .offset(y = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = avatarUrl,
                contentDescription = "profile",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                when (this.painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    shimmerBrush(
                                        targetValue = 1300f,
                                        showShimmer = true
                                    )
                                )
                                .size(180.dp)
                                .clip(RoundedCornerShape(20.dp)),
                        )
                    }

                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }

                    is AsyncImagePainter.State.Error -> {
                        Image(
                            painter = painterResource(id = R.drawable.boy_1),
                            contentDescription = "profile",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }

                    is AsyncImagePainter.State.Empty -> {
                        Image(
                            painter = painterResource(id = R.drawable.boy_1),
                            contentDescription = "profile",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }
        }
    }

    if (alertDialog) {
        ConfirmationDialog(
            title = alertDialogTitle,
            text = alertDialogMessage,
            onDismissRequest = hideDialog,
            confirmButton = confirmDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonAction(
    image: Int,
    text: String,
    tooltipText: String = "",
    onNavigate: () -> Unit = {},
    needArrow: Boolean = true
) {
    Button(
        onClick = onNavigate,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorBluePastelBackground,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = "People",
                    tint = Color1,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = text, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(16.dp))
                if (tooltipText.isNotBlank()) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(tooltipText, fontSize = 16.sp)
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = Color1
                        )
                    }
                }
            }
            if (
                needArrow
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Arrow Right",
                    modifier = Modifier.size(32.dp),
                    tint = Color1
                )
            }
        }
    }
}