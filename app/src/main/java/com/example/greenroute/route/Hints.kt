package com.example.greenroute.route

import com.squareup.moshi.Json

data class Hints(

    @Json(name ="visited_nodes.sum") val nodesSum: String,
    @Json(name ="visited_nodes.average") val nodesAverage: String
)