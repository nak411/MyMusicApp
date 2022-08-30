package com.naveed.mymusicapp.ext

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.android.material.color.MaterialColors
import com.naveed.mymusicapp.R

@ColorInt
fun Context.getPrimaryColor(): Int {
    return MaterialColors.getColor(
        this,
        androidx.appcompat.R.attr.colorPrimary,
        0
    )
}

@ColorInt
fun Context.getTextColor(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnSurface,
      0
    )
}