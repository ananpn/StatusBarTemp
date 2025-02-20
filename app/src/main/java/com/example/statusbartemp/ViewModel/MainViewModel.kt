package com.weeklist.screens.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statusbartemp.Prefs.PrefsImpl
import com.example.statusbartemp.UpdateWorker.WorkerDependency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: PrefsImpl,
    private val workerDependency: WorkerDependency
) : ViewModel() {

    //Temperature Updater *************************************************************************

    fun initializeTemperatureUpdater() {
        workerDependency.initializeTemperatureUpdater()
    }

    fun stopUpdate() {
        workerDependency.stopUpdate()
    }


    //Flows ****************************************************************************
    val isUpdating = workerDependency._isUpdating
    val dataFlow = workerDependency.dataFlow
    val statusMessage = workerDependency.statusMessageFlow



    //Global variables *********************************************************************


    //Preferences *********************************************************************
    
    fun saveData(
        distance : Float? = null,
        prevTemp : Double? = null,
        measureTime : String? = null,
        measureSource : String? = null,
        updateInterval: String? = null,
        useSavedLocation : Boolean? = null,
        lastRun : String? = null,
        secretOffset: String? = null,
        historyString : String? = null,
        locationDataString : String? = null,
    )= viewModelScope.launch {
        prefs.saveData(
            distance = distance,
            prevTemp = prevTemp,
            measureTime = measureTime,
            measureSource = measureSource,
            updateInterval = updateInterval,
            useSavedLocation = useSavedLocation,
            lastRun = lastRun,
            secretOffsetParameters = secretOffset,
            historyString = historyString,
            locationDataString = locationDataString,
        )
    }

    fun setStatusMessage(new : String) = viewModelScope.launch{
        workerDependency.setStatusMessage(new)
    }

    fun saveLocation(
        title : String,
        lat : Double,
        lon : Double
    ) {
        workerDependency.saveManualLocation(
            title = title, lat = lat, lon = lon
        )
    }
    
    fun applyLocation(
        title : String,
        lat : Double,
        lon : Double
    ) {
        workerDependency.applyManualLocation(
            title = title, lat = lat, lon = lon
        )
    }

    suspend fun getOfflineLocation() : LocationData? {
        val data = dataFlow.firstOrNull()
        if (data != null)
            return data.locationSaved
        else return null
    }

    suspend fun getCurrentLocation() : LocationData? {
        val data = dataFlow.firstOrNull()
        if (data != null)
            return data.locationToUse
        else return null

    }
    
    suspend fun obtainPlaceName(lat : Double, lon : Double) : String {
        return workerDependency.obtainPlaceName(lat, lon)
    }
    
    fun deleteHistory(){
        workerDependency.deleteHistory()
    }

    //Dialog State ********************************************************

    var openLocationInputDialog by mutableStateOf(false)
    var openSimpleSettingsDialog by mutableStateOf(false)
    var openTemperatureHistoryDialog by mutableStateOf(false)
    
    
    
    
    fun plsOpenLocationInputDialog() {
        openLocationInputDialog = true
    }

    fun closeLocationInputDialog() {
        openLocationInputDialog = false
    }



    fun plsOpenSimpleSettingsDialog() {
        openSimpleSettingsDialog = true
    }

    fun closeSimpleSettingsDialog() {
        openSimpleSettingsDialog = false
    }
    
    fun plsOpenTemperatureHistoryDialog(new : Boolean = true){
        openTemperatureHistoryDialog = new
    }

    suspend fun checkBackGroundProcessState() : Boolean {
        return workerDependency.checkBackGroundProcessState()
    }

    fun initCheckWorker(start : Boolean){
        workerDependency.initCheckWorker(start)
    }
    
    fun checkSecretOffset(){
        workerDependency.checkSecretOffset()
    }
    
}

data class LocationData(
    val latitude : Double,
    val longitude : Double,
    val placeName : String
)
