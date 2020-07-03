package com.example.greenroute.route


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL="https://graphhopper.com/"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface RouteDataService {
    @GET("api/1/route")
    fun getRouteData(@Query("key") apiKey: String,
                     @Query("point") location: String,
                     @Query("point") destination: String,
                     @Query("vehicle") vehicle: String,
                     @Query("points_encoded") points_encode:String,
                     @Query("instructions") instructions: String,
                     @Query("block_area") block_area: String,
                     @Query("ch.disable") ch: String):
            Call<RouteData>
    @GET("api/1/route")
    fun getRouteDataWithoutBlocking(@Query("key") apiKey: String,
                     @Query("point") location: String,
                     @Query("point") destination: String,
                     @Query("vehicle") vehicle: String,
                     @Query("points_encoded") points_encode:String,
                     @Query("instructions") instructions: String,
                     @Query("ch.disable") ch: String):
            Call<RouteData>
}

object RouteDataApi {
    val retrofitService : RouteDataService by lazy {retrofit.create(RouteDataService::class.java)}
}