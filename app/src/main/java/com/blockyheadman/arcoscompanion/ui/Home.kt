package com.blockyheadman.arcoscompanion.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun HomePage(externalPadding: PaddingValues) {
    Box (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome!\nThere's not much to see here..",
            textAlign = TextAlign.Center
        )
    }
}