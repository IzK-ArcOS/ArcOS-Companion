package com.blockyheadman.arcoscompanion.ui

import android.os.VibrationEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.blockyheadman.arcoscompanion.vibrator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersPage(externalPadding: PaddingValues) {
    var showNewAPIDialog by remember { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier
            .padding(externalPadding),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                showNewAPIDialog = true
            }) {
                Text("New API")
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("There's not much to see here..")
        }
        if (showNewAPIDialog) {
            Dialog(onDismissRequest = { showNewAPIDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Column (Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                        Text("This feature is not yet available.")
                        Spacer(Modifier.size(8.dp))
                        Button(onClick = {
                            showNewAPIDialog = false
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}