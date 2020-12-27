package org.unifiedpush.connector

import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

fun registerApp(context: Context, distributor: String): String{
    val token = getToken(context).let{
        if(it.isNullOrEmpty()) newToken(context) else it
    }

    val broadcastIntent = Intent()
    broadcastIntent.`package` = distributor
    broadcastIntent.action = REGISTER
    broadcastIntent.putExtra("token", token)
    broadcastIntent.putExtra("application", context.packageName)
    context.sendBroadcast(broadcastIntent)
    Log.i("registerApp","sent with token=$token")
    return token
}

fun unregisterApp(context: Context, distributor: String){
    val token = getToken(context)!!
    val broadcastIntent = Intent()
    broadcastIntent.`package` = distributor
    broadcastIntent.action = UNREGISTER
    broadcastIntent.putExtra("token", token)
    broadcastIntent.putExtra("application", context.packageName)
    context.sendBroadcast(broadcastIntent)
}

fun getToken(context: Context): String{
    return context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)?.getString(
            PREF_MASTER_TOKEN, ""
    )?: ""
}

fun newToken(context: Context): String{
    val token = UUID.randomUUID().toString()
    context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE).edit().putString(PREF_MASTER_TOKEN, token).commit()
    return token
}