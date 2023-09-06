package com.blockyheadman.arcoscompanion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.blockyheadman.arcoscompanion.data.UserPreferences
import com.blockyheadman.arcoscompanion.data.navBarItems
import com.blockyheadman.arcoscompanion.ui.HomePage
import com.blockyheadman.arcoscompanion.ui.MessagesPage
import com.blockyheadman.arcoscompanion.ui.ServersPage
import com.blockyheadman.arcoscompanion.ui.SettingsPage
import com.blockyheadman.arcoscompanion.ui.theme.ArcOSCompanionTheme

lateinit var vibrator: Vibrator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            CompanionApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionApp() {
    val context = LocalContext.current

    val store = UserPreferences(context)

    val systemDarkTheme = isSystemInDarkTheme()
    //val dynamicColor by remember { Settings.dynamicColor }
    //val darkThemeEnabled by remember { Settings.darkThemeEnabled }
    val dynamicColor = store.getMaterialYouMode.collectAsState(initial = false)
    val darkThemeEnabled = store.getThemeMode.collectAsState(initial = 0)
    val darkTheme = when (darkThemeEnabled.value) {
        0 -> systemDarkTheme
        1 -> false
        2 -> true
        else -> systemDarkTheme
    }

    var selectedNavBarItem by rememberSaveable { mutableStateOf(0) }


    ArcOSCompanionTheme (darkTheme, dynamicColor.value) {
        Surface (
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold (
                topBar = {
                    TopAppBar (
                        title = {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (selectedNavBarItem) {
                                    0 -> Text("ArcOS Companion")
                                    1 -> Text("Servers")
                                    2 -> Text("Messages")
                                    else -> Text("Settings")
                                }

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
                                selected = selectedNavBarItem == index,
                                onClick = {
                                    selectedNavBarItem = index
                                    vibrator.vibrate(
                                        VibrationEffect.createPredefined(
                                            VibrationEffect.EFFECT_DOUBLE_CLICK))
                                          },
                                icon = {
                                    if (index == 2 && ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == 0) {
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
                                    }
                                },
                                label = { Text(item.name) }
                            )
                        }
                    }
                }

            ) { innerPadding ->

                when (selectedNavBarItem) {
                    0 -> {
                        HomePage(innerPadding)
                    }

                    1 -> {
                        ServersPage(innerPadding)
                    }

                    2 -> {
                        MessagesPage(innerPadding)
                    }

                    else -> {
                        SettingsPage(innerPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("MainActivity","PERMISSION GRANTED")
        } else {
            // Permission Denied: Do something
            Log.d("MainActivity","PERMISSION DENIED")
        }
    }
    val context = LocalContext.current

    Box (
        Modifier.fillMaxSize(),
        Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to the ArcOS Companion App!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "This app has some permissions we need you to grant!",
                    textAlign = TextAlign.Center
                )
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) -> {
                                Log.d("MainActivity", "Code requires permission")
                            }

                            else -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        }

                    }) {
                        Text("Accept Permissions")
                    }
                    Spacer(Modifier.width(4.dp))
                    Button(onClick = { /*TODO*/ }) {
                        Text("No thanks.")
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, wallpaper = 1)
@Composable
fun DarkGreetingPreview() {
    CompanionApp()
}

@Preview(showBackground = true)
@Composable
fun LightGreetingPreview() {
    CompanionApp()
}