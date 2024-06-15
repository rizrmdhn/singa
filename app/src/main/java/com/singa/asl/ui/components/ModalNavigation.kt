package com.singa.asl.ui.components

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.singa.asl.R
import com.singa.asl.ui.screen.conversation.ConversationViewModel
import com.singa.asl.ui.screen.history.HistoryScreenViewModel
import com.singa.asl.ui.theme.Color1
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ModalNavigation(
    context: Context = LocalContext.current,
    navigateToRealtimeCamera: () -> Unit,
    navigateToHistoryCamera: (String) -> Unit,
    navigateToConversation: (String) -> Unit,
    dismissBottomSheet: () -> Unit,
    viewModelConversation: ConversationViewModel = koinViewModel(),
    viewModelStatic: HistoryScreenViewModel = koinViewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            navigateToRealtimeCamera()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncherWithoutRedirect = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    var conversationDialog by remember { mutableStateOf(false) }
    var conversationTitle by remember { mutableStateOf("") }
    var staticDialog by remember { mutableStateOf(false) }
    var staticTitle by remember { mutableStateOf("") }


    Column(
        Modifier
            .fillMaxHeight(0.3f)
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Button(
            onClick = {
                when {
                    cameraPermissionState.hasPermission -> {
                        staticDialog = true
                    }

                    cameraPermissionState.shouldShowRationale -> {
                        Toast.makeText(
                            context,
                            "Permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        cameraPermissionLauncherWithoutRedirect.launch(Manifest.permission.CAMERA)
                        staticDialog = true
                    }
                }
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color1,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_video_camera_front_24),
                contentDescription = stringResource(id = R.string.upload_video),
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.upload_video),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                when {
                    cameraPermissionState.hasPermission -> {
                        conversationDialog = true
                    }

                    cameraPermissionState.shouldShowRationale -> {
                        Toast.makeText(
                            context,
                            "Permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        cameraPermissionLauncherWithoutRedirect.launch(Manifest.permission.CAMERA)
                        conversationDialog = true
                    }
                }
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color1,

                ),
            border = BorderStroke(2.dp, Color1),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_people_alt_24),
                contentDescription = stringResource(id = R.string.start_a_conversation),
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.start_a_conversation),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                when {
                    cameraPermissionState.hasPermission -> {
                        navigateToRealtimeCamera()
                    }

                    cameraPermissionState.shouldShowRationale -> {
                        Toast.makeText(
                            context,
                            "Permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color1,

                ),
            border = BorderStroke(2.dp, Color1),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_camera_enhance_24),
                contentDescription = stringResource(id = R.string.realtime),
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.realtime),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }

    if (staticDialog) {
        PopupInputAlertDialog(
            title = "Create a static translation",
            value = staticTitle,
            isLoading = viewModelStatic.createStaticStateIsLoading,
            onValueChange = {
                staticTitle = it
            },
            onDismissRequest = { staticDialog = false },
            confirmButton = {
                MainScope().launch {
                    navigateToHistoryCamera(staticTitle)
                }
            }
        )
    }

    if (conversationDialog) {
        PopupInputAlertDialog(
            title = "Create a conversation",
            value = conversationTitle,
            isLoading = viewModelConversation.createConversationStateIsLoading,
            onValueChange = { conversationTitle = it },
            onDismissRequest = { conversationDialog = false },
            confirmButton = {
                MainScope().launch {
                    viewModelConversation.createConversation(
                        title = conversationTitle,
                        navigateToConversation = { id ->
                            navigateToConversation(id)
                        }
                    )
                    // These lines will only be executed after the createConversation coroutine is done
                    conversationDialog = false
                    dismissBottomSheet()
                }
            }
        )
    }
}
