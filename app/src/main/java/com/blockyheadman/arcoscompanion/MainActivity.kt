package com.blockyheadman.arcoscompanion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.blockyheadman.arcoscompanion.data.ApiSaveDao
import com.blockyheadman.arcoscompanion.data.ApiSaveData
import com.blockyheadman.arcoscompanion.data.ApiSaveDatabase
import com.blockyheadman.arcoscompanion.data.UserPreferences
import com.blockyheadman.arcoscompanion.data.classes.AuthResponse
import com.blockyheadman.arcoscompanion.data.navBarItems
import com.blockyheadman.arcoscompanion.data.network.ApiCall
import com.blockyheadman.arcoscompanion.data.network.NotificationWorker
import com.blockyheadman.arcoscompanion.ui.HomePage
import com.blockyheadman.arcoscompanion.ui.MessagesPage
import com.blockyheadman.arcoscompanion.ui.ServersPage
import com.blockyheadman.arcoscompanion.ui.SettingsPage
import com.blockyheadman.arcoscompanion.ui.theme.ArcOSCompanionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Duration

lateinit var vibrator: Vibrator
lateinit var connectivityManager: ConnectivityManager
lateinit var notificationManager: NotificationManager

lateinit var apiDao: ApiSaveDao
var apis by mutableStateOf(emptyList<ApiSaveData>())
var hapticsEnabled: State<Boolean> = mutableStateOf(false)

object NotificationIDs {
    const val NOTIFICATION_ID = 112
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val messageChannel = NotificationChannel(
            getString(R.string.message_notifs_ID),
            getString(R.string.message_notifs_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        messageChannel.enableLights(true)
        messageChannel.lightColor = Color.Yellow.toArgb()
        messageChannel.enableVibration(true)
        messageChannel.description = getString(R.string.message_notifs_desc)

        notificationManager.createNotificationChannel(messageChannel)

        // Create the NotificationChannel.
        /*val name = getString(R.string.message_notifs_name)
        val descriptionText = getString(R.string.message_notifs_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(getString(R.string.message_notifs_ID), name, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)*/

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        installSplashScreen()
        setContent {
            CompanionApp()
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()


    }*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionApp() {

    val context = LocalContext.current
    val store = UserPreferences(context)

    val systemDarkTheme = isSystemInDarkTheme()
    val dynamicColor = store.getMaterialYouMode.collectAsState(initial = false)
    val darkThemeEnabled = store.getThemeMode.collectAsState(initial = 0)
    val darkTheme = when (darkThemeEnabled.value) {
        0 -> systemDarkTheme
        1 -> false
        2 -> true
        else -> systemDarkTheme
    }

    hapticsEnabled = store.getHapticsMode.collectAsState(initial = false)

    var showConnectionLost by rememberSaveable { mutableStateOf(true) }
    var connectionAvailable by rememberSaveable { mutableStateOf(true) }

    val db = ApiSaveDatabase.getInstance(context)
    apiDao = db.apiSaveDao()

    val notificationWorkRequest: WorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(Duration.ofSeconds(1))
            .build()

    WorkManager
        .getInstance(context)
        .enqueue(notificationWorkRequest)

    LaunchedEffect(connectionAvailable) {
        coroutineScope {
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("ConnectionManager", "The default network is now: $network")
                    showConnectionLost = true
                    connectionAvailable = true
                }

                override fun onLost(network: Network) {
                    Log.d(
                        "ConnectionManager",
                        "The application no longer has a default network. The last default network was $network"
                    )
                    showConnectionLost = true
                    connectionAvailable = false
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    /*Log.d(
                        "ConnectionManager",
                        "The default network changed capabilities: $networkCapabilities"
                    )*/
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    /*Log.d(
                        "ConnectionManager",
                        "The default network changed link properties: $linkProperties"
                    )*/
                }
            })
        }
    }

