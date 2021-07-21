package com.weatherdemo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.BuildConfig
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_demo.*
import com.weatherdemo.R
import com.weatherdemo.databinding.ActivityDemoBinding
import com.weatherdemo.ui.viewmodel.DemoViewModel
import com.weatherdemo.util.CustomeProgressDialog
import com.weatherdemo.util.hasPermission
import com.weatherdemo.util.requestPermissionWithRationale
import java.util.*


/**
 * Class Name       :  **DemoActivity.KT****
 * Purpose          :  DemoActivity is activity contain mvvm demo with location and weather integration.
 * Developed By     :  **@radhu_android**
 * Created Date     :  **21-07-2021**
 *
 *
 * Modified Reason  :  ****
 * Modified By      :  **@radhu_android**
 * Modified Date    :  ****
 *
 *
 ** */

class DemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    }

    var binding: ActivityDemoBinding? = null
    var viewmodel: DemoViewModel? = null
    var customeProgressDialog: CustomeProgressDialog? = null
    var current_city:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo)
        viewmodel = ViewModelProviders.of(this).get(DemoViewModel::class.java)
        binding?.viewmodel = viewmodel
        customeProgressDialog = CustomeProgressDialog(this)
        initObservables()

        locationRequestOnClick()
    }

    private fun initObservables() {
        viewmodel?.yourCity?.set("Click to fetch your current city.")

        viewmodel?.progressDialog?.observe(this, Observer {
            if (it!!) customeProgressDialog?.show() else customeProgressDialog?.dismiss()
        })
        viewmodel?.backClick?.observe(this, Observer {
             this.finish()
        })
        viewmodel?.apiErroe?.observe(this, Observer { error ->
            Toast.makeText(this, "${error}", Toast.LENGTH_LONG).show()
        })
        viewmodel?.cityClick?.observe(this, Observer { error ->
            if (current_city.equals(""))
                locationRequestOnClick()
        })
        viewmodel?.prime?.observe(this, Observer {
            if (it) {
                viewmodel?.primeValue?.set(resources.getString(R.string.msg_prime_date))
                cardLayout.visibility = View.VISIBLE
            } else {
                viewmodel?.primeValue?.set(resources.getString(R.string.msg_non_prime_date))
                cardLayout.visibility = View.GONE
            }
        })
        viewmodel?.getDatePickerDialogData()?.observe(this, Observer { dis ->
            if (dis!!) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    var date_selected = ""
                    if (monthOfYear >= 0 && monthOfYear < 9 && dayOfMonth > 0 && dayOfMonth < 10)
                        date_selected = (year.toString() + "-0"
                                + (monthOfYear + 1).toString() + "-0"
                                + dayOfMonth.toString())
                    else if (monthOfYear >= 0 && monthOfYear < 9)
                        date_selected = (year.toString() + "-0"
                                + (monthOfYear + 1).toString() + "-"
                                + dayOfMonth.toString())
                    else if (dayOfMonth > 0 && dayOfMonth < 10)
                        date_selected = (year.toString() + "-"
                                + (monthOfYear + 1).toString() + "-0"
                                + dayOfMonth.toString())
                    else
                        date_selected = (year.toString() + "-"
                                + (monthOfYear + 1).toString() + "-"
                                + dayOfMonth.toString())

                    viewmodel?.onDateChange(current_city, date_selected, dayOfMonth)
                }, year, month, day)
                dpd.show()
            }
        })
    }








    //current location implementaion
    // The Fused Location Provider provides access to location APIs.
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private var cancellationTokenSource = CancellationTokenSource()

    // If the user denied a previous permission request, but didn't check "Don't ask again", this
    // Snackbar provides an explanation for why user should approve, i.e., the additional rationale.
    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(
                binding?.mainLayout!!,
                R.string.fine_location_permission_rationale,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.ok) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
            // Cancels location request (if in flight).
            cancellationTokenSource.cancel()
        }

    /**
     * Permission result
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult()")

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive an empty array.
                    Log.d(TAG, "User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    Snackbar.make(
                            binding?.mainLayout!!,
                            R.string.permission_approved_explanation,
                            Snackbar.LENGTH_LONG
                    )
                            .show()
                    if (canGetLocation()) requestCurrentLocation() else enableLocationSettings();
                }
                else -> {
                    Snackbar.make(
                            binding?.mainLayout!!,
                            R.string.fine_permission_denied_explanation,
                            Snackbar.LENGTH_LONG
                    )
                            .setAction(R.string.settings) {
                                // Build intent that displays the App settings screen.
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                        "package",
                                        BuildConfig.APPLICATION_ID,
                                        null
                                )
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            .show()
                }
            }
        }
    }


    /**
     * Gets current location.
     * Note: The code checks for permission before calling this method, that is, it's never called
     * from a method with a missing permission. Also, I include a second check with my extension
     * function in case devs just copy/paste this code.
     */
    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        Log.d(TAG, "requestCurrentLocation()")
        if (applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Returns a single current location fix on the device. Unlike getLastLocation() that
            // returns a cached location, this method could cause active location computation on the
            // device. A single fresh location will be returned if the device location can be
            // determined within reasonable time (tens of seconds), otherwise null will be returned.
            //
            // Both arguments are required.
            // PRIORITY type is self-explanatory. (Other options are PRIORITY_BALANCED_POWER_ACCURACY,
            // PRIORITY_LOW_POWER, and PRIORITY_NO_POWER.)
            // The second parameter, [CancellationToken] allows the activity to cancel the request
            // before completion.
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
            )
            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                val result = if (task.isSuccessful && task.result != null) {
                    val result: Location = task.result
                    "Location (success): ${result.latitude}, ${result.longitude}"

                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(result.latitude, result.longitude, 1)
                    current_city = addresses.get(0).locality
                    viewmodel?.yourCity?.set(current_city)

                    Log.e(TAG, "locality::" + addresses.get(0).locality)
                } else {
                    val exception = task.exception
                    "Location (failure): $exception"
                }

                Log.d(TAG, "getCurrentLocation() result: $result")
            }
        }
    }


    /**
     * Getting current location
     */
    fun locationRequestOnClick() {
        Log.d(TAG, "locationRequestOnClick()")
        val permissionApproved =
                applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionApproved) {
            if (canGetLocation()) requestCurrentLocation() else enableLocationSettings();
        } else {
            requestPermissionWithRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                    fineLocationRationalSnackbar
            )
        }
    }



    /**
     * To turn on gps of device
     */
    protected fun enableLocationSettings() {
        val locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this) { response: LocationSettingsResponse? -> }
                .addOnFailureListener(this) { ex: Exception? ->
                    if (ex is ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ex.startResolutionForResult(this, 1)
                        } catch (sendEx: SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode==1) {
            if(Activity.RESULT_OK == resultCode){
                //user clicked OK, you can startUpdatingLocation(...);
                locationRequestOnClick()
            }else{
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
            }
        }
    }


    /**
     * To check gps is enable or not
     */
    fun canGetLocation(): Boolean {
        val lm: LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // exceptions will be thrown if provider is not permitted.
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }
        try {
            networkEnabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }
        return gpsEnabled && networkEnabled
    }
}
