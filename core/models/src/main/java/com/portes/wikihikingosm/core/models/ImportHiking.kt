package com.portes.wikihikingosm.core.models

import io.ticofab.androidgpxparser.parser.domain.Gpx

data class ImportHiking(
    var gpx: Gpx? = null,
)