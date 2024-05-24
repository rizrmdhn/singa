package com.singa.asl.ui.screen.profile_detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import com.singa.asl.R
import com.singa.asl.ui.components.InputForm
import com.singa.asl.ui.components.shimmerBrush
import com.singa.asl.ui.theme.Color1
import com.singa.asl.ui.theme.ColorBackgroundWhite
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileDetailScreen(
    context: Context = LocalContext.current,
    avatarUrl: String,
    name: String,
    onChangeName: (String) -> Unit,
    email: String,
    onChangeEmail: (String) -> Unit,
    onUpdate: (
        uri: Uri,
        setIsLoading: (Boolean) -> Unit,
    ) -> Unit,
    viewModel: ProfileDetailScreenViewModels = koinViewModel()
) {
    val uri by viewModel.uri.collectAsState()
    val isUpdateProfileLoading by viewModel.isUpdateProfileLoading.collectAsState()

    ProfileDetailContent(
        context = context,
        uri = uri,
        setUri = viewModel::setUri,
        avatarUrl = avatarUrl,
        name = name,
        onChangeName = onChangeName,
        email = email,
        onChangeEmail = onChangeEmail,
        onUpdate = {
            onUpdate(
                uri,
                viewModel::setIsUpdateProfileLoading
            )
        },
        isUpdateProfileLoading = isUpdateProfileLoading
    )
}

@Composable
fun ProfileDetailContent(
    context: Context,
    uri: Uri,
    setUri: (Uri) -> Unit,
    avatarUrl: String,
    name: String,
    onChangeName: (String) -> Unit,
    email: String,
    onChangeEmail: (String) -> Unit,
    onUpdate: () -> Unit,
    isUpdateProfileLoading: Boolean
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            setUri(it)
        } else {
            Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show()
            setUri(Uri.EMPTY)
        }
    }

    val galleryPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            launcher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }




    Box(Modifier.fillMaxWidth()) {
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
                Modifier
                    .padding(top = 140.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.padding(16.dp)) {
                    InputForm(
                        title = "Name",
                        icon = R.drawable.baseline_people_alt_24,
                        value = name,
                        onChange = onChangeName
                    )
                    InputForm(
                        title = "Email",
                        icon = R.drawable.baseline_email_24,
                        value = email,
                        onChange = onChangeEmail
                    )
                }
                Button(
                    enabled = !isUpdateProfileLoading,
                    onClick = onUpdate,
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
                    if (isUpdateProfileLoading) {
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
                        Text(text = "Save", fontSize = 24.sp, color = Color.White)
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
                model = if (uri != Uri.EMPTY) uri else avatarUrl,
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
        Box(
            Modifier.offset(x = 180.dp, y = 92.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = 76.dp, y = 76.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color1,
                    contentColor = Color.White
                ),
                onClick = {
                    val permissionCheckResult =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_MEDIA_IMAGES
                            )
                        } else {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        launcher.launch("image/*")
                    } else {
                        // Request a permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            galleryPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            galleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
        }

    }
}