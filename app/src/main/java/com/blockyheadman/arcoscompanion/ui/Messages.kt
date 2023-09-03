package com.blockyheadman.arcoscompanion.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MessagesPage(externalPadding: PaddingValues) {
    Box (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("There's not much to see here..")
    }
}