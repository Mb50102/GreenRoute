package com.example.greenroute.route

data class Path(
    val ascend: Double,
    val bbox: List<Double>,
    val descend: Double,
    val details: Details,
    val distance: Double,
    val legs: List<Any>,
    val points: Points,
    val points_encoded: Boolean,
    val snapped_waypoints: SnappedWaypoints,
    val time: Int,
    val transfers: Int,
    val weight: Double
)