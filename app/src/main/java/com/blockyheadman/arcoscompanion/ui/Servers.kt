package com.blockyheadman.arcoscompanion.ui

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.VibrationEffect
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.blockyheadman.arcoscompanion.connectivityManager
import com.blockyheadman.arcoscompanion.data.ApiSaveDao
import com.blockyheadman.arcoscompanion.data.ApiSaveData
import com.blockyheadman.arcoscompanion.data.ApiSaveDatabase
import com.blockyheadman.arcoscompanion.data.network.AuthCall
import com.blockyheadman.arcoscompanion.data.network.AuthResponse
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

var connectionAvailable: Boolean = false

lateinit var showNewAPIDialog: MutableState<Boolean>
lateinit var privateAPIDialog: MutableState<Boolean>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersPage(externalPadding: PaddingValues) {

    showNewAPIDialog = rememberSaveable { mutableStateOf(false) }
    privateAPIDialog = rememberSaveable { mutableStateOf(false) }

    var serverTypeTabIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold (
        modifier = Modifier
            .padding(externalPadding),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                showNewAPIDialog.value = true
                privateAPIDialog.value = serverTypeTabIndex == 1
            }) {
                Text(
                    when (serverTypeTabIndex) {
                        0 -> "New API"
                        else -> "New Private API"
                    }
                )
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->

        TabRow(
            selectedTabIndex = serverTypeTabIndex,
            divider = {
                Divider(
                    thickness = 1.dp
                )
                      },
            tabs = {
                Tab(
                    selected = serverTypeTabIndex==0,
                    onClick = { serverTypeTabIndex = 0 },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Public Servers")
                }
                Tab(
                    selected = serverTypeTabIndex==1,
                    onClick = { serverTypeTabIndex = 1 },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Private Servers")
                }
            }
        )

        val mainContext = LocalContext.current

        val db = ApiSaveDatabase.getInstance(mainContext)

        val apiDao = db.apiSaveDao()
        var apis: List<ApiSaveData> by rememberSaveable { mutableStateOf(emptyList()) }

        //val apiSaveCall = ApiSaveIO()
        //var apiSaveList: List<ApiSave>?

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            //Text("There's not much to see here..")

            LaunchedEffect(Unit) {
                coroutineScope {
                    launch {
                        apis = apiDao.getAll()
                    }
                }
            }

            apis.forEach { item ->
                item.authCode?.let { Log.d("AuthCodeChecker", "Auth code: $it") }
                if (serverTypeTabIndex == 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        var settingsExpanded = false

                        Text(item.name)
                        Text(item.username)
                        Text(item.password)
                        IconButton(onClick = { settingsExpanded = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        // TODO get dropdown to show and work
                        DropdownMenu(expanded = settingsExpanded, onDismissRequest = { settingsExpanded = false }) {
                            DropdownMenuItem(text = { Text("Delete") }, onClick = { /*TODO delete item*/ })
                        }
                    }
                } else if (serverTypeTabIndex ==1 && !item.authCode.isNullOrBlank()) {
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(item.name)
                        Text(item.username)
                        Text(item.password)
                        Text(item.authCode)
                    }
                } else{
                    Text("There's not much to see here..")
                }
            }
        }
        if (showNewAPIDialog.value) {
            //var connectionAvailable by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(connectionAvailable) {
                coroutineScope {
                    connectivityManager.registerDefaultNetworkCallback(object :
                        ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            Log.e("ConnectionManager", "The default network is now: $network")
                            connectionAvailable = true
                        }

                        override fun onLost(network: Network) {
                            Log.e(
                                "ConnectionManager",
                                "The application no longer has a default network. The last default network was $network"
                            )
                            connectionAvailable = false
                        }

                        override fun onCapabilitiesChanged(
                            network: Network,
                            networkCapabilities: NetworkCapabilities
                        ) {
                            Log.e(
                                "ConnectionManager",
                                "The default network changed capabilities: $networkCapabilities"
                            )
                        }

                        override fun onLinkPropertiesChanged(
                            network: Network,
                            linkProperties: LinkProperties
                        ) {
                            Log.e(
                                "ConnectionManager",
                                "The default network changed link properties: $linkProperties"
                            )
                        }
                    })
                }
            }

            NewApiDialog(apiDao)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewApiDialog(apiDao: ApiSaveDao) {
    var showApiError by rememberSaveable { mutableStateOf(false) }
    var showAuthCodeError by rememberSaveable { mutableStateOf(false) }
    var showUsernameError by rememberSaveable { mutableStateOf(false) }
    var showPasswordError by rememberSaveable { mutableStateOf(false) }
    var showConnectionError by rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = { showNewAPIDialog.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (privateAPIDialog.value) 400.dp else 330.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            val focusManager = LocalFocusManager.current

            var apiInput by rememberSaveable { mutableStateOf("") }
            var authCodeInput by rememberSaveable { mutableStateOf("") }
            var usernameInput by rememberSaveable { mutableStateOf("") }
            var passwordInput by rememberSaveable { mutableStateOf("") }

            var apiError by rememberSaveable { mutableStateOf(false) }
            var authCodeError by rememberSaveable { mutableStateOf(false) }
            var usernameError by rememberSaveable { mutableStateOf(false) }
            var passwordError by rememberSaveable { mutableStateOf(false) }

            Column (
                Modifier.fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                //Text("This feature is not yet available.")
                //Spacer(Modifier.size(8.dp))

                val addApiContext = LocalContext.current

                Text(
                    "Add API",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Divider(
                    modifier = Modifier.padding(8.dp, 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    label = { Text("API Name:") },
                    value = apiInput,
                    onValueChange = {
                        apiInput = it
                        apiError = false
                    },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.None,
                        false,
                        KeyboardType.Uri,
                        ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    placeholder = { Text("ex: community.arcapi.nl") },
                    isError = apiError,
                    singleLine = true
                )

                if (privateAPIDialog.value) {
                    OutlinedTextField(
                        label = { Text("Auth Code:") },
                        value = authCodeInput,
                        onValueChange = {
                            authCodeInput = it
                            authCodeError = false
                        },
                        keyboardOptions = KeyboardOptions(
                            KeyboardCapitalization.None,
                            false,
                            KeyboardType.Password,
                            ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = authCodeError,
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    label = { Text("Username:") },
                    value = usernameInput,
                    onValueChange = {
                        usernameInput = it
                        usernameError = false
                    },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Words,
                        false,
                        KeyboardType.Text,
                        ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = usernameError,
                    singleLine = true
                )

                OutlinedTextField(
                    label = { Text("Password:") },
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        passwordError = false
                    },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.None,
                        false,
                        KeyboardType.Password,
                        ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    isError = passwordError,
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                var buttonClicked by rememberSaveable { mutableStateOf(false) }

                Row (verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        showNewAPIDialog.value = false
                        vibrator.vibrate(
                            VibrationEffect.createPredefined(
                                VibrationEffect.EFFECT_DOUBLE_CLICK
                            )
                        )
                    }
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            Log.d("AddAPIOKButton", "OK Clicked")
                            focusManager.clearFocus()
                            buttonClicked = true

                            //showNewAPIDialog = false
                            vibrator.vibrate(
                                VibrationEffect.createPredefined(
                                    VibrationEffect.EFFECT_DOUBLE_CLICK
                                )
                            )
                        },
                        enabled = apiInput.isNotEmpty() && usernameInput.isNotEmpty() && passwordInput.isNotEmpty()
                    ) {
                        Text("OK")
                    }
                }

                if (buttonClicked) {
                    val authRequest = AuthCall()
                    var authData: AuthResponse?

                    LaunchedEffect(authRequest) {
                        coroutineScope {
                            authData = authRequest.getToken(apiInput, authCodeInput, usernameInput, passwordInput)
                        }
                        if (authRequest.errorMessage.isEmpty()) {
                            if (authData?.data?.token.isNullOrBlank()) {
                                // token success
                                showNewAPIDialog.value = false

                                /*runBlocking {
                                    addAPI(
                                        addApiContext,
                                        apiInput,
                                        null,
                                        usernameInput,
                                        passwordInput
                                    )
                                }*/

                                Toast.makeText(
                                    addApiContext,
                                    "API Added Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("AuthRequest", "API Authenticated Successfully!")
                                apiDao.insert(ApiSaveData(apiInput, usernameInput, passwordInput, authCodeInput))
                            }

                        } else {
                            // error
                            Log.d("AddAPIAuth", authRequest.errorMessage)
                            if (authRequest.errorMessage.startsWith("Unable to resolve host")) {
                                if (connectionAvailable) {showApiError = true} else { showConnectionError = true }
                            } else if (authRequest.errorMessage == "HTTP 404 ") {
                                showUsernameError = true
                            } else if (authRequest.errorMessage == "HTTP 403 ") {
                                showPasswordError = true
                            }
                        }
                        buttonClicked = false
                    }
                }
            }

            if (showApiError) {
                AlertDialog(
                    onDismissRequest = { showApiError = false },
                    confirmButton = {
                        Button(onClick = {
                            showApiError = false
                            apiError = true
                        }) {
                            Text("OK")
                        }
                    },
                    title = {
                        Text("Incorrect API name")
                    },
                    text = {
                        Text("Try using and API name like 'community.arcapi.nl' for the value.")
                    }
                )
            }
            if (showAuthCodeError) {
                AlertDialog(
                    onDismissRequest = { showAuthCodeError = false },
                    confirmButton = {
                        Button(onClick = {
                            showAuthCodeError = false
                            authCodeError = true
                        }) {
                            Text("OK")
                        }
                    },
                    text = {
                        Text("Incorrect auth code")
                    }
                )
            }
            if (showUsernameError) {
                AlertDialog(
                    onDismissRequest = { showUsernameError = false },
                    confirmButton = {
                        Button(onClick = {
                            showUsernameError = false
                            usernameError = true
                        }) {
                            Text("OK")
                        }
                    },
                    text = {
                        Text("Incorrect username")
                    }
                )
            }
            if (showPasswordError) {
                AlertDialog(
                    onDismissRequest = { showPasswordError = false },
                    confirmButton = {
                        Button(onClick = {
                            showPasswordError = false
                            passwordError = true
                        }) {
                            Text("OK")
                        }
                    },
                    text = {
                        Text("Incorrect password")
                    }
                )
            }
            if (showConnectionError) {
                AlertDialog(
                    onDismissRequest = { showConnectionError = false },
                    confirmButton = {
                        Button(onClick = {
                            showConnectionError = false
                        }) {
                            Text("OK")
                        }
                    },
                    text = {
                        Text("To continue, you must be online")
                    }
                )
            }
        }

    }
}

/*suspend fun addAPI(
    context: Context,
    name: String,
    authCode: String?,
    username: String,
    password: String
) {
    if (authCode != null) {
        context.apiDataStore.updateData { currentData ->
            currentData.toBuilder()
                .setName(name)
                .setAuthCode(authCode)
                .setUsername(username)
                .setPassword(password)
                .build()
        }
    } else {
        context.apiDataStore.updateData { currentData ->
            currentData.toBuilder()
                .setName(name)
                .setUsername(username)
                .setPassword(password)
                .build()
        }
    }
}*/