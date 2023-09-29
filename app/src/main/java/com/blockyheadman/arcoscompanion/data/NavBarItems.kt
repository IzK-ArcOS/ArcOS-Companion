package com.blockyheadman.arcoscompanion.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationBarItem(
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
    val name: String
)

val navBarItems = listOf(
    NavigationBarItem(Icons.Outlined.Home, Icons.Filled.Home, "Home"),
    NavigationBarItem(Icons.AutoMirrored.Outlined.List, Icons.AutoMirrored.Filled.List, "Servers"),
    NavigationBarItem(Icons.Outlined.Email, Icons.Filled.Email, "Messages"),
    NavigationBarItem(Icons.Outlined.Settings, Icons.Filled.Settings, "Settings")
)