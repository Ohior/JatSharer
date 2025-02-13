package jat.sharer.com.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import jat.sharer.com.core.ServerManager
import jat.sharer.com.models.DeviceFile
import jat.sharer.com.models.StringAnotation
import jat.sharer.com.ui.AnnotatedText
import jat.sharer.com.ui.ImageSwitcher
import jat.sharer.com.ui.TextIcon
import jat.sharer.com.ui.drawUnderLine
import jat.sharer.com.ui.theme.PixelDensity
import jat.sharer.com.utils.Constants
import jat.sharer.com.utils.MediaType
import jat.sharer.com.utils.SnippetTools
import jatsharer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(title = {
                    Text(
                        text = "JatShare",
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                    )
                })
            }
        ) { pv ->
            val homeViewModel = rememberScreenModel { HomeViewModel() }
            val serverState by ServerManager.listenToServer.collectAsState(false)
            Column(modifier = Modifier.fillMaxSize().padding(pv)) {
                // DISPLAY FILES IF SELECTED ELSE DISPLAY INFO
                DisplaySelectedFiles(
                    modifier = Modifier
                        .weight(1f)
                        .padding(PixelDensity.small),
                )
                // DISPLAY START STOP SERVER BUTTON
                ServerButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(topEnd = PixelDensity.large, topStart = PixelDensity.large)
                        )
                        .padding(PixelDensity.extraLarge),
                    homeViewModel = homeViewModel,
                    active = serverState
                )
            }
        }
    }


    @Composable
    private fun DisplaySelectedFiles(
        modifier: Modifier,
        vmodel: HomeViewModel = rememberScreenModel { HomeViewModel() }
    ) {
        val deviceFiles by vmodel.deviceFiles.collectAsState(emptyList())
        if (deviceFiles.isEmpty()) {
            DisplayNoFile(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
            )
        } else {
            DisplayFiles(
                modifier = modifier,
                files = deviceFiles
            ) { dv, df ->
                if (dv == DismissValue.DismissedToStart) {
                    vmodel.deleteDeviceFiles(df)
                    true
                } else false
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun DisplayFiles(
        modifier: Modifier,
        files: List<DeviceFile>,
        onSwipe: (DismissValue, DeviceFile) -> Boolean
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 columns
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(PixelDensity.small),
            verticalArrangement = Arrangement.spacedBy(PixelDensity.medium)
        ) {
            items(items = files, key = { it.hashId }) { file ->
                val swipeState = rememberDismissState {
                    onSwipe(it, file)
                }
                SwipeToDismiss(
                    state = swipeState,
                    background = {
                        val (color1, color2) = when (swipeState.dismissDirection) {
                            DismissDirection.EndToStart -> Pair(Color.Red, Color.Black)
                            else -> Pair(Color.Transparent, Color.Transparent)
                        }
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .clip(RoundedCornerShape(PixelDensity.medium))
                                .background(color1)
                                .padding(horizontal = PixelDensity.medium),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = color2
                            )
                        }
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(PixelDensity.medium))
                            .background(MaterialTheme.colors.primary)
                            .padding(
                                top = PixelDensity.small,
                                start = PixelDensity.small,
                                end = PixelDensity.small
                            )
                            .drawUnderLine(file.fileColor),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PixelDensity.medium)
                    ) {
                        Image(
                            painter = painterResource(
                                resource = when (file.mediaType) {
                                    MediaType.Image -> Res.drawable.image
                                    MediaType.Audio -> Res.drawable.musical_note
                                    MediaType.Document -> Res.drawable.docs
                                    MediaType.Unknown -> Res.drawable.folder
                                    MediaType.Video -> Res.drawable.video
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(PixelDensity.large * 2)
                        )
                        Column() {
                            Text(
                                text = file.name,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = SnippetTools.formatByteSize(file.size ?: 0),
                                fontWeight = FontWeight.Light,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(topStart = PixelDensity.large, topEnd = PixelDensity.large))
//                            .background(MaterialTheme.colors.secondary.copy(alpha = 0.5f))
//                            .drawUnderLine(file.fileColor)
//                            .padding(top = PixelDensity.small, start = PixelDensity.small, end = PixelDensity.small),
//                    ) {
//                        Text(
//                            text = file.name,
//                            fontWeight = FontWeight.SemiBold,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                        Text(
//                            modifier = Modifier.fillMaxWidth(),
//                            textAlign = TextAlign.End,
//                            fontWeight = FontWeight.SemiBold,
//                            color = MaterialTheme.colors.primary,
//                            text = if (file.size != null) SnippetTools.formatByteSize(file.size) else "NAN",
//                        )
//
//                    }
                }
            }
        }
    }

    @Composable
    private fun DisplayNoFile(modifier: Modifier) {
        var infoPopup by remember { mutableStateOf(false) }
        if (infoPopup) {
            AlertDialog(
                onDismissRequest = { infoPopup = false },
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(vertical = PixelDensity.medium),
                        textAlign = TextAlign.Center,
                        text = "How to use",
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Constants.HOWTO,
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                buttons = {
                    TextIcon(
                        modifier = Modifier.fillMaxWidth()
                            .padding(PixelDensity.medium)
                            .clip(RoundedCornerShape(PixelDensity.extraLarge))
                            .background(MaterialTheme.colors.onSurface)
                            .padding(PixelDensity.medium)
                            .clickable { infoPopup = false },
                        text = "Close",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.surface
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colors.surface
                            )
                        })
                },
                backgroundColor = Color.LightGray,
                shape = RoundedCornerShape(topStart = PixelDensity.medium, topEnd = PixelDensity.medium)
            )
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(PixelDensity.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ImageSwitcher(
                modifier = Modifier
                    .padding(PixelDensity.large)
                    .size(PixelDensity.large * 5),
                images = listOf(
                    Res.drawable.image,
                    Res.drawable.folder,
                    Res.drawable.musical_note,
                    Res.drawable.video,
                    Res.drawable.docs
                ),
            )
            AnnotatedText(
                texts = listOf(
                    StringAnotation(
                        text = "Share files between mobile phones, PC",
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
                    ),
                    StringAnotation(
                        text = " http://${Constants.HOST}:${Constants.PORT}",
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary,
                            textDecoration = TextDecoration.Underline
                        ).toSpanStyle(),
                        key = 1,
                        onClick = { infoPopup = true }
                    )
                )
            )
        }
    }

    @Composable
    private fun SelectShareButton(
        modifier: Modifier,
        homeViewModel: HomeViewModel,
        active: Boolean
    ) {
        // Pick files from Compose
        val launcher = rememberFilePickerLauncher(
            mode = PickerMode.Multiple(5)
        ) { files ->
            // Handle picked files
            homeViewModel.makeDeviceFiles(files)
        }
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                enabled = !active,
                modifier = Modifier
                    .border(
                        color = if (!active) Color.Black else Color.Black.copy(alpha = 0.3f),
                        width = PixelDensity.verySmall,
                        shape = RoundedCornerShape(PixelDensity.large)
                    )
                    .padding(PixelDensity.verySmall),//.drawUnderLine(Color.Black, PixelDensity.verySmall),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.secondary
                ),
                onClick = {
                    launcher.launch()
                }) {
                Text(
                    text = "Select Files",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(Modifier.width(PixelDensity.medium))
            TextButton(
                enabled = active,
                modifier = Modifier
                    .border(
                        color = if (active) Color.Black else Color.Black.copy(alpha = 0.3f),
                        width = PixelDensity.verySmall,
                        shape = RoundedCornerShape(PixelDensity.large)
                    )
                    .padding(PixelDensity.verySmall),//.drawUnderLine(Color.Black, PixelDensity.verySmall),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.secondary
                ),
                onClick = {
                    homeViewModel.pickFile()
                }) {
                Text(
                    text = "Share Files",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }


    @Composable
    private fun ServerButton(
        modifier: Modifier,
        active: Boolean,
        homeViewModel: HomeViewModel,
    ) {
        // Pick files from Compose
        val launcher = rememberFilePickerLauncher(
            mode = PickerMode.Multiple(5)
        ) { files ->
            // Handle picked files
            homeViewModel.makeDeviceFiles(files)
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextIcon(
                modifier = Modifier
                    .clip(RoundedCornerShape(14f))
                    .background(if (active) Color.Green else Color.Red)
                    .clickable(onClick = {
                        if (active)
                            ServerManager.stopServer()
                        else {
                            ServerManager.startServer()
                        }
                    })
                    .padding(PixelDensity.medium),
                text = "${if (active) "Stop" else "Start"} Server",
                style = MaterialTheme.typography.h6.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                ),
                leadingIcon = {
                    if (active) {
                        Icon(
                            painterResource(Res.drawable.baseline_toggle_on_24),
                            modifier = Modifier.size(PixelDensity.large),
                            contentDescription = "Server is active"
                        )
                    } else {
                        Icon(
                            painterResource(Res.drawable.baseline_toggle_off_24),
                            modifier = Modifier.size(PixelDensity.large),
                            contentDescription = "Server is inactive"
                        )
                    }
                }
            )
            TextIcon(
                modifier = Modifier
                    .clip(RoundedCornerShape(14f))
                    .background(MaterialTheme.colors.primary)
                    .clickable(enabled = !active, onClick = {
                    launcher.launch()
                    })
                    .padding(PixelDensity.medium),
                text = "Select Files",
                style = MaterialTheme.typography.h6.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                ),
                leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_file_open_24),
                            modifier = Modifier.size(PixelDensity.large),
                            contentDescription = "Select Files"
                        )
                }
            )
        }
    }

}
