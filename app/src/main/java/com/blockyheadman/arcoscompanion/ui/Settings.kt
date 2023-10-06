package com.blockyheadman.arcoscompanion.ui

import android.graphics.Bitmap
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
import androidx.core.content.ContextCompat
import com.blockyheadman.arcoscompanion.NotificationIDs
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.data.UserPreferences
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

    Column (
        modifier = Modifier.padding(externalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Demo Version | v1.0.0.DEMO",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Switch(checked = dynamicColor.value, onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        store.saveMaterialYouMode(!dynamicColor.value)
                    }
                    vibrator.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    )
                })
                Spacer(Modifier.size(5.dp))
                Text("Enable Dynamic Color")
            }
        }

        Button(onClick = {
            showThemeDialog = !showThemeDialog
            vibrator.vibrate(
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
            val db = ContextCompat.getDrawable(context, R.drawable.bg)
            val bit = Bitmap.createBitmap(
                db!!.intrinsicWidth,
                db.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val notification = NotificationCompat.Builder(context, "ArcMailIncoming")
                .setSmallIcon(R.drawable.arcos_logo)
                .setContentTitle("Izaak Kuipers")
                .setContentText("1 new message")
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setLargeIcon(bit)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle("Test Message")
                        .setSummaryText("ArcMail")
                        .addLine("This is a test message for ArcMail notifications")
                )

            notificationManager.notify(NotificationIDs.NOTIFICATION_ID, notification.build())
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
                                vibrator.vibrate(
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
                                vibrator.vibrate(
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
                                vibrator.vibrate(
                                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                )
                            })
                    }
                    Button(onClick = {
                        showThemeDialog = false
                        vibrator.vibrate(
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