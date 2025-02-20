package com.example.statusbartemp.APIstuff

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    
    @GET("wfs")
    suspend fun makeWFSQueryToFMI(
        @Query("service") service: String = "WFS",
        @Query("version") version: String = "2.0.0",
        @Query("request") request: String = "getFeature",
        @Query("storedquery_id") storedquery_id : String,
        @Query("parameters") parameters: String,
        @Query("latlon") latlon: List<String>?,
        @Query("bbox") bbox: String?,
        @Query("timestep") timestep : String?,
        @Query("starttime") starttime: String?,
        @Query("endtime") endtime: String?
    
    ): String
}