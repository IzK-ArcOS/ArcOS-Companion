package com.blockyheadman.arcoscompanion.ui

import android.os.VibrationEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.blockyheadman.arcoscompanion.data.UserPreferences
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(externalPadding: PaddingValues) {
    val context = LocalContext.current

    val store = UserPreferences(context)

    var showThemeDialog by remember { mutableStateOf(false) }
    //var dynamicColor by remember { Settings.dynamicColor }
    //var darkThemeEnabled by remember { Settings.darkThemeEnabled }
    val darkThemeEnabled = store.getThemeMode.collectAsState(initial = 0)
    val dynamicColor = store.getMaterialYouMode.collectAsState(initial = false)

    Column (
        modifier = Modifier.padding(externalPadding)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Switch(checked = dynamicColor.value, onCheckedChange = {
                //dynamicColor = !dynamicColor
                CoroutineScope(Dispatchers.IO).launch {
                    store.saveMaterialYouMode(!dynamicColor.value)
                }
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
            })
            Spacer(Modifier.size(5.dp))
            Text("Enable Dynamic Color")
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                showThemeDialog = !showThemeDialog
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
            }) {
                Text("Change Theme")
            }
        }
    }
    if (showThemeDialog) {
        Dialog(onDismissRequest = { showThemeDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        "Change Theme",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Divider(
                        modifier = Modifier.padding(8.dp, 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("System Theme")
                        RadioButton(
                            selected = darkThemeEnabled.value == 0,
                            onClick = {
                                //darkThemeEnabled = 0
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveThemeMode(0)
                                }
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Light Theme")
                        RadioButton(
                            selected = darkThemeEnabled.value == 1,
                            onClick = {
                                //darkThemeEnabled = 1
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveThemeMode(1)
                                }
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dark Theme")
                        RadioButton(
                            selected = darkThemeEnabled.value == 2,
                            onClick = {
                                //darkThemeEnabled = 2
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveThemeMode(2)
                                }
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            })
                    }
                    Button(onClick = {
                        showThemeDialog = false
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                    }) {
                        Text("OK")
                    }

                }

            }
        }
    }
}