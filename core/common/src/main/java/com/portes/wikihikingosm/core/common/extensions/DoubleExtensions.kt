package com.portes.wikihikingosm.core.common.extensions

fun Double.toKm() = (this * 0.001).toFormat()

fun Double.toFormat() =
    String.format("%.2f", this)