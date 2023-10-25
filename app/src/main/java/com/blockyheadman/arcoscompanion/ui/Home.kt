package com.blockyheadman.arcoscompanion.ui

import android.os.VibrationEffect
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.hapticsEnabled
import com.blockyheadman.arcoscompanion.ui.theme.ArcOSCompanionTheme
import com.blockyheadman.arcoscompanion.vibrator
import kotlin.math.roundToInt

@Composable
fun HomePage(externalPadding: PaddingValues) {
    Box (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        //contentAlignment = Alignment.Center
    ) {
        /*Text(
            text = "Welcome!\nThere's not much to see here..",
            textAlign = TextAlign.Center
        )*/
        LazyColumn {
            item { HomeApiCard() }
        }
    }

}

// TODO get home api card and mini message cards to use input data
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeApiCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.primary
        )
    ) {
        val density = LocalDensity.current
        val defaultActionSize = 60.dp
        val startActionSizePx = with(density) { -(defaultActionSize).toPx() }
        val endActionSizePx = with(density) { (defaultActionSize).toPx() }

        val cardSwipeState = remember { AnchoredDraggableState(
            0f,
            anchors = DraggableAnchors {
                0f at 0f//startActionSizePx
                //0.5f at 0f
                1f at -endActionSizePx
            },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween()
        ) }

        Box (
            modifier = Modifier.height(80.dp)
        ) {
            val context = LocalContext.current

            Box (
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                FilledIconButton(
                    onClick = {
                        if(hapticsEnabled.value) vibrator.vibrate(
                            VibrationEffect.createPredefined(
                                VibrationEffect.EFFECT_DOUBLE_CLICK
                            )
                        )
                        Toast.makeText(
                            context,
                            "Feature not yet available.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .width(defaultActionSize),
                    shape = RoundedCornerShape(size = 16.dp),
                    colors = IconButtonColors(
                        containerColor = Color(0xFF9E0000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF360000),
                        disabledContentColor = Color(0xFF9E9E9E)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .offset {
                        IntOffset(
                            x = cardSwipeState
                                .requireOffset()
                                .roundToInt(),
                            y = 0
                        )
                    }
                    .anchoredDraggable(
                        cardSwipeState,
                        Orientation.Horizontal,
                        enabled = true,
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row (
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.inversePrimary,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                        ) {
                            Spacer(Modifier.width(8.dp))
                            Image(
                                painterResource(R.drawable.launcher_icon_background),
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Blocky",
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "community.arcapi.nl",
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.baseline_public_24),
                        contentDescription = "public",
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Messages",
            modifier = Modifier.padding(start = 12.dp)
        )
        Column {
            MiniMessageCard()
            MiniMessageCard()
            MiniMessageCard()
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun MiniMessageCard() {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Spacer(Modifier.height(8.dp))
        Row {
            Spacer(Modifier.width(6.dp))
            Image(
                painterResource(R.drawable.launcher_icon_background),
                contentDescription = "message profile picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Izaak Kuipers",
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Test title!",
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This is a body for a message on the home screen...",
                    softWrap = true,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Preview
@Composable
fun PreviewHomeApiCard() {
    ArcOSCompanionTheme(true) {
        HomeApiCard()
    }
}