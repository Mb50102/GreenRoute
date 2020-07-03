package com.example.greenroute.trafficdata


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL="https://traffic.ls.hereapi.com/"


private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
       .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface TrafficDataService {
    @GET("traffic/6.2/flow.json")
    fun getTraffic(@Query("apiKey") apiKey: String,
                   @Query("bbox") bbox: String,
                   @Query("minjamfactor") minjamfactor: String,
                   @Query("responseattributes") responseattributes : String):
            Call<TrafficData>
}

object TrafficDataApi {
    val retrofitService : TrafficDataService by lazy {retrofit.create(TrafficDataService::class.java)}
}

