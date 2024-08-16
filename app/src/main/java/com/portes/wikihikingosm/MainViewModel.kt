package com.portes.wikihikingosm

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

    private var _importHiking: ImportHiking? = null
    val importHiking: ImportHiking?
        get() = _importHiking

    fun setImport(inputStream: InputStream) {
        _importHiking = ImportHiking(gpx = parser.parse(inputStream))
    }
}