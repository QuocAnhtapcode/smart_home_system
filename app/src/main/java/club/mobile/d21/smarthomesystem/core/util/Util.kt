package club.mobile.d21.smarthomesystem.core.util

import club.mobile.d21.smarthomesystem.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Util {
    var isAboveThreshold = false
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }
    fun getColorBasedOnTemperatureValue(value: Float): Int {
        return when {
            value < 25 -> R.color.green
            value < 27 -> R.color.yellow
            value < 29 -> R.color.orange
            value < 31 -> R.color.red
            value < 33 -> R.color.purple
            else -> R.color.dark_purple
        }
    }
    fun getColorBasedOnHumidityValue(value: Float): Int {
        return when {
            value < 60 -> R.color.blue_1
            value < 65 -> R.color.blue_2
            value < 70 -> R.color.blue_3
            value < 75 -> R.color.blue_4
            else -> R.color.blue_5
        }
    }
    fun getColorBasedOnLightValue(value: Float): Int {
        return when {
            value < 2000 -> R.color.dark_blue
            value < 2200 -> R.color.blue
            value < 2400 -> R.color.light_blue
            value < 2600 -> R.color.light_yellow
            value < 2800 -> R.color.yellow
            else -> R.color.orange
        }
    }
    fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss", java.util.Locale.getDefault())
        return format.format(date)
    }

}