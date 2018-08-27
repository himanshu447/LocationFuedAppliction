package com.example.applecustomer.locationfuedappliction

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.LocationResult

class LocationUpdatesIntentService : IntentService("MyIntentService") {


    companion object {
        public val ACTION_PROCESS_UPDATES = "123"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {

            val i = Intent()
            val lat = intent.getStringExtra("latitude")
            val long = intent.getStringExtra("lontitude")


            val sp = getSharedPreferences("location", Context.MODE_PRIVATE)?.edit()
            sp?.putString("latitude", lat)
            sp?.putString("longitude", long)
            Log.e("tag", lat)
            Log.e("tag", long)
            sp?.apply()
            addNotification(lat, long)


        }
    }

    private fun addNotification(toString: String, toString1: String) {

        val notificationBuilder = NotificationCompat.Builder(this)
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_focused)
        notificationBuilder.setContentTitle("Current Location")
        notificationBuilder.setContentText("let is '$toString' long is '$toString1'")
        notificationBuilder.setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager!!.notify(0, notificationBuilder.build())
    }
}