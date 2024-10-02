package club.mobile.d21.smarthomesystem.data.model

import club.mobile.d21.smarthomesystem.data.model.device.DeviceStatus
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData

data class FirebaseData(
    //val deviceHistory: Map<Long, DeviceHistory> = emptyMap(),
    val dataHistory: Map<Long, SensorData> = emptyMap(),
    val currentDevice: DeviceStatus = DeviceStatus(),
    val currentData: SensorData = SensorData()
)