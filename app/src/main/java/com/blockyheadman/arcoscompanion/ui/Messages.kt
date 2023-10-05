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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.blockyheadman.arcoscompanion.NotificationIDs
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.apis
import com.blockyheadman.arcoscompanion.data.MessageData
import com.blockyheadman.arcoscompanion.data.MessageList
import com.blockyheadman.arcoscompanion.data.navBarItems
import com.blockyheadman.arcoscompanion.data.network.ApiCall
import com.blockyheadman.arcoscompanion.getToken
import com.blockyheadman.arcoscompanion.ui.theme.ArcOSCompanionTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    var apiTabIndex by rememberSaveable { mutableIntStateOf(0) }

    permissionGranted = when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) -> true
        else -> false
    }

    Scaffold (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        topBar = {
            ScrollableTabRow(selectedTabIndex = apiTabIndex, tabs = {
                apis.forEach { api ->
                    Tab(
                        selected = apiTabIndex == apis.indexOf(api),
                        onClick = { apiTabIndex = apis.indexOf(api) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text("(${api.username})")
                        Text(
                            text = api.name,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                    }
                }
            })
        }
        //contentAlignment = Alignment.Center
    ) { innerPadding ->
        //Text("There's not much to see here..")

        //val mainContext = LocalContext.current

        var messageData: MessageList? by rememberSaveable { mutableStateOf(null) }
        var messageDataError by rememberSaveable { mutableStateOf(0) }

        // TODO Get apis to show cards of messages from specific servers.

        LaunchedEffect(Unit) {
            coroutineScope {
                launch(Dispatchers.IO) {
                    messageData = null
                    messageDataError = 0

                    val token: String? = async {
                        getToken(
                            apis[apiTabIndex].name,
                            apis[apiTabIndex].username,
                            apis[apiTabIndex].password,
                            apis[apiTabIndex].authCode
                        )
                    }.await()

                    if (!token.isNullOrBlank()) {
                        Log.e("GET MESSAGES", "Empty token. Stopping..")
                        messageDataError = 1
                        return@launch
                    }

                    Log.d("GET MESSAGES", "Api name: ${apis[apiTabIndex].name}")
                    Log.d("GET MESSAGES", "Token: $token")
                    messageData = token?.let {
                        getMessages(
                            apis[apiTabIndex].name,
                            it // TODO get token to save in api save.
                        )
                    }

                }
            }
        }

        when (messageDataError) {
            1 -> Toast.makeText(
                context,
                "API information is incorrect",
                Toast.LENGTH_SHORT
            ).show()
        }

        LazyColumn (
            Modifier.padding(innerPadding)//.padding(top = 52.dp)
        ) {
            messageData?.data?.let { list ->
                items(list.size) {
                    MessageCard(messageData!!.data[it])
                }
            }
        }

        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Row {
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
                        Icon(
                            imageVector = if (!permissionGranted) Icons.Outlined.Notifications
                            else Icons.Filled.Notifications,
                            contentDescription = if (!permissionGranted) "Enable notifications"
                            else "Notifications enabled"
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text("Reload Messages")
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    ElevatedButton(
                        onClick = {
                            Log.d("GET MESSAGES", "Api name: ${apis[apiTabIndex].name}")
                            Log.d("GET MESSAGES", "Api auth code: ${apis[apiTabIndex].authCode}")
                            val scope = CoroutineScope(Job())
                            scope.launch {
                                messageData = null
                                messageDataError = 0

                                val token: String? = async {
                                    getToken(
                                        apis[apiTabIndex].name,
                                        apis[apiTabIndex].username,
                                        apis[apiTabIndex].password,
                                        apis[apiTabIndex].authCode
                                    )
                                }.await()

                                if (token.isNullOrBlank()) {
                                    Log.e("GET MESSAGES", "Empty token. Stopping..")
                                    messageDataError = 1
                                    return@launch
                                }

                                Log.d("GET MESSAGES", "Api name: ${apis[apiTabIndex].name}")
                                Log.d("GET MESSAGES", "Token: $token")
                                messageData = getMessages(
                                    apis[apiTabIndex].name,
                                    token // TODO get token to save in api save.
                                )

                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload"
                        )
                    }
                }
            }
        }

    }

}

@Composable
fun MessageCard(messageInfo: MessageData) {
    val context = LocalContext.current

    var settingsExpanded by rememberSaveable { mutableStateOf(false) }

    val bodyContents = messageInfo.partialBody.split("\n", limit = 2)

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
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Row {
                Column (modifier = Modifier.weight(1f)) {
                    Text(
                        "From: ${messageInfo.sender}",
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "To: ${messageInfo.receiver}",
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = { settingsExpanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options"
                    )

                    DropdownMenu(
                        expanded = settingsExpanded,
                        onDismissRequest = { settingsExpanded = false }
                    ) {

                        DropdownMenuItem(text = { Text("Reply") },
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Feature not yet available",
                                    Toast.LENGTH_SHORT
                                ).show()
                                settingsExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.baseline_reply_24),
                                    contentDescription = "Reply"
                                )
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Title: " +
                bodyContents[0].removePrefix("### "),
                fontWeight = FontWeight.W600,
                fontSize = 20.sp
            )
            Text(
                bodyContents[1],
                fontWeight = FontWeight.W400,
                fontSize = 16.sp
            )
        }
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

suspend fun getMessages(apiName: String, authToken: String): MessageList? {
    val messageRequest = ApiCall()
    var messageData: MessageList? = null

    coroutineScope {
        runBlocking {
            launch(Dispatchers.IO) {
                messageData = messageRequest.getMessages(
                    apiName,
                    authToken
                )
            }
        }
        if (messageRequest.errorMessage.isEmpty()) {
            Log.d("MessageData", messageData.toString())
        } else {
            Log.e("MessageDataError", messageRequest.errorMessage)
        }
    }

    return messageData
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MessagesPreview() {
    ArcOSCompanionTheme(darkTheme = true, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Messages")
                            }
                        },
                        navigationIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.arcos_logo),
                                contentDescription = "ArcOS logo",
                                modifier = Modifier.size(48.dp)
                            )
                        }

                    )
                },
                bottomBar = {
                    BottomAppBar {
                        navBarItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == 2,
                                onClick = {},
                                icon = {
                                    if (index == 2) {
                                        Icon(
                                            imageVector = item.iconFilled,
                                            contentDescription = null
                                        )
                                    } else {
                                        Icon(
                                            imageVector = item.iconOutlined,
                                            contentDescription = null
                                        )
                                    }
                                },
                                label = { Text(item.name) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                MessagesPage(innerPadding)
            }
        }
    }
}

@Preview
@Composable
fun MessageCardPreview() {
    val messageInfo = MessageData(
        "Blocky",
        "Izaak Kuipers",
        "### This is a pretty long title for testing things\nThis is a test body for a message card that's pretty long and should wrap at least a line or two depending on if this is a huge message.",
        1234567890123,
        null,
        123456789,
        false
    )
    ArcOSCompanionTheme(true) {
        MessageCard(messageInfo)
    }
}