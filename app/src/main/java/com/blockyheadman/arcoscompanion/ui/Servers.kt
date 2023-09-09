package com.blockyheadman.arcoscompanion.ui

import android.os.VibrationEffect
import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.blockyheadman.arcoscompanion.data.network.AuthCall
import com.blockyheadman.arcoscompanion.data.network.AuthResponse
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersPage(externalPadding: PaddingValues) {
    var showNewAPIDialog by rememberSaveable { mutableStateOf(false) }
    var showApiError by rememberSaveable { mutableStateOf(false) }
    var showUsernameError by rememberSaveable { mutableStateOf(false) }
    var showPasswordError by rememberSaveable { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier
            .padding(externalPadding),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                showNewAPIDialog = true
            }) {
                Text("New API")
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("There's not much to see here..")
        }
        if (showNewAPIDialog) {
            Dialog(onDismissRequest = { showNewAPIDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    val focusManager = LocalFocusManager.current

                    var apiInput by rememberSaveable { mutableStateOf("") }
                    var usernameInput by rememberSaveable { mutableStateOf("") }
                    var passwordInput by rememberSaveable { mutableStateOf("") }

                    var apiError by rememberSaveable { mutableStateOf(false) }
                    var usernameError by rememberSaveable { mutableStateOf(false) }
                    var passwordError by rememberSaveable { mutableStateOf(false) }

                    Column (
                        Modifier.fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        //Text("This feature is not yet available.")
                        //Spacer(Modifier.size(8.dp))

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
                            placeholder = { Text("ex: community") },
                            isError = apiError,
                            singleLine = true
                        )

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
                                showNewAPIDialog = false
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
                                    buttonClicked = true
                                    Log.d("AddAPIOKButton", "OK Clicked")

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
                                    authData = authRequest.getToken(apiInput, usernameInput, passwordInput)
                                }
                                if (authRequest.errorMessage.isEmpty()) {
                                    if (authData?.data?.token.isNullOrBlank()) {
                                        // token success
                                        showNewAPIDialog = false
                                    }

                                } else {
                                    // error
                                    Log.d("AddAPIAuth", authRequest.errorMessage)
                                    if (authRequest.errorMessage.startsWith("Unable to resolve host")) {
                                        showApiError = true
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
                                Text("Try using and API name like 'community' for the value.")
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

                }

            }
        }
    }
}