package org.unifiedpush.android.connector_fcm_added.fcm

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.unifiedpush.android.connector_fcm_added.*

class FirebaseRedirectionService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("UP-FCM", "Firebase onNewToken $token")
        if (getDistributor(baseContext) == FCM_DISTRIBUTOR_NAME) {
            saveToken(baseContext, token)
            registerApp(baseContext)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("UP-FCM", "Firebase onMessageReceived ${message.messageId}")
        val intent = Intent()
        intent.action = ACTION_MESSAGE
        intent.setPackage(baseContext.packageName)
        intent.putExtra(EXTRA_MESSAGE, message.notification?.body.toString())
        intent.putExtra(EXTRA_MESSAGE_ID, message.messageId)
        intent.putExtra(EXTRA_TOKEN, getToken(baseContext))
        baseContext.sendBroadcast(intent)
    }
}