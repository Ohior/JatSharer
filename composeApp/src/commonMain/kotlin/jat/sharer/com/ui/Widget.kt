package jat.sharer.com.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import jat.sharer.com.models.StringAnnotation
import jat.sharer.com.ui.theme.PixelDensity
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun TextIcon(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    leadingIcon: @Composable (() -> Unit?)? = null,
    trailingIcon: @Composable (() -> Unit?)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(PixelDensity.small))
        }
        Text(text = text, style = style)
        if (trailingIcon != null) {
            Spacer(Modifier.width(PixelDensity.small))
            trailingIcon()
        }
    }
}

@Composable
fun MyHorizontalPager(
    modifier: Modifier,
    pagerCount: Int,
    content: @Composable (Int) -> Unit
) {
    val pagerState = rememberPagerState { pagerCount }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
//            contentPadding = PaddingValues(start = PixelDensity.extraLarge),
        ) { page ->
            content(page)
        }
        Row(
            modifier = Modifier
                .padding(bottom = PixelDensity.small),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = modifier
                        .padding(PixelDensity.small)
                        .background(color)
                        .size(PixelDensity.small)
                )
            }
        }
    }
}

@Composable
fun AnnotatedText(
    modifier: Modifier = Modifier,
    texts: List<StringAnnotation>,
    textAlign: TextAlign = TextAlign.Center
) {
    val annotatedString = buildAnnotatedString {
        texts.forEach { text ->
            if (text.onClick == null) {
                withStyle(style = text.style) {
                    append(text.text+" ")
                }
            } else {
                withLink(
                    LinkAnnotation.Clickable(
                        "url",
                        TextLinkStyles(style = text.style)
                    ) {
                        text.onClick.invoke(text.key)
                    }
                ) {
                    append(text.text)
                }
            }
        }
    }
    Text(modifier = modifier,text = annotatedString, textAlign = textAlign)
}


@Composable
fun Modifier.createShimmer(colors: List<Color>): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition()
    val startOffset by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(startOffset, 0f),
            end = Offset(startOffset + size.width, size.height.toFloat())
        )
    )
        .onGloballyPositioned { size = it.size }
}

@Composable
fun Modifier.drawUnderLine(color: Color, thickness: Dp = PixelDensity.verySmall): Modifier = composed {
    drawBehind {
        val underlineThickness = thickness.toPx() // Thickness of the underline
        // Calculate the start and end positions of the underline
        val startX = 0f
        val endX = size.width
        val baselineY = size.height// + 10
        // Draw the underline
        drawLine(
            color = color, // Color of the underline
            start = Offset(startX, baselineY),
            end = Offset(endX, baselineY),
            strokeWidth = underlineThickness
        )
    }
}


@Composable
fun ImageSwitcher(modifier: Modifier, images: List<DrawableResource>) {
    // Remember the current image index
    var currentIndex by remember { mutableStateOf(0) }

    // Update the image index every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000L) // Wait for 10 seconds
            currentIndex = (currentIndex + 1) % images.size // Cycle through the images
        }
    }

    // Display the current image
    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            slideInVertically { height -> height } + fadeIn() togetherWith
                    slideOutVertically { height -> -height } + fadeOut()
//        // Compare the incoming number with the previous number.
//        if (targetState > initialState) {
//            // If the target number is larger, it slides up and fades in
//            // while the initial (smaller) number slides up and fades out.
//            slideInVertically { height -> height } + fadeIn() togetherWith
//                    slideOutVertically { height -> -height } + fadeOut()
//        } else {
//            // If the target number is smaller, it slides down and fades in
//            // while the initial number slides down and fades out.
//            slideInVertically { height -> -height } + fadeIn() togetherWith
//                    slideOutVertically { height -> height } + fadeOut()
//        }.using(
//            // Disable clipping since the faded slide-in/out should
//            // be displayed out of bounds.
//            SizeTransform(clip = false)
//        )
        }) {
        Image(
            painter = painterResource(resource = images[currentIndex]),
            contentDescription = "appearing and disappearing image",
            modifier = modifier
        )
    }
}
