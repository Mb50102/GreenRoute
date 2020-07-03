package com.example.greenroute.trafficdata

data class TrafficData(
    val CREATED_TIMESTAMP: String?,
    val MAP_VERSION: String?,
    val RWS: List<RWS>?,
    val UNITS: String?,
    val VERSION: String?
)