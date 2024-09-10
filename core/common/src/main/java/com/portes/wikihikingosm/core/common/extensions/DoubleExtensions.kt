package com.portes.wikihikingosm.core.common.extensions

fun Double.toKm() = "${(this * 0.001).toFormat()} km"

fun Double.toFormat() =
    String.format("%.2f", this)

fun Double.toMeters() = "${String.format("%,.0f", this)} m"