package com.example.greenroute.trafficdata



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.StringBuilder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sqrt


private const val R=6371000

class TrafficDataNetwork {


     fun getTrafficData(apiKey:String, bbox:String, minjamfactor:String, responseAttributes: String, radius: Int): LiveData<String> {
         val trafficPoints= MutableLiveData<String>()
         TrafficDataApi.retrofitService.getTraffic(
             apiKey,
             bbox,
             minjamfactor,
             responseAttributes
         ).enqueue(object: Callback<TrafficData>{
             override fun onFailure(call: Call<TrafficData>, t: Throwable) {
                 Timber.i("failure getting traffic data:$t")
             }
             override fun onResponse(call: Call<TrafficData>, response: Response<TrafficData>) {
                 if (response.isSuccessful && response.body() != null) {
                     val locations=bbox.split(";")
                     trafficPoints.value=getTrafficPoints(response.body()!!,radius,locations[0].split(","),locations[1].split(","))
                 }else{
                     trafficPoints.value=" "
                 }
             }
         })
        return trafficPoints
    }

    private fun getTrafficPoints(trafficDataResult: TrafficData, radius: Int, location:List<String>,destination:List<String>) : String {
        val pointList= mutableListOf<String>()
        val pointString=StringBuilder()
        var distanceCovered : Int
        val safetyMeasure=radius*0.1+radius
        val radiusSafety=(radius-radius*0.15).toInt()
        trafficDataResult.RWS?.let {
            it.forEach {
                it.RW.let {
                    it.forEach {
                        it.FIS.let {
                            it.forEach{
                                it.FI.let {
                                    it.forEach {
                                        val length= it.TMC.LE*1000
                                        it.SHP.let {
                                            distanceCovered=0
                                            var newPointAdded= false
                                            it.forEach {
                                                val newPoints=it.value[0].split(" ")

                                                for(point in newPoints){
                                                    if(point.isNotEmpty() && pointList.size<130){
                                                        if(!newPointAdded) {
                                                            val distanceBetweenLocationAndPoint=distanceBetweenTwoCoordinates(location,point.split(","))
                                                            val distanceBetweenDestinationAndPoint =distanceBetweenTwoCoordinates(destination,point.split(","))
                                                            if(distanceBetweenDestinationAndPoint>safetyMeasure && distanceBetweenLocationAndPoint>safetyMeasure){
                                                                pointList.add(point)
                                                                pointString.append(point).append(",").append(radiusSafety).append(";")
                                                                newPointAdded = true
                                                                distanceCovered += radius / 2
                                                            }else{
                                                                val minimum=min(distanceBetweenDestinationAndPoint,distanceBetweenLocationAndPoint).toInt()
                                                                Timber.i("minimum:$minimum")
                                                                pointList.add(point)
                                                                pointString.append(point).append(",").append(minimum/2).append(";")
                                                                newPointAdded = true
                                                                distanceCovered+=minimum/2
                                                            }
                                                        }else  {
                                                            if(length>distanceCovered) {
                                                                val lastIndex = pointList.lastIndex
                                                                val lastPoint = pointList[lastIndex]
                                                                val lastCordinates = lastPoint.split(",")
                                                                val newCordinates = point.split(",")

                                                                val distance =
                                                                    distanceBetweenTwoCoordinates(
                                                                        lastCordinates,
                                                                        newCordinates
                                                                    )

                                                                if (distance >= radius / 2) {
                                                                    val  distanceBetweenLocationAndPoint=distanceBetweenTwoCoordinates(location,point.split(","))
                                                                    val distanceBetweenDestinationAndPoint =distanceBetweenTwoCoordinates(destination,point.split(","))

                                                                    if(distanceBetweenDestinationAndPoint>safetyMeasure && distanceBetweenLocationAndPoint>safetyMeasure){
                                                                        pointList.add(point)
                                                                        pointString.append(point).append(",").append(radiusSafety).append(";")
                                                                        distanceCovered += radius / 2

                                                                    }else{
                                                                        val minimum=min(distanceBetweenDestinationAndPoint,distanceBetweenLocationAndPoint).toInt()
                                                                        Timber.i("minimum:$minimum")
                                                                        pointList.add(point)
                                                                        pointString.append(point).append(",").append(minimum/2).append(";")
                                                                        distanceCovered+=minimum/2
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return pointString.toString()
    }

    private fun distanceBetweenTwoCoordinates(
        lastCordinates: List<String>,
        newCordinates: List<String>
    ): Double {
        val x: Double =
            ((lastCordinates[0].toDouble() - newCordinates[0].toDouble()) * PI / 180) * cos(
                ((lastCordinates[1].toDouble() + newCordinates[1].toDouble()) * PI / 180) / 2
            )

        val y: Double =
            (lastCordinates[1].toDouble() - newCordinates[1].toDouble()) * PI / 180

        return sqrt(x * x + y * y) * R
    }

}