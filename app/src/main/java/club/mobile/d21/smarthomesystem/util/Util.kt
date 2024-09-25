package club.mobile.d21.smarthomesystem.util

import club.mobile.d21.smarthomesystem.R

object Util {
    var currentPage = 1
    var currentDataPage = 1
    fun getColorBasedOnTemperatureValue(value: Float): Int {
        return when {
            value < 25 -> R.color.green
            value < 30 -> R.color.yellow
            value < 35 -> R.color.orange
            value < 40 -> R.color.red
            value < 45 -> R.color.purple
            else -> R.color.dark_purple
        }
    }
    fun getColorBasedOnHumidityValue(value: Float): Int {
        return when {
            value < 50 -> R.color.blue_1
            value < 60 -> R.color.blue_2
            value < 70 -> R.color.blue_3
            value < 80 -> R.color.blue_4
            else -> R.color.blue_5
        }
    }
    fun getColorBasedOnLightValue(value: Float): Int {
        return when {
            value < 500 -> R.color.dark_blue
            value < 1000 -> R.color.blue
            value < 2000 -> R.color.light_blue
            value < 3000 -> R.color.light_yellow
            value < 4000 -> R.color.yellow
            else -> R.color.orange
        }
    }
    fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss", java.util.Locale.getDefault())
        return format.format(date)
    }

}