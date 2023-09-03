package com.blockyheadman.arcoscompanion

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.blockyheadman.arcoscompanion.data.navBarItems
import com.blockyheadman.arcoscompanion.ui.HomePage
import com.blockyheadman.arcoscompanion.ui.MessagesPage
import com.blockyheadman.arcoscompanion.ui.ServersPage
import com.blockyheadman.arcoscompanion.ui.Settings
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
    val systemDarkTheme = isSystemInDarkTheme()

    val dynamicColor by remember { Settings.dynamicColor }
    val darkThemeEnabled by remember { Settings.darkThemeEnabled }

    var selectedNavBarItem by rememberSaveable { mutableStateOf(0) }

    val darkTheme = when (darkThemeEnabled) {
        0 -> systemDarkTheme
        1 -> false
        2 -> true
        else -> systemDarkTheme
    }

    ArcOSCompanionTheme (darkTheme, dynamicColor) {
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
                                    if (index == 2) {
                                        BadgedBox(badge = { Badge (
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        ) {
                                            Text("99+")
                                        } }) {
                                            if (selectedNavBarItem == index) {
                                                Icon(imageVector = item.iconFilled, contentDescription = null)
                                            } else{
                                                Icon(imageVector = item.iconOutlined, contentDescription = null)
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

@Preview(showBackground = true)
@Composable
fun LightGreetingPreview() {
    CompanionApp()
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DarkGreetingPreview() {
    CompanionApp()
}
