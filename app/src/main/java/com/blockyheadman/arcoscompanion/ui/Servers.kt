package com.blockyheadman.arcoscompanion.ui

import android.os.VibrationEffect
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.blockyheadman.arcoscompanion.data.network.AuthResponse
import com.blockyheadman.arcoscompanion.data.network.AuthViewModel
import com.blockyheadman.arcoscompanion.vibrator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersPage(externalPadding: PaddingValues) {
    //val authViewModel = AuthViewModel()

    /*LaunchedEffect(Unit, block = {
        authViewModel.getToken("community")
    })*/

    var showNewAPIDialog by rememberSaveable { mutableStateOf(false) }

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
            /*if (authViewModel.errorMessage.isEmpty()) {
                val authData: AuthResponse? = authViewModel.auth

                if (authData?.data?.token == null) {
                    Text("null")
                } else {
                    Text(authData.data.token)
                }

                //Text("Auth Success!")
            } else {
                Text(authViewModel.errorMessage)
            }*/
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
                    var apiInputError = false
                    var usernameInput by rememberSaveable { mutableStateOf("") }
                    var passwordInput by rememberSaveable { mutableStateOf("") }

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
                            onValueChange = { apiInput = it },
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
                            isError = apiInputError,
                            singleLine = true
                        )

                        OutlinedTextField(
                            label = { Text("Username:") },
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            keyboardOptions = KeyboardOptions(
                                KeyboardCapitalization.Words,
                                false,
                                KeyboardType.Text,
                                ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            label = { Text("Password:") },
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            keyboardOptions = KeyboardOptions(
                                KeyboardCapitalization.None,
                                false,
                                KeyboardType.Password,
                                ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )

                        var buttonClicked by rememberSaveable { mutableStateOf(false) }

                        Button(onClick = {
                            buttonClicked = true
                            Log.d("AddAPIOKButton", "OK Clicked")

                            //showNewAPIDialog = false
                            vibrator.vibrate(
                                VibrationEffect.createPredefined(
                                    VibrationEffect.EFFECT_DOUBLE_CLICK
                                )
                            )



                        }) {
                            Text("OK")
                        }

                        if (buttonClicked) {
                            val authRequest = AuthViewModel()

                            /*LaunchedEffect(Unit, block = {
                                authRequest.getToken(apiInput)
                            })*/
                            
                            LaunchedEffect(authRequest) {
                                coroutineScope {
                                    launch { authRequest.getToken(apiInput) }
                                }
                            }

                            if (authRequest.errorMessage.isEmpty()) {
                                val authData: AuthResponse? = authRequest.auth

                                /*if (authData?.data?.token == null) {
                                    // null
                                    Log.d("AddAPIAuth", "Token null")
                                } else */

                                if (authData?.data?.token != null) {
                                    // token success
                                    Log.d(
                                        "AddAPIAuth",
                                        "Token: ${authRequest.auth!!.data.token}"
                                    )
                                    showNewAPIDialog = false
                                }

                            } else {
                                Log.d("AddAPIAuth", authRequest.errorMessage)
                                // error
                            }
                        }

                    }
                }
            }
        }
    }
}