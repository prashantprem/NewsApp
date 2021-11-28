package com.example.newsapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.model.Article
import com.example.newsapp.model.newsModel
import com.example.newsapp.network.Api
import com.example.newsapp.network.ServiceBuilder
import com.example.newsapp.room.RoomAppDb
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import android.location.Geocoder

import android.location.LocationManager
import android.opengl.Visibility
import android.provider.Settings
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.io.IOException


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: NewsAdapter
    lateinit var context: Context
    lateinit var articleViewModel: ArticleViewModel
    var articleDatabase: RoomAppDb? = null
    var TAG: String  ="my activity"
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    private  var countryName = "in"
    private var flag = 0
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    lateinit var adView : AdView


    override fun onStart() {
        super.onStart()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
        if (checkPermissionForLocation(this)){
            startLocationUpdates()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLocationRequest = LocationRequest()
        context = this@MainActivity
        articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        recyclerView = findViewById(R.id.recylcerView)
        adapter = NewsAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps()
//        }
        RemoteConfiFirebase.RemoteConfigUtils.init()
        adView = findViewById<View>(R.id.adView) as AdView
        var temp =RemoteConfiFirebase.RemoteConfigUtils.getAdStatus()
        if(temp)
        {
            Toast.makeText(applicationContext,"Advertisement Turend ON", Toast.LENGTH_LONG).show()
            adView.visibility = View.VISIBLE
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            adView.adListener = object : AdListener(){
                override fun onAdFailedToLoad(p0: Int) {
                    super.onAdFailedToLoad(p0)
                    val toastMessage: String = "Ad fail to load: " + p0
                }
            }
        }
        else
        {
            Toast.makeText(applicationContext,"Advertisement Turend OFF", Toast.LENGTH_LONG).show()

        }

    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)

            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }

        val alert: AlertDialog = builder.create()
        alert.show()


    }
    fun getDataFromAPI(country:String)
    {

        val request = ServiceBuilder.buildService(Api::class.java)
        val call = request.getTopHeadlines(countryName,"14b4bdd536864da79b8e6745902e14db")

        call.enqueue(object :Callback<newsModel>
        {

            override fun onResponse(call: Call<newsModel>?, response: Response<newsModel>?) {
//                Log.d(TAG, response?.body().toString())
                if(response?.body() != null)
                {
                    var newsList:newsModel = response.body()!!
                    var articleList:List<Article> = newsList.articles
//                    for(i in newsList)
//                    articleList.toMutableList().add(i.articles)
                    articleViewModel.delete(context)
                    adapter.setArticlesList(articleList)
                    articleViewModel.insertData(context,articleList)

                }
            }

            override fun onFailure(call: Call<newsModel>?, t: Throwable?) {

                articleViewModel.getArticleDetails(context)?.observe(this@MainActivity, Observer {

                    if(it != null) {
                        adapter.setArticlesList(it)
                    }

                })


            }
        })

    }


     protected fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here

            mLastLocation = locationResult.lastLocation
            val date: Date = Calendar.getInstance().time
            val sdf = SimpleDateFormat("hh:mm:ss a")
            var country_name: String? = null
            val lm = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val geocoder = Geocoder(applicationContext)
            for (provider in lm.allProviders) {
                if (mLastLocation != null) {
                    try {
                        val addresses: List<Address>? =
                            geocoder.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
                        if (addresses != null && addresses.size > 0) {
                            country_name = addresses[0].getCountryName()
                            break
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            if (country_name != null) {
                getDataFromAPI(country_name.take(2))
//                countryName = country_name
            }
//            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a")
        var country_name: String? = null
        val lm = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(applicationContext)
        for (provider in lm.allProviders) {
            if (mLastLocation != null) {
                try {
                    val addresses: List<Address>? =
                        geocoder.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
                    if (addresses != null && addresses.size > 0) {
                        country_name = addresses[0].getCountryName()
                        break
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        if (country_name != null) {
            countryName = country_name
        }

    }
//        txtTime.text = "Updated at : " + sdf.format(date)
//        txtLat.text = "LATITUDE : " + mLastLocation.latitude
//        txtLong.text = "LONGITUDE : " + mLastLocation.longitude
//        // You can now create a LatLng Object for use with maps


    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()

            } else {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    override fun onPause() {
        if (adView!=null) {
            adView.pause();
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView.resume();
        }
    }

    override fun onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


}






