package club.mobile.d21.smarthomesystem.model.chart

import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData

data class Chart(
    val label: String,
    val dataHistory : List<Pair<Long, SensorData>>
)
