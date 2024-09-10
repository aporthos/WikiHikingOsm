package com.portes.wikihikingosm.core.common

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun ImageView.setImage(context: Context, @DrawableRes drawable: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, drawable))
}
