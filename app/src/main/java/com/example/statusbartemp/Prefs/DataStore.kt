package com.example.statusbartemp.Prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultDistance
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultHistoryString
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultLastQueryString
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultLastRun
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultLocationDataString
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultMeasureSource
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultMeasureTime
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultPrevTemp
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultSecretOffsetParameters
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultUpdateInterval
import com.example.statusbartemp.LogicAndData.Constants.Companion.defaultUseSavedLocation
import com.example.statusbartemp.LogicAndData.SecureEncrypter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Singleton


val Context.AppPrefs by preferencesDataStore("StatusBarTempPrefs")

data class DataStoreDataRaw(
    val distance : Float,
    val prevTemp : Double,
    val measureTime : String,
    val measureSource : String,
    val updateInterval : String,
    val useSavedLocation : Boolean,
    val lastRun : String,
    val secretOffsetParameters: String,
    val historyString : String,
    val locationDataString : String,
    val lastQueryString : String,
    //val secretLocationOffset : String,
)


class AppPrefs(context: Context) : PrefsImpl {
    private val dataStore = context.AppPrefs
    
    private val secureEncrypter = SecureEncrypter()
    
    override suspend fun saveData(
        distance : Float?,
        prevTemp : Double?,
        measureTime : String?,
        measureSource : String?,
        updateInterval: String?,
        useSavedLocation : Boolean?,
        lastRun : String?,
        secretOffsetParameters : String?,
        historyString : String?,
        locationDataString : String?,
        lastQueryString : String?,
    ){
        val secretKey = secureEncrypter.getSecretKeyWithCheck()
        
        dataStore.edit { preferences ->
            prevTemp?.let{preferences[PREV_TEMP_KEY] = prevTemp}
            measureTime?.let{preferences[MEASURE_TIME_KEY] = measureTime}
            distance?.let{preferences[DISTANCE_KEY] = distance}
            measureSource?.let{preferences[MEASURE_SOURCE_KEY] = measureSource}
            updateInterval?.let{preferences[UPDATE_INTERVAL_KEY] = updateInterval}
            useSavedLocation?.let{preferences[LOCATION_OFF_KEY] = useSavedLocation}
            lastRun?.let{preferences[LAST_RUN_KEY] = lastRun}
            secretOffsetParameters?.let{preferences[SECRET_OFFSET_KEY] =
                secureEncrypter.encrypt(secretOffsetParameters, secretKey) ?:defaultSecretOffsetParameters
            }
            historyString?.let{preferences[HISTORY_STRING_KEY] =
                secureEncrypter.encrypt(historyString, secretKey) ?:defaultHistoryString
            }
            locationDataString?.let{preferences[LOCATION_DATA_STRING_KEY] =
                secureEncrypter.encrypt(locationDataString, secretKey) ?: defaultLocationDataString
            }
            lastQueryString?.let{ preferences[LASTQUERY_STRING_KEY] =
                secureEncrypter.encrypt(lastQueryString, secretKey) ?:defaultLastQueryString
            }
            
        }
    }

    override suspend fun saveUpdateInterval(updateInterval: String) {
        dataStore.edit { preferences ->
            preferences[UPDATE_INTERVAL_KEY] = updateInterval
        }
    }

