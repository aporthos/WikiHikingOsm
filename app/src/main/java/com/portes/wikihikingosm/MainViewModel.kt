package com.portes.wikihikingosm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.portes.wikihikingosm.core.models.ImportHiking
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ticofab.androidgpxparser.parser.GPXParser
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val parser: GPXParser
) : ViewModel() {

    var importHiking by mutableStateOf(ImportHiking())
        private set

    fun setImport(inputStream: InputStream) {
        importHiking = importHiking.copy(gpx = parser.parse(inputStream))
    }
}