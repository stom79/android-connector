package org.unifiedpush.android.connector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

interface MessagingReceiverHandler {
    fun onNewEndpoint(context: Context?, endpoint: String)
    fun onRegistrationFailed(context: Context?)
    fun onRegistrationRefused(context: Context?)
    fun onUnregistered(context: Context?)
    fun onMessage(context: Context?, message: String)
}

open class MessagingReceiver(private val handler: MessagingReceiverHandler) : BroadcastReceiver() {
    private val up = Registration()
    override fun onReceive(context: Context?, intent: Intent?) {
        if (up.getToken(context!!) != intent!!.getStringExtra(EXTRA_TOKEN)) {
            return
        }
        when (intent!!.action) {
            ACTION_NEW_ENDPOINT -> {
                val endpoint = intent.getStringExtra(EXTRA_ENDPOINT)!!
                this@MessagingReceiver.handler.onNewEndpoint(context, endpoint)
            }
            ACTION_REGISTRATION_FAILED -> {
                val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "No reason supplied"
                Log.i("UP-registration", "Failed: $message")
                this@MessagingReceiver.handler.onRegistrationFailed(context)
                up.removeToken(context!!)
            }
            ACTION_REGISTRATION_REFUSED -> {
                val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "No reason supplied"
                Log.i("UP-registration", "Refused: $message")
                this@MessagingReceiver.handler.onRegistrationRefused(context)
                up.removeToken(context!!)
            }
            ACTION_UNREGISTERED -> {
                this@MessagingReceiver.handler.onUnregistered(context)
                up.removeToken(context!!)
                up.removeDistributor(context!!)
            }
            ACTION_MESSAGE -> {
                val message = intent.getStringExtra(EXTRA_MESSAGE)!!
                val id = intent.getStringExtra(EXTRA_MESSAGE_ID) ?: ""
                this@MessagingReceiver.handler.onMessage(context, message)
                acknowledgeMessage(context, id)
            }
        }
    }

    private fun acknowledgeMessage(context: Context, id: String) {
        val token = up.getToken(context)!!
        val broadcastIntent = Intent()
        broadcastIntent.`package` = up.getDistributor(context)
        broadcastIntent.action = ACTION_MESSAGE_ACK
        broadcastIntent.putExtra(EXTRA_TOKEN, token)
        broadcastIntent.putExtra(EXTRA_MESSAGE_ID, id)
        context.sendBroadcast(broadcastIntent)
    }
}
