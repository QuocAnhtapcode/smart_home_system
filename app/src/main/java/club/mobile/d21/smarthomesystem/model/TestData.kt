package club.mobile.d21.smarthomesystem.model

import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.model.chart.Chart
import club.mobile.d21.smarthomesystem.model.device.Device
import club.mobile.d21.smarthomesystem.model.device.DeviceHistory
import java.time.LocalDateTime

object TestData {
    val ac = Device("AC", R.drawable.ic_big_ac, true)
    val tv = Device("TV", R.drawable.ic_big_tv, true)
    val light = Device("Light", R.drawable.ic_big_light, true)
    val deviceHistoryList: MutableList<DeviceHistory> = mutableListOf()
    val temperatureChart = Chart(
        "Temperature (°C)",
        mutableListOf(0f, 25.5f, 40.5f, 27.2f, 24.1f, 30f, 32f, 29f, 26.5f, 21f, 18f)
    )
    val humidityChart = Chart(
        "Humidity (%)",
        mutableListOf(0f, 50f, 67f, 65.5f, 80.6f, 90f, 96f, 98f, 70.7f, 55f, 60f)
    )
    val lightChart = Chart(
        "Light ()",
        mutableListOf(0f, 25.5f, 40.5f, 27.2f, 24.1f, 30f, 32f, 29f, 26.5f, 21f, 18f)
    )
    val itemHome: List<Any> = listOf(ac, tv, light, temperatureChart, humidityChart, lightChart)

}