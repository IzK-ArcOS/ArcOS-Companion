package com.blockyheadman.arcoscompanion.ui

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.VibrationEffect
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.blockyheadman.arcoscompanion.R
import com.blockyheadman.arcoscompanion.apiDao
import com.blockyheadman.arcoscompanion.apis
import com.blockyheadman.arcoscompanion.connectivityManager
import com.blockyheadman.arcoscompanion.data.ApiSaveDao
import com.blockyheadman.arcoscompanion.data.ApiSaveData
import com.blockyheadman.arcoscompanion.data.network.ApiCall
import com.blockyheadman.arcoscompanion.data.network.AuthResponse
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

var connectionAvailable: Boolean = false

lateinit var privateAPIDialog: MutableState<Boolean>
lateinit var showAddAPI: MutableState<Boolean>
lateinit var showEditAPI: MutableState<Boolean>
lateinit var editApi: ApiSaveData

@Composable
fun ServersPage(externalPadding: PaddingValues) {

    privateAPIDialog = rememberSaveable { mutableStateOf(false) }
    showAddAPI = rememberSaveable { mutableStateOf(false) }
    showEditAPI = rememberSaveable { mutableStateOf(false) }

    var apiTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier
            .padding(externalPadding),
        topBar = {
            TabRow(
                selectedTabIndex = apiTabIndex,
                divider = {
                    HorizontalDivider(
                        thickness = 1.dp
                    )
                },
                tabs = {
                    Tab(
                        selected = apiTabIndex == 0,
                        onClick = {
                            apiTabIndex = 0
                            privateAPIDialog.value = false
                        },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Public APIs")
                    }
                    Tab(
                        selected = apiTabIndex == 1,
                        onClick = {
                            apiTabIndex = 1
                            privateAPIDialog.value = true
                        },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Private APIs")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                //showNewAPIDialog.value = true
                showAddAPI.value = true
                //privateAPIDialog.value = serverTypeTabIndex == 1
            }) {
                Text(
                    when (apiTabIndex) {
                        0 -> "New API"
                        else -> "New Private API"
                    }
                )
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->

        //val mainContext = LocalContext.current

        //apis by rememberSaveable { mutableStateOf(emptyList<ApiSaveData>()) } //: List<ApiSaveData>

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            LaunchedEffect(Unit) {
                coroutineScope {
                    launch(Dispatchers.Main) {
                        apis = apiDao.getAll()
                    }
                }
            }

            LazyColumn {
                when (apiTabIndex) {
                    0 -> {
                        items(apis.size) {
                            if (apis[it].authCode.isBlank()) ApiCard(apis[it]) // private = false
                        }
                        if (apis.isEmpty()) item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("There's not much to see here..")
                            }
                        }
                    }

                    1 -> {
                        items(apis.size) {
                            if (apis[it].authCode.isNotBlank()) ApiCard(apis[it]) // private = true
                        }
                        if (apis.isEmpty()) item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("There's not much to see here..")
                            }
                        }
                    }

                    else -> item {
                        Text(
                            text = "This page shouldn't exist.\nPlease report this issue.",
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
        if (showAddAPI.value) {
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
        if (showEditAPI.value) {
            EditApiDialog(apiDao, editApi)
        }
    }
}

//@Preview
@Composable
fun ApiCard(data: ApiSaveData) { //data: ApiSaveData add `private: Boolean` for private apis
    var settingsExpanded by rememberSaveable { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(R.drawable.arcos_logo),
                    contentDescription = "ArcOS Logo",
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    data.name,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

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

                        DropdownMenuItem(text = { Text("Edit") },
                            onClick = {
                                runBlocking {
                                    coroutineScope {
                                        launch(Dispatchers.IO) {
                                            // Edit info
                                            settingsExpanded = false
                                            editApi = data
                                            showEditAPI.value = true
                                        }
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        )
                        DropdownMenuItem(text = { Text("Delete") },
                            onClick = {
                                runBlocking {
                                    coroutineScope {
                                        launch(Dispatchers.IO) {
                                            apiDao.delete(data)
                                            settingsExpanded = false
                                            apis = apiDao.getAll()
                                        }
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    "User: " + data.username,
                    fontWeight = FontWeight.W500,
                    fontSize = 24.sp
                )
            }
            /*Text(data.password)
            if (private) data.authCode?.let { Text(it) }*/
        }

    }
}

@Composable
fun NewApiDialog(apiDao: ApiSaveDao) {
    Dialog(onDismissRequest = { showAddAPI.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (privateAPIDialog.value) 410.dp else 340.dp)
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

            val mainContext = LocalContext.current

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                val addApiContext = LocalContext.current

                Text(
                    "Add API",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(
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

                Spacer(Modifier.height(4.dp))

                var buttonClicked by rememberSaveable { mutableStateOf(false) }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        showAddAPI.value = false
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
                    val authRequest = ApiCall()
                    var authData: AuthResponse?

                    LaunchedEffect(authRequest) {
                        coroutineScope {
                            authData = authRequest.getToken(
                                apiInput,
                                usernameInput,
                                passwordInput,
                                authCodeInput
                            )
                        }
                        if (authRequest.errorMessage.isEmpty()) {
                            if (authData?.data?.token.isNullOrBlank()) {
                                coroutineScope {
                                    apiDao.insert(
                                        ApiSaveData(
                                            apiInput,
                                            usernameInput,
                                            passwordInput,
                                            authCodeInput,
                                            //authData?.data?.token
                                        )
                                    )
                                }
                                // token success
                                Toast.makeText(
                                    addApiContext,
                                    "API Added Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d("AuthRequest", "API Authenticated Successfully!")
                                apis = apiDao.getAll()
                                showAddAPI.value = false
                            }

                        } else {
                            // error handling
                            Log.d("AddAPIAuth", authRequest.errorMessage)

                            if (authRequest.errorMessage.startsWith("Unable to resolve host")) {
                                if (connectionAvailable) {
                                    Toast.makeText(
                                        mainContext,
                                        "Incorrect API name.\n" +
                                                "Try using 'community.arcapi.nl'.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    apiError = true
                                } else {
                                    Toast.makeText(
                                        mainContext,
                                        "You must be online to continue.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (authRequest.errorMessage == "HTTP 404 ") {
                                Toast.makeText(
                                    mainContext,
                                    "Incorrect Username.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                usernameError = true
                            } else if (authRequest.errorMessage == "HTTP 403 ") {
                                Toast.makeText(
                                    mainContext,
                                    "Incorrect password.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                passwordError = true
                            } else if (authRequest.errorMessage == "HTTP 401 ") {
                                Toast.makeText(
                                    mainContext,
                                    "Incorrect Auth Code.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                authCodeError = true
                            }
                        }
                        buttonClicked = false
                    }
                }
            }
        }

    }

// If in case I wish to have a Modal
    /*val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showAddApiSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showAddApiSheet.value = false },
            sheetState = sheetState
        ) {
            // Move everything inside the Card here.
        }
    }*/
}

@Composable
fun EditApiDialog(apiDao: ApiSaveDao, api: ApiSaveData) {
    val apiName = api.name
    val username = api.username

    Dialog(onDismissRequest = { showEditAPI.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                //.height(if (privateAPIDialog.value) 300.dp else 210.dp)
                .height(if (privateAPIDialog.value) 410.dp else 340.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            val focusManager = LocalFocusManager.current

            var authCodeInput by rememberSaveable { mutableStateOf("") }
            var passwordInput by rememberSaveable { mutableStateOf("") }

            var authCodeError by rememberSaveable { mutableStateOf(false) }
            var passwordError by rememberSaveable { mutableStateOf(false) }

            val mainContext = LocalContext.current

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                val editApiContext = LocalContext.current

                Text(
                    "Edit API",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(
                    modifier = Modifier.padding(8.dp, 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    label = { Text("API Name:") },
                    value = editApi.name,
                    onValueChange = {},
                    enabled = false,
                    singleLine = true
                )

                OutlinedTextField(
                    label = { Text("Username:") },
                    onValueChange = {},
                    value = editApi.username,
                    enabled = false,
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

                Spacer(Modifier.height(4.dp))

                var buttonClicked by rememberSaveable { mutableStateOf(false) }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        showEditAPI.value = false
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
                            Log.d("EditAPIOKButton", "OK Clicked")
                            focusManager.clearFocus()
                            buttonClicked = true

                            //showNewAPIDialog = false
                            vibrator.vibrate(
                                VibrationEffect.createPredefined(
                                    VibrationEffect.EFFECT_DOUBLE_CLICK
                                )
                            )
                        },
                        enabled = passwordInput.isNotEmpty()
                    ) {
                        Text("OK")
                    }
                }

                if (buttonClicked) {
                    val authRequest = ApiCall()
                    var authData: AuthResponse?

                    LaunchedEffect(authRequest) {
                        coroutineScope {
                            authData = authRequest.getToken(
                                apiName,
                                username,
                                passwordInput,
                                authCodeInput
                            )
                        }
                        if (authRequest.errorMessage.isEmpty()) {
                            authData?.data?.token?.let { Log.d("AuthRequest", it) }
                            if (authData?.data?.token.isNullOrBlank()) {
                                coroutineScope {
                                    apiDao.update(
                                        ApiSaveData(
                                            api.name,
                                            api.username,
                                            passwordInput,
                                            authCodeInput,
                                            //authData?.data?.token
                                        )
                                    )
                                }
                                // token success
                                Toast.makeText(
                                    editApiContext,
                                    "API Added Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d("AuthRequest", "API Authenticated Successfully!")
                                apis = apiDao.getAll()
                                showEditAPI.value = false
                            }

                        } else {
                            // error handling
                            Log.d("EditAPIAuth", authRequest.errorMessage)

                            if (authRequest.errorMessage.startsWith("Unable to resolve host")) {
                                if (connectionAvailable) {
                                    Toast.makeText(
                                        mainContext,
                                        "Incorrect API name.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        mainContext,
                                        "You must be online to continue.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (authRequest.errorMessage == "HTTP 404 ") {
                                Toast.makeText(
                                    mainContext,
                                    "Incorrect Username.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (authRequest.errorMessage == "HTTP 403 ") {
                                Toast.makeText(
                                    mainContext,
                                    "Incorrect password.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                passwordError = true
                            } else if (authRequest.errorMessage == "HTTP 401 ") {
                                Toast.makeText(
                                    mainContext,
                                    "Incorrect Auth Code.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                authCodeError = true
                            }
                        }
                        buttonClicked = false
                    }
                }
            }
        }

    }

// If in case I wish to have a Modal
    /*val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showAddApiSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showAddApiSheet.value = false },
            sheetState = sheetState
        ) {
            // Move everything inside the Card here.
        }
    }*/
}
