package club.mobile.d21.smarthomesystem.data

import java.sql.Date
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Data {
    var isLightOn = false
    var isAirConditionerOn = false
    var isAirPurifierOn = false
    var isFanOn = false
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
    val airPollutionLevel = Chart(
        "Air pollution level (AQI)",
        mutableListOf(0f, 50f, 167f, 265.5f, 80.6f, 190f, 96f, 298f, 37.7f, 155f, 60f)
    )
    val noisePollutionLevel = Chart(
        "Noise pollution level (dB)",
        mutableListOf(0f, 25.5f, 40.5f, 27.2f, 24.1f, 30f, 32f, 29f, 26.5f, 21f, 18f)
    )
    val history = mutableListOf(
        History("Light",true, LocalDateTime.of(2024, 8, 28, 15, 30, 45)),
        History("Fan",false, LocalDateTime.of(2024, 8, 28, 15, 30, 50))
    )
}