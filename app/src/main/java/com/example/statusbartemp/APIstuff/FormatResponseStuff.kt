package com.example.statusbartemp.APIstuff

import android.location.Location
import com.example.statusbartemp.LogicAndData.Constants
import com.example.statusbartemp.LogicAndData.DataPoint
import com.example.statusbartemp.LogicAndData.TimeFunctions
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.roundToInt

fun formatXMLResponseToTemp(
    response : String,
    latitude : Double,
    longitude : Double
) : DataPoint? {
    val data = extractDataPoints(response)
    if (data.isEmpty()) return null
    val data0 = data.filter{ abs(it.latitude - data.first().latitude) <= 0.00001}
    if (data0.isEmpty()) return null
    val data1 = data.filterNot{it in data0}
    val data00 = data0.filter{abs(it.longitude - data0.first().longitude) <= 0.0001}
    val data01 = data0.filterNot{it in data00}
    val data10 = data1.filter{abs(it.longitude - data1.first().longitude) <= 0.0001}
    val data11 = data1.filterNot{it in data10}
    val listOfDatas = listOf(data00, data01, data10, data11)
    val listOfTempDistances = mutableListOf<TempDistance>()
    for (dataSubset in listOfDatas){
        if (dataSubset.isNotEmpty()){
            val temp = dataSubset.sumOf{it.temp }/(dataSubset.size.toDouble())
            val distance = calculateDistance(
                latitude, longitude, dataSubset.first().latitude, dataSubset.first().longitude
            )+1
            listOfTempDistances.add(
                TempDistance(temp = temp, distanceSquared = (distance*distance).toDouble())
            )
        }
    }
    //Log.v("format xml", "listOfTempDistances = $listOfTempDistances")
    val tempPrediction = listOfTempDistances.sumOf { it.temp/(it.distanceSquared) }/
            listOfTempDistances.sumOf { 1.0/(it.distanceSquared) }
    val time1Millis =
        TimeFunctions.formatToLocalDate(
            time = data0.first().time,
            format = Constants.ISO_TIME_FORMAT
        ).toInstant(ZoneOffset.UTC).toEpochMilli()
    val time2Millis =
        TimeFunctions.formatToLocalDate(
            time = data0.last().time,
            format = Constants.ISO_TIME_FORMAT
        ).toInstant(ZoneOffset.UTC).toEpochMilli()
    val timeMeanMillis = (time1Millis+time2Millis)/2
    val timeMeanLocalDateTime =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMeanMillis), ZoneOffset.UTC
        )
    val timeMeanString = TimeFunctions.formatLocalDateTimeToString(
        timeMeanLocalDateTime,
        Constants.ISO_TIME_FORMAT
    )
    
    return data.first().copy(
        temp = tempPrediction,
        time = timeMeanString,
        latitude = latitude,
        longitude = longitude,
        distance = 0f
    )
    
}

fun extractDataPoints(response : String) : List<DataPoint> {
    val output = mutableListOf<DataPoint>()
    var parsed = response
    while (parsed.length>0){
        parsed = parsed.substringAfter("<gml:pos>", missingDelimiterValue = "")
        if (parsed.length==0) break
        val position = parsed.substringBefore("</gml:pos>", "")
        parsed = parsed.substringAfter("<BsWfs:Time>", "")
        val time = parsed.substringBefore("</BsWfs:Time>", "")
        parsed = parsed.substringAfter("<BsWfs:ParameterValue>", "")
        val tempString = parsed.substringBefore("</BsWfs:ParameterValue>", "")
        try{
            val temp = tempString.toDouble()
            val latitude = position.substringBefore(" ", "").toDouble()
            val longitude = position.substringAfter(" ","").toDouble()
            output.add(
                DataPoint(
                    time = time,
                    latitude = latitude,
                    longitude = longitude,
                    temp = temp
                )
            )
        }
        catch(e : Exception){
        }
    }
    if (output.isEmpty()){
        val timeString = with(response
                                  .substringAfterLast("<BsWfs:Time>", "")
                                  .substringBefore("</BsWfs:Time>", "")){
            if (this.isEmpty())  "time and location unavailable"
            else this
        }
        val tempString = response
            .substringAfterLast("<BsWfs:ParameterValue>", "")
            .substringBefore("</BsWfs:ParameterValue>", "")
        try{
            val temp = tempString.toDouble()
            output.add(
                DataPoint(
                    time = timeString,
                    latitude = 0.0,
                    longitude = 0.0,
                    temp = temp
                )
            )
        }
        catch(e : Exception) {
        }
    }
    return output.toList()
    
}

fun calculateDistance(latitude1 : Double, longitude1 : Double, latitude2 : Double, longitude2 : Double) : Float{
    val results = FloatArray(1, {0f})
    Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results)
    return results[0]
    //return (latitude1-latitude2)*(latitude1-latitude2)+(longitude1-longitude2)*(longitude1-longitude2)
}

fun isResponseValid(response : String) : Boolean {
    val substring = response
        .substringAfterLast("<BsWfs:ParameterValue>", "")
        .substringBefore("</BsWfs:ParameterValue>", "")
    try {
        val double = substring.toDouble()
        return substring != ""
    }
    catch(e : Exception){
        return false
    }
}

fun formatDistance(distance : Float) : String{
    var temp = (distance*10f).roundToInt()
    val tempString = temp.toString()
    var kilometers = tempString.dropLast(1)
    if (kilometers == "") kilometers = "0"
    return kilometers+"."+tempString.takeLast(1)
    
}

data class TempDistance(
    val temp : Double,
    val distanceSquared : Double
)