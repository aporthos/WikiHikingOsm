package com.portes.wikihikingosm.core.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.osmdroid.util.GeoPoint

class GeoPointConverters {
    @TypeConverter
    fun stringToGeoPoint(data: String?): GeoPoint? = Gson().fromJson(data, GeoPoint::class.java)

    @TypeConverter
    fun geoPointToString(geoPoint: GeoPoint?): String? = Gson().toJson(geoPoint)
}