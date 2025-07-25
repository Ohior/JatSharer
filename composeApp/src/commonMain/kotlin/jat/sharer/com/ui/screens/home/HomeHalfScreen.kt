package jat.sharer.com.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import jat.sharer.com.FileInfo
import jat.sharer.com.JeyFile
import jat.sharer.com.models.StringAnnotation
import jat.sharer.com.rememberScreenSize
import jat.sharer.com.ui.AnnotatedText
import jat.sharer.com.ui.ImageSwitcher
import jat.sharer.com.ui.theme.PixelDensity
import jat.sharer.com.utils.Constants
import jat.sharer.com.utils.MediaType
import jat.sharer.com.utils.Tools
import jatsharer.composeapp.generated.resources.Res
import jatsharer.composeapp.generated.resources.docs
import jatsharer.composeapp.generated.resources.folder
import jatsharer.composeapp.generated.resources.image
import jatsharer.composeapp.generated.resources.musical_note
import jatsharer.composeapp.generated.resources.video
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilesHalfScreen(
    modifier: Modifier,
    files: List<JeyFile>,
    onSwipe: (DismissValue, JeyFile) -> Boolean
) {
    val size = rememberScreenSize()
    val isPortrait = remember(size) {
        mutableStateOf(size.first + size.second / 3.5 < size.second)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isPortrait.value) 1 else 2), // 2 columns
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PixelDensity.small),
        verticalArrangement = Arrangement.spacedBy(PixelDensity.medium)
    ) {
        items(items = files, key = {
            it.getFileInfo()[FileInfo.NAME] + Tools.generateSimpleUid(7)
        }) { file ->
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
                        .padding(horizontal = PixelDensity.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PixelDensity.medium)
                ) {
                    val fileInfo = remember { file.getFileInfo() }
                    Image(
                        painter = painterResource(
                            resource = when (MediaType.fromPath(fileInfo[FileInfo.NAME] ?: "*")) {
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
                    Column {
                        Text(
                            text = fileInfo[FileInfo.NAME] ?: "no title",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.background,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = Tools.formatByteSize(fileInfo[FileInfo.SIZE]?.toLong() ?: 0L),
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colors.background,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun InfoHalfScreen(modifier: Modifier = Modifier, infoPopup: () -> Unit) {
    Column(
        modifier = modifier,
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
                StringAnnotation(
                    text = "Share files between mobile phones, PC",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                        .toSpanStyle(),
                ),
                StringAnnotation(
                    text = " http://${Constants.HOST}:${Constants.PORT}",
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        textDecoration = TextDecoration.Underline
                    ).toSpanStyle(),
                    key = 1,
                    onClick = { infoPopup() }
                )
            )
        )
    }
}