    ArcOSCompanionTheme(darkTheme, dynamicColor.value) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedNavBarItem by rememberSaveable { mutableIntStateOf(0) }
            var previousSelectedNavBarItem by rememberSaveable { mutableIntStateOf(0) }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (selectedNavBarItem) {
                                    0 -> Text("ArcOS Companion")
                                    1 -> Text("APIs")
                                    2 -> Text("Messages")
                                    else -> Text("Settings")
                                }

                            }
                        },
                        navigationIcon = {
                            Image(
                                painter = painterResource(R.drawable.companion_logo),
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
                                selected = selectedNavBarItem == index,
                                onClick = {
                                    previousSelectedNavBarItem = selectedNavBarItem
                                    selectedNavBarItem = index
                                    if(hapticsEnabled.value) vibrator.vibrate(
                                        VibrationEffect.createPredefined(
                                            VibrationEffect.EFFECT_DOUBLE_CLICK
                                        )
                                    )
                                },
                                icon = {
                                    /*if (index == 2 && ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == 0
                                    ) {
                                        BadgedBox(badge = {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                            ) {
                                                Text("99+")
                                            }
                                        }) {
                                            if (selectedNavBarItem == index) {
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
                                        }
                                    } else {
                                        if (selectedNavBarItem == index) {
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
                                    }*/
                                    if (selectedNavBarItem == index) {
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
                },
                snackbarHost = {
                    AnimatedVisibility(
                        visible = !connectionAvailable && showConnectionLost,
                        modifier = Modifier.padding(6.dp)
                    ) {
                        Snackbar(
                            modifier = Modifier.padding(4.dp),
                            dismissAction = {
                                Button({showConnectionLost = false}){Text("OK")}
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Network Connection has been lost.")
                        }
                    }
                }
            ) { innerPadding ->

                LaunchedEffect(Unit) {
                    coroutineScope {
                        launch(Dispatchers.Main) {
                            apis = apiDao.getAll()
                        }
                    }
                }

                AnimatedContent(
                    targetState = selectedNavBarItem,
                    transitionSpec = {
                        if (targetState > previousSelectedNavBarItem) {
                            Log.d("MainActivity", "targetState ($targetState) is a larger # than the new # ($selectedNavBarItem)")
                            (slideInHorizontally { fullWidth -> fullWidth } + fadeIn()).togetherWith(
                                slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
                        } else {
                            Log.d("MainActivity", "targetState ($targetState) is a smaller # than the new # ($selectedNavBarItem)")
                            (slideInHorizontally { fullWidth -> -fullWidth } + fadeIn()).togetherWith(
                                slideOutHorizontally { fullWidth -> fullWidth } + fadeOut())
                        }.using(
                            SizeTransform(clip = false)
                        )
                    },
                    label = "Animated main items"
                ) { selectedItem ->
                    when (selectedItem) {
                        0 -> { HomePage(innerPadding) }
                        1 -> { ServersPage(innerPadding) }
                        2 -> { MessagesPage(innerPadding) }
                        3 -> { SettingsPage(innerPadding) }
                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "This page shouldn't exist.\nPlease report this issue.",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun getAuthToken(
    apiName: String,
    username: String,
    password: String,
    authCode: String
): String? {
    val authRequest = ApiCall()
    val authData: AuthResponse?

    try {
        authData = coroutineScope {
            async {
                authRequest.getToken(
                    apiName,
                    username,
                    password,
                    authCode
                )
            }
        }.await()
    } catch (e: Exception) {
        Log.e("getToken", "Error getting token: $e")
        return null
    }

    Log.d("getToken", "data: $authData")

    if (authData == null) {
        Log.e("getToken", "Auth Data is null")
        return null
    }

    if (authRequest.errorMessage.isEmpty()) {
        Log.d("AuthData", authData.toString())
        Log.d("TokenData", authData.data.token)
    } else {
        Log.e("TokenDataError", authRequest.errorMessage)
        return null
    }

    return authData.data.token
}