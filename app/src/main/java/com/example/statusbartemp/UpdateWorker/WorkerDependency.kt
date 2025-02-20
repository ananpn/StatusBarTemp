package com.example.statusbartemp.UpdateWorker

import android.content.Context
import com.example.statusbartemp.APIstuff.WeatherApi
import com.example.statusbartemp.APIstuff.calculateDistance
import com.example.statusbartemp.APIstuff.formatXMLResponseToTemp
import com.example.statusbartemp.APIstuff.isResponseValid
import com.example.statusbartemp.AlarmManager.isAlarmSet
import com.example.statusbartemp.AlarmManager.scheduleAlarm
import com.example.statusbartemp.Location.AppLocationInfa
import com.example.statusbartemp.LogicAndData.Constants.Companion.badResponses
import com.example.statusbartemp.LogicAndData.Constants.Companion.emptyDataStoreData
import com.example.statusbartemp.LogicAndData.Constants.Companion.stbDivider0
import com.example.statusbartemp.LogicAndData.Constants.Companion.stbDivider1
import com.example.statusbartemp.LogicAndData.Constants.Companion.storedQueryIdList
import com.example.statusbartemp.LogicAndData.DataPoint
import com.example.statusbartemp.LogicAndData.LogicConstants.Companion.FULL_FAILURE
import com.example.statusbartemp.LogicAndData.TimeFunctions
import com.example.statusbartemp.LogicAndData.TimeFunctions.Companion.getTimeNow
import com.example.statusbartemp.LogicAndData.latLonToApiLatLon4X
import com.example.statusbartemp.LogicAndData.latestTemperatureToImageString
import com.example.statusbartemp.LogicAndData.toLatLonList
import com.example.statusbartemp.LogicAndData.toLocationDataList
import com.example.statusbartemp.LogicAndData.toStringWith
import com.example.statusbartemp.Notifications.AppNotificationInfa
import com.example.statusbartemp.Prefs.PrefsImpl
import com.weeklist.screens.utils.LocationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.random.Random

