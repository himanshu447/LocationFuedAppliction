package com.example.applecustomer.locationfuedappliction


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.LocationResult


class BroadcastReceiver : BroadcastReceiver() {

    companion object {
         val ACTION_PROCESS_UPDATES = "123"
    }

    override fun onReceive(p0: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val locations = result.lastLocation
                   /* val sp = p0?.getSharedPreferences("location", Context.MODE_PRIVATE)?.edit()
                    sp?.putString("latitude", locations.latitude.toString())
                    sp?.putString("longitude", locations.longitude.toString())
                    Log.e("tag",locations.longitude.toString())
                    Log.e("tag",locations.latitude.toString())
                    if (p0 != null) {
                       addNotification(p0,locations.longitude.toString(),locations.latitude.toString())
                    }
                    sp?.apply()*/

                    val i = Intent(p0,LocationUpdatesIntentService::class.java)
                    i.putExtra("latitude",locations.latitude.toString())
                    i.putExtra("lontitude",locations.longitude.toString())
                    Log.e("tag",locations.longitude.toString())
                    Log.e("tag",locations.latitude.toString())
                    p0?.startService(i)

                }
            }
        }
    }
    private fun addNotification(context: Context, toString: String, toString1: String) {

        val notificationBuilder = NotificationCompat.Builder(context)
                notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_focused)
                notificationBuilder.setContentTitle("Current Location")
                notificationBuilder.setContentText("let is '$toString' long is '$toString1'")
                notificationBuilder.setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager!!.notify(1, notificationBuilder.build())
    }
}