package com.example.greenroute.route


import android.content.Context
import android.widget.Toast
import com.example.greenroute.R
import com.example.greenroute.trafficdata.TrafficDataNetwork
import com.here.sdk.core.Color
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.GeoPolyline
import com.here.sdk.mapview.MapPolyline
import com.here.sdk.mapview.MapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


@Suppress("NAME_SHADOWING")
class Route(val context:Context, private val mapView: MapView) {


     private val mapPolylines = mutableListOf<MapPolyline>()

    fun createRoute(minjamfactor: String, responseAttributes: String, radius: Int, bbox: String, vehicle: String){
        val trafficData= TrafficDataNetwork()
        GlobalScope.launch(Dispatchers.Main) {
            val trafficData =trafficData.getTrafficData(context.getString(R.string.HereApiKey),bbox,minjamfactor,responseAttributes,radius)
           trafficData.observeForever {
               val locations=bbox.split(";")
                getRoutePoints(it,locations[0],locations[1],vehicle)
            }
        }
    }


    private fun getRoutePoints(block_area:String,location:String,destination:String, vehicle:String) {
        RouteDataApi.retrofitService.getRouteData(
            context.getString(R.string.GraphHopperApiKey),
            location,
            destination,
            vehicle,
            "false",
            "false",
            block_area,
            "true"
        ).enqueue(object: Callback<RouteData> {
            override fun onFailure(call: Call<RouteData>, t: Throwable) {
                Timber.i("failure getting traffic data:$t")

            }
            override fun onResponse(call: Call<RouteData>, response: Response<RouteData>) {
                if(response.code()==400){
                    Toast.makeText(context,"Connection between locations not found",Toast.LENGTH_SHORT).show()
                }else if(response.isSuccessful && response.body() != null){
                    val lineColor = Color( 0x00,0x90,  0x8A,0xA0)
                    drawRoute(response.body()!!, lineColor)
                }
            }
        })
    }

    private fun drawRoute(routeData: RouteData, color: Color) {
        val coordinates: ArrayList<GeoCoordinates> = ArrayList()
        routeData.paths.let { list ->
            list.forEach{ path ->
                path.points.let{ points ->
                    points.coordinates.forEach {
                        coordinates.add(GeoCoordinates(it[1],it[0]))
                    }
                }
            }
        }

        val geoPolyline = GeoPolyline(coordinates)
        val widthInPixels = 14.0
        val mapPolyline = MapPolyline(geoPolyline, widthInPixels, color)

        mapPolylines.add(mapPolyline)
        mapView.mapScene.addMapPolyline(mapPolyline)

    }

    fun clearRoute(){
        for(mapPolyline in mapPolylines){
            mapView.mapScene.removeMapPolyline(mapPolyline)
        }
        mapPolylines.clear()
    }

   fun getDefaultRoutePoints(location:String, destination:String, vehicle:String){
       RouteDataApi.retrofitService.getRouteDataWithoutBlocking(
           context.getString(R.string.GraphHopperApiKey),
           location,
           destination,
           vehicle,
           "false",
           "false",
           "true"
       ).enqueue(object: Callback<RouteData> {
           override fun onFailure(call: Call<RouteData>, t: Throwable) {
               Timber.i("failure getting traffic data:$t")
           }

           override fun onResponse(call: Call<RouteData>, response: Response<RouteData>) {
               if (response.isSuccessful && response.body() != null){
                   val lineColor = Color( 0x30,0x20,  0x8A,0xA0)
                   drawRoute(response.body()!!, lineColor)

               }
           }
       })
   }


}