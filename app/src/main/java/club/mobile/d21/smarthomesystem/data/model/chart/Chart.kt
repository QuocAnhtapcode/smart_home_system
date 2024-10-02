package club.mobile.d21.smarthomesystem.data.model.chart

import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData

data class Chart(
    val label: String,
    val dataHistory : List<Pair<Long, SensorData>>
)
