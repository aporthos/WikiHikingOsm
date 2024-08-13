package com.portes.wikihikingosm.feature.hikings

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Named

class HikingRoutePref @Inject constructor(
    @Named(HIKING_PREFERENCES) private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val HIKING_PREFERENCES = "hikingPreferences"
        private const val START_HIKING = "startHiking"
    }

    fun startHiking(idHike: Long) {
        sharedPreferences.edit {
            putLong(START_HIKING, idHike)
        }
    }

    fun isStartHiking(): Long = sharedPreferences.getLong(START_HIKING, 0)
}