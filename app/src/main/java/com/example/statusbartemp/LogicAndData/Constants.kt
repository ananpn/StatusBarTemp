package com.example.statusbartemp.LogicAndData

import android.Manifest
import android.os.Build
import com.example.statusbartemp.LogicAndData.LogicConstants.Companion.WINDOW_ALARM
import com.example.statusbartemp.UpdateWorker.DataStoreData
import com.weeklist.screens.utils.LocationData

class Constants {
    companion object {
        val basicPermissions =  //API 31
            if (Build.VERSION.SDK_INT >= 33){
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.USE_EXACT_ALARM,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                )
            }
            else if (Build.VERSION.SDK_INT >= 31){
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                )
            }
            else arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
            )
        val locationPermissions =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        val backGroundLocationPermissions = if (Build.VERSION.SDK_INT >= 29){
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        }
        else {
            arrayOf()
        }

        val badResponses = listOf(
            "no response",
            "invalid response"
        )
        
        val stbDivider0 = "<>>"
        val stbDivider1 = "%£"
        
        
        val latDegreesOf100Meters = 0.0009
        val lonDegreesOf100Meters = 0.00195

        val defaultDistance : Float = 0f
        val defaultPrevTemp : Double = 0.0
        val defaultLatitude : Double = 0.0
        val defaultLongitude : Double = 0.0
        val defaultLocationData : LocationData = LocationData(
            latitude = 0.0,
            longitude = 0.0,
            placeName = ""
        )
        val defaultMeasureTime : String = ""
        val defaultMeasureSource : String = "Harmonie"
        val defaultUpdateInterval : String = "30 minutes"
        val defaultLocationString : String = defaultLocationData.toStringWith(stbDivider1)
        val defaultLocationDataString : String =
            listOf(
                defaultLocationString,
                defaultLocationString,
                defaultLocationString
            ).joinToString(stbDivider0)
        val defaultLastQueryString : String = ""
        val defaultPlaceName : String = ""
        val defaultStatusMessage : String = ""
        val defaultUseSavedLocation : Boolean = false
        val defaultLastRun : String = "Not run"
        val defaultAlarmType : String = WINDOW_ALARM
        val defaultAccuracyMeters : Int = 1000
        val defaultHistoryString : String = ""
        val defaultSecretOffsetParameters : String = ""
        
        
        
        val harmonieSurfacePointSimpleQuery = "fmi::forecast::harmonie::surface::point::simple"
        //val observationsWeatherSimpleQuery = "fmi::observations::weather::simple"
        val ecmwfForeCastSurfacePointSimpleQuery = "ecmwf::forecast::surface::point::simple"
        val fmiForecastEditedPointSimpleQuery = "fmi::forecast::edited::weather::scandinavia::point::simple"
        val mepsSurfacePointSimpleQuery = "fmi::forecast::meps::surface::point::simple"
        
        val storedQueryIdList = listOf(
            fmiForecastEditedPointSimpleQuery,
            ecmwfForeCastSurfacePointSimpleQuery,
            harmonieSurfacePointSimpleQuery,
            mepsSurfacePointSimpleQuery,
            //observationsWeatherSimpleQuery,
        )
        
        
        
        val emptyDataStoreData = DataStoreData(
            distance = defaultDistance,
            prevTemp = defaultPrevTemp,
            locationToUse = defaultLocationData,
            locationReal = defaultLocationData,
            locationSaved = defaultLocationData,
            measureTime = defaultMeasureTime,
            measureSource = defaultMeasureSource,
            updateInterval = defaultUpdateInterval,
            useSavedLocation = defaultUseSavedLocation,
            lastRun = defaultLastRun,
            historyString = defaultHistoryString,
            secretOffsetParameters = defaultSecretOffsetParameters,
            lastQueryString = defaultLastQueryString
        )
        
        
        

        
        /*
        val currentYear=TimeFunctions.getTimeNow("yyyy")
        val tomorrowYear= LocalDateTime.now().plusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy"))
*/
        
        val ISO_TIME_FORMAT = "uuuu-MM-dd'T'HH:mm:ss'Z'"


        //Regex Patterns ****************************************************************

        val onlyNumbersPattern = Regex("^\\d+\$")
        val decimalPattern = Regex("^\\d+\\.?\\d*\$")
        val betterDecimalPattern = Regex("/^[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))\$/")
        val noSpecialCharPattern = Regex("^\\d+\\.?\\d*\$")
        //val startSpacePattern = Regex("^\\s+")
        val whiteSpaceAnyWherePattern = Regex("\\s+")
        val spaceAnyWherePattern = Regex("\" \"+")
        val newLinePattern = Regex("\\v")
        val textInputNegativePattern = Regex("^\\v+\$")
        //val decimalPattern = Regex("^\\d+\\.?\\d*\$")
        
    }
}