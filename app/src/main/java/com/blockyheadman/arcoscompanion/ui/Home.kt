package com.blockyheadman.arcoscompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.ui.theme.ArcOSCompanionTheme

@Composable
fun HomePage(externalPadding: PaddingValues) {
    Box (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        /*Text(
            text = "Welcome!\nThere's not much to see here..",
            textAlign = TextAlign.Center
        )*/
        MiniMessageCard()
    }
}

@Composable
fun MiniMessageCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.onPrimary, // TODO make color dark
            MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box (
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_public_24),
                    contentDescription = "public",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.End)
                        .padding(4.dp)
                )
            }
            Card(
                modifier = Modifier,//.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.inversePrimary,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(R.drawable.arcos_logo),
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(64.dp)
                    )
                    Text("Blockyheadman")
                }
            }
        }
        Text("FDSJFHSDKFHSJKDLFHJKSDF")
    }
}

@Preview
@Composable
fun PreviewMiniMessageCard() {
    ArcOSCompanionTheme(true) {
        MiniMessageCard()
    }
}