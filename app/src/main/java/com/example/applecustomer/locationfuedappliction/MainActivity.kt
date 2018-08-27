package com.example.applecustomer.locationfuedappliction

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private val REQUEST_LOCATION = 199
    private val INTERVAL = (1000 * 10).toLong()
    private val FASTEST_INTERVAL = (1000 * 5).toLong()
    private val TAG = MainActivity::class.java.simpleName

    var locationRequest: LocationRequest? = null
    var googleApiClient: GoogleApiClient? = null

    var btnFusedLocation: TextView? = null
    var tvLocation: TextView? = null

    var builder: LocationSettingsRequest.Builder? = null

    private var let: Double? = null
    private var lon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setFinishOnTouchOutside(true)

        tvLocation = findViewById(R.id.textView)

        btnFusedLocation = findViewById(R.id.textView2)

        buildGoogleApiClient()

        val sp = getSharedPreferences("location", Context.MODE_PRIVATE)
        val lat = sp.getString("latitude", null)
        val long = sp.getString("longitude", null)
        Toast.makeText(this, "let is  share in bc" + lat, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "long is  share  in bc" + long, Toast.LENGTH_SHORT).show()

        removeBD.setOnClickListener {
            removeLocationUpdates()
        }

    }

    fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onConnected(p0: Bundle?) {
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest!!.interval = INTERVAL // Update location every second
        builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

        builder!!.setAlwaysShow(true)

        if (checkGpsPerimmisionAndLocationUpdate()) {
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                    123)
        }
    }

    override fun onConnectionSuspended(i: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString())
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Firing onLocationChanged..............................................")
        let = location.latitude
        lon = location.longitude
        updateUI()
    }

    fun checkGpsPerimmisionAndLocationUpdate(): Boolean {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder?.build())

            result.setResultCallback { result ->
                val status = result.status

                when (status.statusCode) {

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and checkGpsPerimmisionAndLocationUpdate the result in onActivityResult().
                        status.startResolutionForResult(this, REQUEST_LOCATION)


                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, e.toString())
                    }

                }
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, getPendingIntent())

            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult?) {
                            for (location in result!!.locations) {
                                let = location.latitude
                                lon = location.longitude
                            }
                            if (let != null && lon != null)
                                updateUI()
                        }
                    }, null)
            return true
        }
        return false
    }

    fun removeLocationUpdates() {
        val intent = Intent(this, BroadcastReceiver::class.java)
        intent.action = BroadcastReceiver.ACTION_PROCESS_UPDATES
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel()
    }


    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, BroadcastReceiver::class.java)
        intent.action = BroadcastReceiver.ACTION_PROCESS_UPDATES
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 199) {
            updateUI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (checkGpsPerimmisionAndLocationUpdate())
                    updateUI()
            } else {

            }
        }

    }

    public override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart fired ..............")
        googleApiClient?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        googleApiClient?.disconnect()
    }


    private fun updateUI() {
        tvLocation?.text = let.toString()
        btnFusedLocation?.text = lon.toString()
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
