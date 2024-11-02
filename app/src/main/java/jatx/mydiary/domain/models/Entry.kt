package jatx.mydiary.domain.models

import java.text.SimpleDateFormat
import java.util.*

data class Entry(
    val id: Long? = null,
    val type: Int,
    val time: Long
)

fun Entry.formatTimeList(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
    val date = Date()
    date.time = time
    return sdf.format(date)
}

fun Entry.formatTimeTop(): String {
    val dt = System.currentTimeMillis() - time
    val secTotal = dt / 1000L
    val sec = secTotal % 60
    val minTotal = secTotal / 60
    val min = minTotal % 60
    val hoursTotal = minTotal / 60
    val hours = hoursTotal % 24
    val days = hoursTotal / 24
    return when {
        days > 0 -> {
            "$days д. $hours ч. назад"
        }
        hours > 0 -> {
            "$hours ч. $min м. назад"
        }
        else -> {
            "$min м. $sec с. назад"
        }
    }
}