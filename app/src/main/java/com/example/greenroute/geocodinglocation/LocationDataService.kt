package com.example.greenroute.geocodinglocation

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private  const val BASE_URL="https://graphhopper.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface LocationDataService{
    @GET("api/1/geocode")
    fun getLocation(@Query("q") q : String,
                            @Query("limit") limit : String,
                            @Query("key") key : String):
            Call<Location>

}

object LocationDataApi {
    val retrofitService : LocationDataService by lazy { retrofit.create(LocationDataService::class.java) }
}