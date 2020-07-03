package com.example.greenroute.geocodinglocation


import android.content.Context
import android.widget.Toast
import com.example.greenroute.R
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Point2D
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapView
import com.here.sdk.mapview.MapViewBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class GeoCodingLocations(private val context: Context, private val mapView:MapView) {



    private val mapMarkerList = mutableListOf<MapMarker>()

    lateinit var destination:GeoCoordinates
    private var destinationSet=false

    lateinit var location:GeoCoordinates
    private var locationSet=false


    fun getMarkers(locationInput: String){
        LocationDataApi.retrofitService.getLocation(
            locationInput,
            context.getString(R.string.numberOfMarkers),
            context.getString(R.string.GraphHopperApiKey)
        ).enqueue(object : Callback<Location> {
            override fun onFailure(call: Call<Location>, t: Throwable) {
                Timber.i("failure getting location data:$t")
            }
            override fun onResponse(call: Call<Location>, response: Response<Location>) {
                if (response.isSuccessful && response.body() != null) {
                    createMarkers(response.body()!!)
                }
            }
        })
    }

    private fun createMarkers(locations: Location) {
        locations.hits.forEach {
            val points=it.point
            val geo= GeoCoordinates(points.lat,points.lng)
            val mapImage= MapImageFactory.fromResource(context.resources, R.mipmap.poi)
            val mapMarker = MapMarker(geo,mapImage)
            mapView.mapScene.addMapMarker(mapMarker)
            mapMarkerList.add(mapMarker)
        }
        setTapGestureHandler()
    }

    private fun setTapGestureHandler(){
        mapView.gestures.tapListener = TapListener { touchPoint: Point2D? ->
            val radiusInPixel = 2.0
            mapView.pickMapItems(touchPoint!!,radiusInPixel, MapViewBase.PickMapItemsCallback {
                val markers=it?.markers
                if(markers?.isEmpty()!!){
                    return@PickMapItemsCallback
                }else{
                    if(!destinationSet){
                        val mapMarker=markers[0]!!
                        val cordinates=mapMarker.coordinates
                        destination=GeoCoordinates(cordinates.latitude,cordinates.longitude)
                        destinationSet=true
                        Toast.makeText(context,"destination is set at:${destination.latitude},${destination.longitude}",Toast.LENGTH_SHORT).show()

                    }else if(!locationSet){
                        val mapMarker=markers[0]!!
                        val cordinates=mapMarker.coordinates
                        location=GeoCoordinates(cordinates.latitude,cordinates.longitude)
                        locationSet=true
                        Toast.makeText(context,"location is set at:${location.latitude},${location.longitude}",Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

   fun clearMarkers(){
       if(mapMarkerList.isEmpty()){
           Toast.makeText(context,"Nema markera!",Toast.LENGTH_SHORT).show()
           return
       }
        for(mapMarker in mapMarkerList){
            mapView.mapScene.removeMapMarker(mapMarker)
        }
       mapMarkerList.clear()
       destinationSet=false
       locationSet=false
   }

    fun checkForDestination():Boolean{
        return destinationSet
    }

    fun checkForLocationAndDestination(): Boolean {
        return destinationSet && locationSet
    }

    fun clearLocations() {
        destinationSet=false
        locationSet=false
    }
}
