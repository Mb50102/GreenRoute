package com.example.greenroute.geocodinglocation

data class Hit(
    val city: String,
    val country: String,
    val extent: List<Double>?,
    val name: String,
    val osm_id: String,
    val osm_key: String,
    val osm_type: String,
    val osm_value: String,
    val point: Point,
    val postcode: String
)