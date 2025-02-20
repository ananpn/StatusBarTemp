package com.example.statusbartemp.LogicAndData

data class DataPoint(
    val time : String,
    val latitude : Double,
    val longitude : Double,
    val temp : Double,
    val distance : Float = 0f
)
