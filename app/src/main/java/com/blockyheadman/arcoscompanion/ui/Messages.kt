package com.blockyheadman.arcoscompanion.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.blockyheadman.arcoscompanion.NotificationIDs
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.apis
import com.blockyheadman.arcoscompanion.data.classes.FullMessage
import com.blockyheadman.arcoscompanion.data.classes.MessageData
import com.blockyheadman.arcoscompanion.data.classes.MessageList
import com.blockyheadman.arcoscompanion.data.network.ApiCall
import com.blockyheadman.arcoscompanion.getAuthToken
import com.blockyheadman.arcoscompanion.hapticsEnabled
import com.blockyheadman.arcoscompanion.ui.theme.ArcOSCompanionTheme
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date


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

    var messagesData: MessageList? by rememberSaveable { mutableStateOf(null) }
    var messageDataError by rememberSaveable { mutableStateOf(0) }

    var activeCardId by rememberSaveable { mutableStateOf<Int?>(null) }

    permissionGranted = when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) -> true
        else -> false
    }

    val apisSorted = apis.sortedBy { it.username }

    Scaffold (
        modifier = Modifier
            .padding(externalPadding)
            .fillMaxSize(),
        topBar = {
            if (activeCardId == null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (apis.size > 1) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = apiTabIndex,
                            divider = {
                                HorizontalDivider(Modifier.fillMaxWidth())
                            },
                            tabs = {
                                apisSorted.forEach { api ->
                                    Tab(
                                        selected = apiTabIndex == apisSorted.indexOf(api),
                                        onClick = {
                                            if(hapticsEnabled.value) vibrator.vibrate(
                                                VibrationEffect.createPredefined(
                                                    VibrationEffect.EFFECT_CLICK
                                                )
                                            )

                                            apiTabIndex = apisSorted.indexOf(api)
                                            Log.d(
                                                "ApiTab",
                                                "Api list size is ${apisSorted.size}"
                                            )
                                            Log.d(
                                                "ApiTab",
                                                "Tab $apiTabIndex was clicked"
                                            )

                                            val scope = CoroutineScope(Job())
                                            scope.launch {
                                                // TODO Make this a function
                                                messagesData = null
                                                messageDataError = 0

                                                val token: String? = async {
                                                    getAuthToken(
                                                        apisSorted[apiTabIndex].name,
                                                        apisSorted[apiTabIndex].username,
                                                        apisSorted[apiTabIndex].password,
                                                        apisSorted[apiTabIndex].authCode
                                                    )
                                                }.await()

                                                if (token.isNullOrBlank()) {
                                                    Log.e(
                                                        "GET MESSAGES",
                                                        "Empty token. Stopping.."
                                                    )
                                                    messageDataError = 1
                                                    return@launch
                                                }

                                                Log.d(
                                                    "GET MESSAGES",
                                                    "Api name: ${apisSorted[apiTabIndex].name}"
                                                )
                                                Log.d("GET MESSAGES", "Token: $token")
                                                messagesData = getMessages(
                                                    apisSorted[apiTabIndex].name,
                                                    apisSorted[apiTabIndex].authCode,
                                                    token
                                                )
                                                ApiCall().deAuthToken(
                                                    apisSorted[apiTabIndex].name,
                                                    apisSorted[apiTabIndex].authCode,
                                                    token
                                                )
                                            }
                                        },
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
                            }
                        )
                    }
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
                                    if (hapticsEnabled.value) vibrator.vibrate(
                                        VibrationEffect.createPredefined(
                                            VibrationEffect.EFFECT_DOUBLE_CLICK
                                        )
                                    )
                                    when (PackageManager.PERMISSION_GRANTED) {
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) -> {
                                            Log.d(
                                                "MessagesPage",
                                                "Code requires permission"
                                            )
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
                        if (apis.isNotEmpty()) {
                            Spacer(Modifier.width(4.dp))
                            TooltipBox(
                                positionProvider = TooltipDefaults
                                    .rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip {
                                        Text("Reload Messages")
                                    }
                                },
                                state = rememberTooltipState()
                            ) {
                                ElevatedButton(
                                    onClick = {
                                        if (hapticsEnabled.value) vibrator.vibrate(
                                            VibrationEffect.createPredefined(
                                                VibrationEffect.EFFECT_DOUBLE_CLICK
                                            )
                                        )
                                        Log.d(
                                            "GET MESSAGES",
                                            "Api name: ${apis[apiTabIndex].name}"
                                        )
                                        Log.d(
                                            "GET MESSAGES",
                                            "Api auth code: ${apis[apiTabIndex].authCode}"
                                        )

                                        val scope = CoroutineScope(Job())
                                        scope.launch {
                                            // TODO Make this a function
                                            messagesData = null
                                            messageDataError = 0

                                            val token: String? = async {
                                                getAuthToken(
                                                    apisSorted[apiTabIndex].name,
                                                    apisSorted[apiTabIndex].username,
                                                    apisSorted[apiTabIndex].password,
                                                    apisSorted[apiTabIndex].authCode
                                                )
                                            }.await()

                                            if (token.isNullOrBlank()) {
                                                Log.e(
                                                    "GET MESSAGES", "Empty token. Stopping.."
                                                )
                                                messageDataError = 1
                                                return@launch
                                            }

                                            Log.d(
                                                "GET MESSAGES",
                                                "Api name: ${apis[apiTabIndex].name}"
                                            )
                                            Log.d("GET MESSAGES", "Token: $token")
                                            messagesData = getMessages(
                                                apisSorted[apiTabIndex].name,
                                                apisSorted[apiTabIndex].authCode,
                                                token
                                            )
                                            ApiCall().deAuthToken(
                                                apisSorted[apiTabIndex].name,
                                                apisSorted[apiTabIndex].authCode,
                                                token
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

        }
    ) { innerPadding ->
        //val mainContext = LocalContext.current

        LaunchedEffect(Unit) {
            coroutineScope {
                launch(Dispatchers.IO) {
                    // TODO Make this a function
                    if (apis.isNotEmpty()) {
                        messagesData = null
                        messageDataError = 0

                        val token: String? = async {
                            getAuthToken(
                                apisSorted[apiTabIndex].name,
                                apisSorted[apiTabIndex].username,
                                apisSorted[apiTabIndex].password,
                                apisSorted[apiTabIndex].authCode
                            )
                        }.await()

                        if (token.isNullOrBlank()) {
                            Log.e("GET MESSAGES", "Empty token. Stopping..")
                            messageDataError = 1
                            return@launch
                        }

                        Log.d("GET MESSAGES", "Api name: ${apis[apiTabIndex].name}")
                        Log.d("GET MESSAGES", "Token: $token")
                        messagesData = getMessages(
                            apisSorted[apiTabIndex].name,
                            apisSorted[apiTabIndex].authCode,
                            token
                        )
                        ApiCall().deAuthToken(
                            apisSorted[apiTabIndex].name,
                            apisSorted[apiTabIndex].authCode,
                            token
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
        LazyColumn(
            Modifier.padding(innerPadding)//.padding(top = 52.dp)
        ) {
            if (messagesData != null) {
                if (messagesData!!.data.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Lonely mailbox you got there..",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Log.d("LazyMessageCards","messagesData:${messagesData!!.data.size}")
                    messagesData?.data!!.let { list ->
                        Log.d("LazyMessageCards","list:${list.size}")
                        items(list.size) { messageIndex ->
                            AnimatedVisibility(
                                visibleState = MutableTransitionState(
                                    initialState = false
                                ).apply { targetState = true },
                                modifier = Modifier,
                                enter = slideInVertically(
                                    initialOffsetY = { 120 }
                                ) + fadeIn(
                                    initialAlpha = 0f
                                ),
                                exit = slideOutVertically() + fadeOut(),
                            ) {
                                MessageCard(
                                    messagesData!!.data[messageIndex],
                                    navigateToFullMessage = {
                                        activeCardId = it //messageData!!.data[messageIndex].id
                                    }
                                )
                            }

                        }
                    }
                }
                Log.d("MessageData", messagesData.toString())
            } else { // TODO Fix crash due to this thing for some odd reason
                item {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = /*if (apisSorted.isNotEmpty()) "Loading your inbox.."
                            else "Add an API to view messages."*/"...",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visibleState = MutableTransitionState(
                initialState = false
            ).apply { targetState = activeCardId != null },
            modifier = Modifier,
            enter = fadeIn(
                initialAlpha = 0f
            ) + slideInVertically(
                initialOffsetY = { 600 }
            ),
            exit = fadeOut() + slideOutVertically(),
        ) {
            FullMessageCard(
                apisSorted[apiTabIndex].name,
                apisSorted[apiTabIndex].username,
                apisSorted[apiTabIndex].authCode,
                activeCardId!!,
                onDismiss = { activeCardId = null },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MessageCard(
    messageInfo: MessageData,
    navigateToFullMessage: (Int) -> Unit = { }
) {
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
                Image(
                    painter = painterResource(R.drawable.arcos_logo),
                    contentDescription = "user profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterVertically)
                )
                Column (
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
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
                Text(
                    //text = "12/31/2023\n12:39 PM",
                    text = getDate(messageInfo.timestamp)+"\n"+ getDateTime(messageInfo.timestamp),
                    modifier = Modifier
                        .weight(0.8f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        if(hapticsEnabled.value) vibrator.vibrate(
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                        )
                        settingsExpanded = true
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options"
                    )

                    DropdownMenu(
                        expanded = settingsExpanded,
                        onDismissRequest = { settingsExpanded = false }
                    ) {

                        // TODO add delete, new message, and reply functionality
                        DropdownMenuItem(text = { Text("View Full") },
                            onClick = {
                                navigateToFullMessage(messageInfo.id)
                                if(hapticsEnabled.value) vibrator.vibrate(
                                    VibrationEffect.createPredefined(
                                        VibrationEffect.EFFECT_CLICK
                                    )
                                )

                                settingsExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = "View Full Message"
                                )
                            }
                        )

                        DropdownMenuItem(text = { Text("Reply") },
                            onClick = {
                                if(hapticsEnabled.value) vibrator.vibrate(
                                    VibrationEffect.createPredefined(
                                        VibrationEffect.EFFECT_CLICK
                                    )
                                )
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
            if (bodyContents.size == 2) {
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
            } else {
                Text(
                    bodyContents[0],
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// TODO make full message card get message data
@Composable
fun FullMessageCard(
    apiName: String,
    apiUsername: String,
    apiAuthCode: String,
    messageId: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.onBackground
        )
    ) {
        val context = LocalContext.current
        Column(
            Modifier.padding(4.dp)
        ) {
            Row {
                IconButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to messages screen"
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(apiUsername)
                    Text(apiName)
                }
                IconButton(onClick = {
                    // TODO Open Context menu
                    Toast.makeText(
                        context,
                        "Feature not yet available",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Message context menu"
                    )
                }
            }
            /*Text(
                text = getDate(timestamp)+" "+getDateTime(timestamp),
                modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )*/
            Text(
                text = "This is a pretty long title for testing things",
                fontSize = 28.sp,
                fontWeight = FontWeight.W500,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center
            )

            Text(text = "From: Blocky")
            Text(text = "To: Izaak Kuipers")
            Spacer(Modifier.height(4.dp))
            Text(
                text = "This is a test body for a message card that's pretty long and " +
                        "should wrap at least a line or two depending on if this " +
                        "is a huge message."
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

suspend fun getMessages(apiName: String, authCode: String, authToken: String): MessageList? {
    val messageRequest = ApiCall()
    var messageData: MessageList? = null

    coroutineScope {
        runBlocking {
            launch(Dispatchers.IO) {
                messageData = messageRequest.getMessages(
                    apiName,
                    authCode,
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

suspend fun getMessage(
    apiName: String,
    authCode: String,
    messageId: Int,
    authToken: String
): FullMessage? {
    val messageRequest = ApiCall()
    var messageData: FullMessage? = null

    coroutineScope {
        runBlocking {
            launch(Dispatchers.IO) {
                messageData = messageRequest.getMessage(
                    apiName,
                    messageId,
                    authCode,
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

fun getDate(timestamp: Long): String {
    val ts = Date(timestamp)
    return SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD).format(ts)//("yyyy/MM/dd hh:mm").format(ts)
}

fun getDateTime(timestamp: Long): String {
    val ts = Date(timestamp)
    return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(ts)
}

@Preview
@Composable
fun MessageCardPreview() {
    val messageInfo = MessageData(
        "Blocky",
        "Izaak Kuipers",
        "### This is a pretty long title for testing things\n" +
                "This is a test body for a message card that's pretty long and should wrap at " +
                "least a line or two depending on if this is a huge message.",
        1234567890123,
        null,
        123456789,
        false
    )
    ArcOSCompanionTheme(true) {
        MessageCard(messageInfo)
    }
}

@Preview
@Composable
fun FullMessageCardPreview() {
    ArcOSCompanionTheme(true) {
        FullMessageCard(
            "community.arcapi.nl",
            "Blocky",
            "",
            1234567890,
            {},
            Modifier
        )
    }
}