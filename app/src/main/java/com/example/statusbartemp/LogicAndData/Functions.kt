package com.example.statusbartemp.LogicAndData

import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultAccuracyMeters
import com.example.statusbartemp.LogicAndData.Constants.Companion.ecmwfForeCastSurfacePointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.fmiForecastEditedPointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.harmonieSurfacePointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.latDegreesOf100Meters
import com.example.statusbartemp.LogicAndData.Constants.Companion.lonDegreesOf100Meters
import com.example.statusbartemp.LogicAndData.Constants.Companion.mepsSurfacePointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.stbDivider0
import com.example.statusbartemp.LogicAndData.Constants.Companion.stbDivider1
import com.weeklist.screens.utils.LocationData
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun latLonToApiLatLon4X(
    latitude : Double,
    longitude : Double,
    secretOffsetParameters : String,
    defaultAccuracyMeters : Int = Constants.defaultAccuracyMeters,
) : String {
    val latNoise = calculateNoiseOffsetDegrees(
        input = latitude,
        secretSeed = secretOffsetParameters.toLong(),
        latitude = true
    )
    val lonNoise = calculateNoiseOffsetDegrees(
        input = longitude,
        secretSeed = secretOffsetParameters.toLong(),
        latitude = false
    )
    
    var lat = latitude
    var lon = longitude
    val offsetDistance = calculateEuclideanLength(
        100.0 * latNoise/latDegreesOf100Meters,
        100.0 * lonNoise/lonDegreesOf100Meters
    )
    if (offsetDistance > 300.0 ){
        lat += latNoise * 700.0/(offsetDistance+400.0)
        lon += lonNoise * 700.0/(offsetDistance+400.0)
    }
    else if (offsetDistance < 100.0 ){
        lat += latNoise*1.25
        lon += lonNoise*1.25
    }
    else{
        lat += latNoise
        lon += lonNoise
    }
    
    val latAccuracy = accuracyMetersToDegreeAccuracy(defaultAccuracyMeters, latitude = true)
    val lonAccuracy = accuracyMetersToDegreeAccuracy(defaultAccuracyMeters, longitude = true)
    
    val outLat0 = roundDownWithAccuracy(
        lat, latAccuracy
    )
    val outLat1 = roundUpWithAccuracy(
        lat, latAccuracy
    )
    val outLon0 = roundDownWithAccuracy(
        lon, lonAccuracy
    )
    val outLon1 = roundUpWithAccuracy(
        lon, lonAccuracy
    )
    
    val outputString =
        "$outLat0,$outLon0$stbDivider0" +
                "$outLat0,$outLon1$stbDivider0" +
                "$outLat1,$outLon0$stbDivider0" +
                "$outLat1,$outLon1"
    
    
    return outputString
}

fun roundWithAccuracy(input : Double, accuracy : Double) : Double{
    return (input/accuracy).roundToInt().toDouble()*accuracy
}

fun roundDownWithAccuracy(input : Double, accuracy : Double) : Double{
    return floor(input/accuracy)*accuracy
}

fun roundUpWithAccuracy(input : Double, accuracy : Double) : Double{
    return ceil(input/accuracy)*accuracy
}


fun latestTemperatureToImageString(temp : Int, sign : Boolean) : String{
    if (sign){
        return "icon$temp"
    }
    else {
        return "iconminus${-temp}"
    }
}

fun accuracyMetersToDegreeAccuracy(meters : Int, latitude : Boolean = false, longitude : Boolean = false) : Double{
    if (latitude) return (meters.toDouble()/100.0)* latDegreesOf100Meters
    if (longitude) return (meters.toDouble()/100.0)* lonDegreesOf100Meters
    else return (defaultAccuracyMeters.toDouble()/100.0)*0.0014
}


fun formatTempToDisplay(temp : Double) : String {
    val sign = (temp >= 0)
    val absTemp = abs(temp)
    val decafied = (absTemp*10.0).roundToInt()
    val decimal = decafied.mod(10)
    val unitsInt = (decafied-decimal)/10
    val output = when (sign){
        true-> "$unitsInt.$decimal"
        false-> "-$unitsInt.$decimal"
    }
    return output
}

fun formatSourceToDisplay(source : String) : String {
    val output = when (source){
        harmonieSurfacePointSimpleQuery -> "Harmonie forecast, FMI"
        mepsSurfacePointSimpleQuery -> "Harmonie (meps) forecast, FMI"
        ecmwfForeCastSurfacePointSimpleQuery -> "ECMWF forecast, FMI"
        fmiForecastEditedPointSimpleQuery -> "FMI Scandinavian edited forecast"
        else -> ""
    }
    return output
}

fun calculateEuclideanLength(x : Double, y : Double) : Double {
    return sqrt(x.pow(2)+y.pow(2))
}

fun List<String>.toLocationData() : LocationData?{
    if (this.size == 3) {
        return LocationData(
            latitude = this[0].toDouble(),
            longitude = this[1].toDouble(),
            placeName = this[2]
        )
    }
    else if (this.size == 2) {
        return LocationData(
            latitude = this[0].toDouble(),
            longitude = this[1].toDouble(),
            placeName = ""
        )
    }
    else return null
}

fun String.toLocationDataList() : List<LocationData?>{
    val list = this.split(stbDivider0)
    val outputList : MutableList<LocationData?> = mutableListOf()
    for (s in list) {
        outputList.add(s.split(stbDivider1).toLocationData())
    }
    return outputList
    
}

fun String.toLatLonList() : List<String>{
    return this.split(stbDivider0)
}

fun LocationData.toStringWith(divider : String) : String{
    val list = listOf(this.latitude.toString(), this.longitude.toString(), this.placeName)
    return list.joinToString(divider)
}

