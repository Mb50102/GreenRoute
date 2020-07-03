package com.example.greenroute.trafficdata

data class RWS(
    val EBU_COUNTRY_CODE: String,
    val EXTENDED_COUNTRY_CODE: String,
    val MAP_VERSION: String,
    val RW: List<RW>,
    val TABLE_ID: String,
    val TY: String,
    val UNITS: String
)