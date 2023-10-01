package com.blockyheadman.arcoscompanion.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.blockyheadman.arcoscompanion.NotificationIDs
import com.blockyheadman.arcoscompanion.data.Message
import com.blockyheadman.arcoscompanion.data.MessageData
import com.blockyheadman.arcoscompanion.data.network.ApiCall
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesPage(externalPadding: PaddingValues) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("MessagesPage", "PERMISSION GRANTED")
        } else {
            // Permission Denied: Do something
            Log.d("MessagesPage", "PERMISSION DENIED")
        }
        permissionGranted = isGranted
    }

    permissionGranted = when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) -> true
        else -> false
    }

    Box (
        Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text("Enable Notifications")
                }
            },
            state = rememberTooltipState()
        ) {
            ElevatedButton(
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) -> {
                            Log.d("MessagesPage", "Code requires permission")
                            Toast.makeText(
                                context,
                                "Notifications are already enabled!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.d("MessagesPage", "Requesting permission")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else requestNotifications(context)
                        }
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (!permissionGranted) Icons.Outlined.Notifications
                        else Icons.Filled.Notifications,
                        contentDescription = "Notification Icon"
                    )
                    //Text("Turn on Notifications")
                }
            }
        }
    }

    Box (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //Text("There's not much to see here..")

        //val mainContext = LocalContext.current

        // MessageCard()

        var buttonClicked by rememberSaveable { mutableStateOf(false) }

        Button(onClick = {
            Log.d("TestButton", "Button Pressed")
            buttonClicked = true
        }) {
            Text("test messages receiver")
        }

        if (buttonClicked) {
            val messageRequest = ApiCall()
            var messageData: Message?

            LaunchedEffect(messageRequest) {
                coroutineScope {
                    messageData = messageRequest.getMessages(
                        "community.arcapi.nl",
                        "58e8dd29-e368-46ac-a060-6d83f139cf2e"
                    )
                }
                if (messageRequest.errorMessage.isEmpty()) {
                    Log.d("BUTTONRETURN", messageData.toString())
                } else {
                    Log.e("BUTTONRETURN", messageRequest.errorMessage)
                }
                buttonClicked = false
            }
        }
    }

}

@Preview
@Composable
fun MessageCard(/*messageInfo*/) {

    val messageInfo = Message(
        true,
        listOf(
            MessageData(
                "Blocky",
                "Izaak Kuipers",
                "This is a test body for a message card",
                1234567890,
                "",
                69,
                false
            )
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text("From: ${messageInfo.data[0].sender}")
        Text("To: ${messageInfo.data[0].receiver}")
        Spacer(Modifier.height(8.dp))
        Text(messageInfo.data[0].partialBody)
    }
}

fun requestNotifications(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        context.startActivity(
            Intent(
                Settings.ACTION_APP_NOTIFICATION_SETTINGS
            )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(
                    Settings.EXTRA_APP_PACKAGE,
                    context.packageName
                )
                .putExtra(
                    Settings.EXTRA_CHANNEL_ID,
                    NotificationIDs.NOTIFICATION_ID
                )
        )
    }
}