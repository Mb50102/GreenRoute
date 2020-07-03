package com.example.greenroute


import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.example.greenroute.geocodinglocation.GeoCodingDialogFragment
import com.example.greenroute.geocodinglocation.GeoCodingLocations

import com.example.greenroute.route.Route
import com.example.greenroute.settings.SettingsActivity

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_dialog.*
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.lang.Exception


class MainActivity : AppCompatActivity(), GeoCodingDialogFragment.NoticeDialogListener {


    private lateinit var geoCodinglocations: GeoCodingLocations

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var  preference : SharedPreferences

    private lateinit var route: Route

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        geoCodinglocations = GeoCodingLocations(this,map_view)

        preference = PreferenceManager.getDefaultSharedPreferences(this)

        route = Route(this, map_view)


        Timber.plant(DebugTree())

        map_view.onCreate(savedInstanceState)

        map_view.mapScene.loadScene(MapScheme.NORMAL_DAY) {
            map_view.camera.lookAt(GeoCoordinates(45.801408, 15.970728),10000.0)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater=menuInflater
        supportActionBar?.setDisplayShowTitleEnabled(false)
        inflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.traffic_button-> {
                if(!item.isChecked){
                    showTraffic(true)
                    item.isChecked = true
                }else{
                    showTraffic(false)
                    item.isChecked = false
                }
                true
            }
            R.id.search_button -> {
                geocodingSearch()
                true
            }
            R.id.start_button -> {
                startRoute()
                true
            }
            R.id.reset_button -> {
                resetRoute()
                true
            }
            R.id.reset_locations -> {
                resetLocations()
                true
            }
            R.id.setings_button -> {
                settings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetLocations() {
        geoCodinglocations.clearLocations()
        Toast.makeText(this,"Trenutna lokacija i odredište su resetirani",Toast.LENGTH_SHORT).show()
    }

    private fun showTraffic(showTraffic:Boolean) {
        if(showTraffic){
            try {
                map_view.mapScene.setLayerState(MapScene.Layers.TRAFFIC_FLOW,MapScene.LayerState.VISIBLE)
                map_view.mapScene.setLayerState(MapScene.Layers.TRAFFIC_INCIDENTS, MapScene.LayerState.VISIBLE)
            } catch (e : Exception) {
                Toast.makeText(this, "Exception when enabling traffic visualization.", Toast.LENGTH_LONG).show()
            }

        }else{
            try {
                map_view.mapScene.setLayerState(MapScene.Layers.TRAFFIC_FLOW,MapScene.LayerState.HIDDEN)
                map_view.mapScene.setLayerState(MapScene.Layers.TRAFFIC_INCIDENTS, MapScene.LayerState.HIDDEN)
            } catch (e : Exception) {
                Toast.makeText(this, "Exception when enabling traffic visualization.", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun settings() {
        startActivity(Intent(this, SettingsActivity::class.java))

    }

    private fun resetRoute() {
        geoCodinglocations.clearMarkers()
        route.clearRoute()

    }

    private fun startRoute() {
        val startRouteFromCurrentLocation=preference.getBoolean("Start_route_from_current_location_preference",true)
        val minJamFactor=preference.getInt("jam_factor_preference",4).toString()
        val radius=preference.getInt("radius_preference",250)
        val vehicle = preference.getString("vehicle_preference","bike")

        val drawDefaultRoute=preference.getBoolean("display_default_route_preference",false)
        if(startRouteFromCurrentLocation){
                if(geoCodinglocations.checkForDestination()){
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location? ->
                            val bbox=location?.latitude.toString()+","+location?.longitude.toString()+";"+geoCodinglocations.destination.latitude.toString()+","+geoCodinglocations.destination.longitude.toString()
                            route.createRoute(minJamFactor, "sh", radius, bbox, vehicle!!)
                            if(drawDefaultRoute){
                                route.getDefaultRoutePoints(location?.latitude.toString()+","+location?.longitude.toString(),
                                    geoCodinglocations.destination.latitude.toString()+","+geoCodinglocations.destination.longitude.toString(),
                                    vehicle
                                )
                            }
                        }
                }else {
                    Toast.makeText(this,"Odredište nije postavljeno!",Toast.LENGTH_SHORT).show()
                }

        }else {
            if(geoCodinglocations.checkForLocationAndDestination()){
                val bbox= geoCodinglocations.location.latitude.toString()+","+ geoCodinglocations.location.longitude.toString()+";"+geoCodinglocations.destination.latitude.toString()+","+geoCodinglocations.destination.longitude.toString()
                route.createRoute(minJamFactor, "sh", radius, bbox, vehicle!!)
                if(drawDefaultRoute){
                    route.getDefaultRoutePoints(
                        geoCodinglocations.location.latitude.toString()+","+ geoCodinglocations.location.longitude.toString(),
                        geoCodinglocations.destination.latitude.toString()+","+geoCodinglocations.destination.longitude.toString(),
                        vehicle
                    )
                }

            }else{
                Toast.makeText(this,"Odredište i lokacija nisu postavljeni!",Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun geocodingSearch() {
        val geoCodingDialog= GeoCodingDialogFragment()
        geoCodingDialog.show(supportFragmentManager, "geoCodingSearch")
    }

   override fun onDialogPositiveClick(dialog: DialogFragment) {
       val locationInput: String = dialog.dialog?.search_input?.text.toString()
       if (locationInput.isNotEmpty()) {
           geoCodinglocations.getMarkers(locationInput)
       }
   }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }
}