    override suspend fun saveUseSavedLocation(newUseSavedLocation: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOCATION_OFF_KEY] = newUseSavedLocation
        }
    }

    override suspend fun saveLastRun(time: String) {
        dataStore.edit { preferences ->
            preferences[LAST_RUN_KEY] = time
        }
    }
    
    fun checkIfKeyIsValid(){
        secureEncrypter.isKeyValid()
    }
    
    
    override val dataFlow: Flow<DataStoreDataRaw> = dataStore.data
        .catch { exception ->
            throw exception
            /*
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
            */
        }.map { preferences ->
            val secretKey = secureEncrypter.getSecretKey()
            
            val distance = preferences[DISTANCE_KEY] ?: defaultDistance
            val prevTemp = preferences[PREV_TEMP_KEY] ?: defaultPrevTemp
            val measureTime = preferences[MEASURE_TIME_KEY] ?: defaultMeasureTime
            val measureSource = preferences[MEASURE_SOURCE_KEY] ?: defaultMeasureSource
            val updateInterval = preferences[UPDATE_INTERVAL_KEY] ?: defaultUpdateInterval
            val useSavedLocation = preferences[LOCATION_OFF_KEY] ?: defaultUseSavedLocation
            val lastRun = preferences[LAST_RUN_KEY] ?: defaultLastRun
            val secretOffset =
                secureEncrypter
                    .decrypt(preferences[SECRET_OFFSET_KEY], secretKey) ?: defaultSecretOffsetParameters
            val historyString =
                secureEncrypter
                    .decrypt(preferences[HISTORY_STRING_KEY], secretKey) ?:defaultHistoryString
            val locationString =
                secureEncrypter
                    .decrypt(preferences[LOCATION_DATA_STRING_KEY], secretKey) ?: defaultLocationDataString
            val lastQueryString =
                secureEncrypter
                    .decrypt(preferences[LASTQUERY_STRING_KEY], secretKey) ?: defaultLastQueryString
            
            secretKey ?:{
                secureEncrypter.generateSecretKey()
            }
            
            DataStoreDataRaw(
                distance = distance,
                prevTemp = prevTemp,
                measureTime = measureTime,
                measureSource = measureSource,
                updateInterval = updateInterval,
                useSavedLocation = useSavedLocation,
                lastRun = lastRun,
                secretOffsetParameters = secretOffset,
                historyString = historyString,
                locationDataString = locationString,
                lastQueryString = lastQueryString,
            )
        }
    companion object PreferencesKeys {
        val DISTANCE_KEY = floatPreferencesKey("distance")
        val PREV_TEMP_KEY = doublePreferencesKey("prev_temp")
        val MEASURE_TIME_KEY = stringPreferencesKey("measure_time")
        val MEASURE_SOURCE_KEY = stringPreferencesKey("measure_source")
        val UPDATE_INTERVAL_KEY = stringPreferencesKey("update_interval")
        val LOCATION_OFF_KEY = booleanPreferencesKey("location_off")
        val LAST_RUN_KEY = stringPreferencesKey("lastrun")
        val SECRET_OFFSET_KEY = stringPreferencesKey("secret_offset_parameters")
        val HISTORY_STRING_KEY = stringPreferencesKey("history_string")
        val LOCATION_DATA_STRING_KEY = stringPreferencesKey("location_data_string")
        val LASTQUERY_STRING_KEY = stringPreferencesKey("last_query_string")
    }



}

@Singleton
interface PrefsImpl {
    
    suspend fun saveData(
        distance : Float? = null,
        prevTemp : Double? = null,
        measureTime : String? = null,
        measureSource : String? = null,
        updateInterval: String? = null,
        useSavedLocation : Boolean? = null,
        lastRun : String? = null,
        secretOffsetParameters: String? = null,
        historyString : String? = null,
        locationDataString : String? = null,
        lastQueryString : String? = null,
    )


    suspend fun saveUpdateInterval(updateInterval : String)

    suspend fun saveUseSavedLocation(useSavedLocation : Boolean)

    suspend fun saveLastRun(time : String)

    val dataFlow : Flow<DataStoreDataRaw>

}





/*
@Singleton
class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    suspend fun writeIntegerValue(value: Int) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.INTEGER_KEY] = value
        }
    }

    suspend fun writeStringValue(value: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.STRING_KEY] = value
        }
    }

    val integerValueFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DataStoreKeys.INTEGER_KEY] ?: 0
        }
}

object DataStoreKeys {
    val INTEGER_KEY = intPreferencesKey("integer_key")
    val STRING_KEY = stringPreferencesKey("string_key")
}
*/