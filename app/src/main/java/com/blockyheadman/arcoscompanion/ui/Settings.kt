package com.blockyheadman.arcoscompanion.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.data.UserPreferences
import com.blockyheadman.arcoscompanion.hapticsEnabled
import com.blockyheadman.arcoscompanion.notificationManager
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(externalPadding: PaddingValues) {
    val context = LocalContext.current

    val store = UserPreferences(context)

    var showThemeDialog by remember { mutableStateOf(false) }
    val dynamicColor = store.getMaterialYouMode.collectAsState(initial = false)
    val darkThemeEnabled = store.getThemeMode.collectAsState(initial = 0)
    hapticsEnabled = store.getHapticsMode.collectAsState(initial = false)

    var id = 0

    Column (
        modifier = Modifier.padding(externalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Version | v1.0.0",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Switch(
                checked = dynamicColor.value,
                onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        store.saveMaterialYouMode(!dynamicColor.value)
                    }
                    if(hapticsEnabled.value) vibrator.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    )
                },
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            )
            Spacer(Modifier.size(5.dp))
            Text("Enable Dynamic Color")
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Switch(checked = hapticsEnabled.value, onCheckedChange = {
                CoroutineScope(Dispatchers.IO).launch {
                    store.saveHapticsMode(!hapticsEnabled.value)
                }
                if(hapticsEnabled.value) vibrator.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                )
            })
            Spacer(Modifier.size(5.dp))
            Text("Enable Haptics")
        }

        Button(onClick = {
            showThemeDialog = !showThemeDialog
            if(hapticsEnabled.value) vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            )
        }) {
            Text("Change Theme")
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Developer Stuffs",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(
            modifier = Modifier.padding(8.dp, 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Button(onClick = {
            if(hapticsEnabled.value) vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            )
            testNotification(context, id)
            id++
        }) {
            Text("Test ArcMail Notification")
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
                    HorizontalDivider(
                        modifier = Modifier.padding(8.dp, 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("System Theme")
                        RadioButton(
                            selected = darkThemeEnabled.value == 0,
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveThemeMode(0)
                                }
                                if(hapticsEnabled.value) vibrator.vibrate(
                                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                )
                            })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Light Theme")
                        RadioButton(
                            selected = darkThemeEnabled.value == 1,
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveThemeMode(1)
                                }
                                if(hapticsEnabled.value) vibrator.vibrate(
                                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                )
                            })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dark Theme")
                        RadioButton(
                            selected = darkThemeEnabled.value == 2,
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveThemeMode(2)
                                }
                                if(hapticsEnabled.value) vibrator.vibrate(
                                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                )
                            })
                    }
                    Button(onClick = {
                        showThemeDialog = false
                        if(hapticsEnabled.value) vibrator.vibrate(
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                        )
                    }) {
                        Text("OK")
                    }

                }

            }
        }
    }
}

fun testNotification(context: Context, id: Int) {
    val bit = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.launcher_icon_background
    )

    val notification = NotificationCompat.Builder(context, "ArcMailIncoming")
        .setSmallIcon(R.drawable.arcos_logo)
        .setContentTitle("Izaak Kuipers")
        .setContentText("1 new message")
        .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
        .setLargeIcon(bit)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .setBigContentTitle("Test Message")
                .setSummaryText("ArcMail")
                .bigText(
                    "This is a test message for ArcMail notifications.\nNotification ID: $id"
                )
        )

    notificationManager.notify(id, notification.build())
}