package club.mobile.d21.smarthomesystem.model.sensor_data

data class CurrentData(
    val humidity: Float = 0f,
    val light: Float = 0f,
    val temperature: Float = 0f,
    val timestamp: Long = 0L
)