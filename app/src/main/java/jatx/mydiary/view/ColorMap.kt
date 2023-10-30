package jatx.mydiary.view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi

const val s = 0.4f
const val v = 0.8f

@ExperimentalGraphicsApi
private val colorMap = mapOf(
    1 to Color.hsv(0f, s, v),
    2 to Color.hsv(60f, s, v),
    3 to Color.hsv(120f, s, v),
    4 to Color.hsv(180f, s, v),
    5 to Color.hsv(240f, s, v),
    6 to Color.hsv(300f, s, v)
)

@ExperimentalGraphicsApi
val types = colorMap.keys.toList()

@ExperimentalGraphicsApi
fun getColorByType(type: Int) = colorMap[type] ?: Color.Black