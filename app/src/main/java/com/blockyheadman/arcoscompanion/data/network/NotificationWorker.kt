package com.blockyheadman.arcoscompanion.data.network

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blockyheadman.arcoscompanion.apis
import com.blockyheadman.arcoscompanion.data.classes.MessageList
import com.blockyheadman.arcoscompanion.getAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

var messageList = CoroutineScope(Dispatchers.IO).async {
    var data: MessageList? = null
    apis.forEach { api ->
        val token: String? = async {
            getAuthToken(
                apis[apis.indexOf(api)].name,
                apis[apis.indexOf(api)].username,
                apis[apis.indexOf(api)].password,
                apis[apis.indexOf(api)].authCode
            )
        }.await()

        if (token.isNullOrBlank()) {
            Log.e("GET MESSAGES", "Empty token. Stopping..")
            return@async null
        }

        data = async { ApiCall().getMessages(api.name, api.authCode, token) }.await()
    }
    return@async data
}

class NotificationWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            val apiCall = ApiCall()

            val token = apiCall.getToken(
                "community.arcapi.nl",
                "Blocky",
                "NiceTry",
                ""
            )?.data?.token
            Log.d("ApiCallErrorMsg", apiCall.errorMessage)
            Log.d("Token", "Token: $token")

            if (token.isNullOrBlank()) {
                Log.e("TokenError", "Token failed to be created.")
                return@launch
            }

            val messageList = apiCall.getMessages(
                "community.arcapi.nl",
                "",
                token
            )
            Log.d("ApiCallErrorMsg", apiCall.errorMessage)
            Log.d("MessageList", "List: $messageList")

            apiCall.deAuthToken(
                "community.arcapi.nl",
                "",
                token
            )
            Log.d("ApiCallErrorMsg", apiCall.errorMessage)
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}