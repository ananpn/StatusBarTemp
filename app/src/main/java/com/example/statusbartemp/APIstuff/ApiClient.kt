package com.example.statusbartemp.APIstuff

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class WeatherApi() {
    private val BASE_URL = "https://opendata.fmi.fi"

    private val retrofit: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            //.addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            //.addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
    
    suspend fun makeWFSQueryToFMI(
        //place : String? = null,
        //wmo : Int,
        storedquery_id : String,
        latlon : List<String>? = null,
        parameters : String = "temperature",
        bbox : String? = null,
        timestep :String? = null,
        starttime : String? = null,
        endtime : String? = null,
    ): String? {
        return try {
            retrofit.makeWFSQueryToFMI(
                //place = place,
                storedquery_id = storedquery_id,
                parameters = parameters,
                latlon = latlon,
                bbox = bbox,
                timestep = timestep,
                starttime = starttime,
                endtime = endtime,
            )
        }
        catch (e: Exception) {
            //Log.v("apiclient getTemp", "$e")
            return null
        }
    }
}