@Singleton
class WorkerDependency @Inject constructor(
    private val locatMan : AppLocationInfa,
    private val notifMan : AppNotificationInfa,
    private val prefs: PrefsImpl,
    private val weatherApi : WeatherApi,
    private val tempUpdateMan : TemperatureUpdateInfa,
    context : Context
    
) {
    val context = context
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun initializeTemperatureUpdater() = coroutineScope.launch {
        readPrefs()
        scheduleAlarm(
            context = context,
            interval = storedData.updateInterval,
        )
        if (storedData.updateInterval != "Disabled"){
            superUpdateTemperature()
        }
    }


    var _isUpdating = MutableStateFlow<Boolean>(false)
    var isUpdating = _isUpdating.value

    fun setUpdateState(new : Boolean){
        _isUpdating.value = new
    }

    fun stopUpdate() = coroutineScope.launch(Dispatchers.IO){
        response = "stopped"
        setStatusMessage(STATUS_MESSAGE_STOPPING)
        
    }

    fun superUpdateTemperature() = coroutineScope.launch{
        setLastRun(getTimeNow("HH:mm:ss dd.MM.yyyy"))
        if (!isUpdating) {
            readPrefs()
            setUpdateState(true)
            response = "no response"
            var loopCount = 0
            checkSecretOffset()
            while (loopCount <= storedQueryIdList.lastIndex && response != "stopped"){
                obtainTemperaturePrediction(
                    queryNumber = loopCount,
                    onTemperatureObtained = {image ->
                        notifMan.sendNotification(
                            iconFilePath = image,
                            temperature = measuredTempDouble,
                            time = measuredTime,
                            distance = measuredDistance,
                            mode = storedQueryIdList[loopCount]
                        )
                        loopCount = storedQueryIdList.lastIndex+2
                    },
                    onFail = {
                        notifMan.sendErrorNotification(
                            time = getTimeNow("HH:mm:ss dd.MM.yyyy"),
                            mode = storedQueryIdList[loopCount]
                        )
                        loopCount += 1
                    }
                )
            }
            if (loopCount == storedQueryIdList.lastIndex+1){
                notifMan.sendErrorNotification(
                    time = getTimeNow("HH:mm:ss dd.MM.yyyy"),
                    mode = FULL_FAILURE
                )
            }
        }
        setUpdateState(false)
    }

    /*
    private val _dataFlow = MutableStateFlow(
        _dataFlowRaw.value.map {
            val locationDataList = it.locationDataString.toLocationDataList()
            DataStoreData(
                distance = it.distance,
                prevTemp = it.prevTemp,
                locationToUse = locationDataList[0] ?:storedData.locationToUse,
                locationReal = locationDataList[1] ?:storedData.locationReal,
                locationSaved = locationDataList[2] ?:storedData.locationSaved,
                measureTime = it.measureTime,
                measureSource = it.measureSource,
                updateInterval = it.updateInterval,
                useSavedLocation = it.useSavedLocation,
                lastRun = it.lastRun,
                historyString = it.historyString,
                secretOffsetParameters = it.secretOffsetParameters,
                lastQueryString = it.lastQueryString
            )
    
        }
    )
    */
    
    val dataFlow = prefs.dataFlow.map {
        val locationDataList = it.locationDataString.toLocationDataList()
        DataStoreData(
            distance = it.distance,
            prevTemp = it.prevTemp,
            locationToUse = locationDataList[0] ?:storedData.locationToUse,
            locationReal = locationDataList[1] ?:storedData.locationReal,
            locationSaved = locationDataList[2] ?:storedData.locationSaved,
            measureTime = it.measureTime,
            measureSource = it.measureSource,
            updateInterval = it.updateInterval,
            useSavedLocation = it.useSavedLocation,
            lastRun = it.lastRun,
            historyString = it.historyString,
            secretOffsetParameters = it.secretOffsetParameters,
            lastQueryString = it.lastQueryString
        )
        
    }

    var storedData : DataStoreData = emptyDataStoreData





    //Weather API *************************************************************************
    
    private var latestData : DataPoint? = null
    private var latestTemp = 0
    private var measuredTempDouble = 0.0
    private var measuredTime = ""
    private var measuredDistance = 0f
    private var response = "no response"
    
    val statusMessageFlow = MutableStateFlow("")
    
    
    suspend fun obtainTemperaturePrediction(
        queryNumber : Int = 0,
        onTemperatureObtained : (String) -> Unit = {},
        onFail : () -> Unit = {},
    ){
        var count = 0
        response = "no response"
        while(response in badResponses) {
            count += 1
            if (count == 4) {
                setStatusMessage(STATUS_MESSAGE_FAILURE)
                onFail()
                break
            }
            obtainLocation()
            if (!storedData.useSavedLocation){
                delay(2400)
            }
            if (response == "stopped") break
            if (count==1)
                setStatusMessage(STATUS_MESSAGE_UPDATE_TEMP)
            else
                setStatusMessage("$STATUS_MESSAGE_UPDATE_TEMP, attempt $count")
            if (response == "stopped") break
            if (response in badResponses) {
                queryTemperaturePrediction(
                    queryNumber = queryNumber,
                    loopCount = count,
                )
            }
            var innercount = 0
            while (response == "no response" && innercount<20) {
                delay(500)
                innercount += 1
            }
            if (response == "stopped") break
            if (response in badResponses){
                delay(1000*8*1)
            }
        }
        if (response !in badResponses && response != "stopped"){
            latestData = formatXMLResponseToTemp(
                response = response,
                latitude = storedData.locationReal.latitude,
                longitude = storedData.locationReal.longitude,
            ) ?:latestData
            latestData?.let{it->
                measuredTempDouble = it.temp
                measuredTime = it.time
                measuredDistance = it.distance
                //Log.v("workdep", "latestData = $latestData")
                setStatusMessage(STATUS_MESSAGE_SUCCESS)
                
                sign = (measuredTempDouble >= 0.0)
                latestTemp = when{
                    (measuredTempDouble < 0.0)
                    -> (measuredTempDouble-0.01).roundToInt()
                    else -> measuredTempDouble.roundToInt()
                }
                prefs.saveData(
                    prevTemp = measuredTempDouble,
                    measureTime = measuredTime,
                    distance = measuredDistance,
                    measureSource = storedQueryIdList[queryNumber],
                )
                setResultToHistory(
                    temp = measuredTempDouble,
                    locationString = storedData.locationReal.placeName,
                    time = measuredTime
                )
                onTemperatureObtained(setImageToDisp())
            }
        }
    }
    
    
    fun queryTemperaturePrediction(
        //place : String? = null,
        queryNumber: Int = 0,
        loopCount : Int,
    ) {
        coroutineScope.launch {
            readPrefs()
            val laterTimeOffset = when(loopCount){
                1 -> 30
                2 -> 5*60
                3 -> 45*60
                else -> 45*60
            }.toLong()
            val earlierTimeOffset = -laterTimeOffset
            val earlierTime = TimeFunctions.getISOTimeWithSecondOffset(earlierTimeOffset)
            val laterTime = TimeFunctions.getISOTimeWithSecondOffset(laterTimeOffset)
            
            val timestep = when(loopCount){
                1 -> "1"
                2 -> "10"
                3 -> null
                else -> null
            }
            val latlon = storedData.lastQueryString.toLatLonList()
            
            response = weatherApi.makeWFSQueryToFMI(
                storedquery_id = storedQueryIdList[queryNumber],
                //storedquery_id = storedQueryIdList[queryNumber],
                latlon = latlon,
                timestep = timestep,
                starttime = earlierTime,
                endtime = laterTime
            ) ?:"no response"
            
            
            //Log.v("querytemp", "storedData.locationToUse = ${storedData.locationToUse}")
            //Log.v("querytemp", "storedData.locationReal = ${storedData.locationReal}")
            //Log.v("querytemp", "storedData.locationSaved = ${storedData.locationSaved}")
            //response = generateTestResponse()
            
            /*
            
                        response = generateTestResponse()
                        println(response)
            */
            
            if (!isResponseValid(response)) response = "invalid response"
        }
    }
    



    var imageToDisp ="icon0"
    var sign = false

    fun setImageToDisp() : String {
        imageToDisp = latestTemperatureToImageString(latestTemp, sign)
        return imageToDisp
    }

    //Location ****************************************************************************
    
    suspend fun obtainLocation() {
        readPrefs()
        if (!storedData.useSavedLocation){
            setStatusMessage(STATUS_MESSAGE_UPDATE_LOC)
            locatMan.GetCurrentLocation(
                onLocationReceived = { coroutineScope.launch {
                    val placeName = obtainPlaceName(it.latitude, it.longitude)
                    val newLocationData = LocationData(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        placeName = placeName
                    )
                    if (calculateDistance(
                            latitude1 = it.latitude,
                            longitude1 = it.longitude,
                            latitude2 = storedData.locationToUse.latitude,
                            longitude2 = storedData.locationToUse.longitude
                        ) > 100f) {
                            prefs.saveData(
                                locationDataString =
                                    listOf(
                                        newLocationData.toStringWith(stbDivider1),
                                        newLocationData.toStringWith(stbDivider1),
                                        storedData.locationSaved.toStringWith(stbDivider1)
                                    ).joinToString(stbDivider0),
                                lastQueryString = latLonToApiLatLon4X(
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                    secretOffsetParameters = storedData.secretOffsetParameters
                                )
                            )
                        }
                    else {
                        prefs.saveData(
                            locationDataString =
                                listOf(
                                    storedData.locationToUse.toStringWith(stbDivider1),
                                    newLocationData.toStringWith(stbDivider1),
                                    storedData.locationSaved.toStringWith(stbDivider1)
                                ).joinToString(stbDivider0),
                            lastQueryString = latLonToApiLatLon4X(
                                latitude = storedData.locationToUse.latitude,
                                longitude = storedData.locationToUse.longitude,
                                secretOffsetParameters = storedData.secretOffsetParameters
                            )
                        )
                    }
                }},
                onException = {
                    coroutineScope.launch{
                        setStatusMessage(STATUS_MESSAGE_UPDATE_LOC_FAILURE)
                    }
                },
                onPermissionsDenied = {
                    coroutineScope.launch{
                        setStatusMessage(STATUS_MESSAGE_UPDATE_LOC_FAILURE)
                    }
                }
            )
        }
        else {
            prefs.saveData(
                locationDataString =
                listOf(
                    storedData.locationToUse.toStringWith(stbDivider1),
                    storedData.locationToUse.toStringWith(stbDivider1),
                    storedData.locationSaved.toStringWith(stbDivider1)
                ).joinToString(stbDivider0),
                lastQueryString = latLonToApiLatLon4X(
                    latitude = storedData.locationToUse.latitude,
                    longitude = storedData.locationToUse.longitude,
                    secretOffsetParameters = storedData.secretOffsetParameters
                )
            )
        }
    }
    
    fun saveManualLocation(
        title : String,
        lat : Double,
        lon : Double
    ) = coroutineScope.launch{
        //readPrefs()
        val locationToSaveString = LocationData(
            latitude = lat,
            longitude = lon,
            placeName = title
        ).toStringWith(stbDivider1)
        prefs.saveData(
            locationDataString =
                listOf(
                    locationToSaveString,
                    locationToSaveString,
                    locationToSaveString
                ).joinToString(stbDivider0)
        )
    }
    
    fun applyManualLocation(
        title : String,
        lat : Double,
        lon : Double
    ) = coroutineScope.launch{
        //readPrefs()
        val locationToSaveString = LocationData(
            latitude = lat,
            longitude = lon,
            placeName = title
        ).toStringWith(stbDivider1)
        prefs.saveData(
            locationDataString =
            listOf(
                locationToSaveString,
                locationToSaveString,
                storedData.locationSaved
            ).joinToString(stbDivider0)
        )
        
        
    }

    suspend fun obtainPlaceName(lat : Double, lon : Double) : String {
        try{
            val locationStr = locatMan.GetLocationString(lat, lon)
            locationStr?.let{return it}
        }
        catch (e : Exception){}
        return ""
    }

    fun checkBackGroundProcessState() : Boolean {
        var output = false
        try {
            if (isAlarmSet(context)) output = true
        }
        catch (e : Exception){
        }

        return output

    }

    fun initCheckWorker(start : Boolean) = coroutineScope.launch{
        tempUpdateMan.StartCheckWorker(start)
    }

    fun checkWorkerWork() = coroutineScope.launch{
        val isRunning = checkBackGroundProcessState()
        if (!isRunning && storedData.updateInterval != "Disabled"){
            initializeTemperatureUpdater()
        }
        if (storedData.updateInterval == "Disabled"){
            tempUpdateMan.StartCheckWorker(false)
        }
    }
    

    suspend fun readPrefs() {
        storedData = dataFlow.firstOrNull() ?:storedData
    }
    
    val STATUS_MESSAGE_UPDATE_LOC = "Getting location"
    val STATUS_MESSAGE_UPDATE_LOC_FAILURE = "Failed to update location"
    val STATUS_MESSAGE_UPDATE_TEMP = "Updating temperature"
    val STATUS_MESSAGE_STOPPING = "Stopping"
    val STATUS_MESSAGE_FAILURE = "Failure!"
    val STATUS_MESSAGE_SUCCESS = "Success!"
    
    var statusMessageOKToChange = true

    fun setStatusMessage(new : String) = coroutineScope.launch {
        
        var count = 0
        while (!statusMessageOKToChange && count < 20) {
            delay(100)
            count += 1
        }
        if (statusMessageOKToChange){
            statusMessageOKToChange = false
            statusMessageFlow.value = new
        }
        count = 0
        while (count < 17) {
            delay(100)
            count += 1
        }
        statusMessageOKToChange = true
        delay(400)
        if (statusMessageOKToChange)
            statusMessageFlow.value = ""
    }

    fun setLastRun(time : String)= coroutineScope.launch {
        prefs.saveLastRun(time)
    }
    
    
    suspend fun setResultToHistory(temp : Double, locationString : String, time : String) {
        readPrefs()
        val historyDataPoints = storedData.historyString
            .toHistoryDataPointList().toMutableList()
        while (historyDataPoints.lastIndex > 100){
            historyDataPoints.removeAt(0)
        }
        val newHistoryDataPoint = HistoryDataPoint(
            temp = temp.toString(),
            locationString = locationString,
            time = time
        )
        historyDataPoints.add(newHistoryDataPoint)
        prefs.saveData(
            historyString = historyDataPoints
                .map{
                    it.joinToStringPound()
                }.joinToString(separator = "|")
        )
    }
    
    fun deleteHistory() = coroutineScope.launch{
        prefs.saveData(
            historyString = ""
        )
    }
    
    fun checkSecretOffset() = coroutineScope.launch{
        readPrefs()
        delay(100)
        if (storedData.secretOffsetParameters == ""){
            prefs.saveData(
                secretOffsetParameters = Random.nextLong().toString()
            )
        }
        else {
            try {
                storedData.secretOffsetParameters.toLong()
            }
            catch (e: Exception) {
                prefs.saveData(
                    secretOffsetParameters = Random.nextLong().toString()
                )
            }
        }
    }
    
}


fun String.toHistoryDataPointList() : List<HistoryDataPoint>{
    return this.split("|").map{
        val pointStringList = it.split("£")
        if (pointStringList.lastIndex>1)
            HistoryDataPoint(
                pointStringList[0],
                pointStringList[1],
                pointStringList[2]
            )
        else HistoryDataPoint(
            "","",""
        )
    }
}

data class HistoryDataPoint(
    val temp : String = "",
    val locationString : String = "",
    val time : String = ""
)

fun HistoryDataPoint.joinToStringPound() : String{
    return this.temp+"£"+this.locationString+"£"+this.time
}

data class DataStoreData(
    val distance : Float,
    val prevTemp : Double,
    val locationToUse : LocationData,
    val locationReal : LocationData,
    val locationSaved : LocationData,
    val measureTime : String,
    val measureSource : String,
    val updateInterval : String,
    val useSavedLocation : Boolean,
    val lastRun : String,
    val historyString : String,
    val secretOffsetParameters : String,
    val lastQueryString : String,
)

