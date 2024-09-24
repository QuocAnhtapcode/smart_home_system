package club.mobile.d21.smarthomesystem.model

import club.mobile.d21.smarthomesystem.model.device.DeviceHistory
import club.mobile.d21.smarthomesystem.model.device.DeviceStatus
import club.mobile.d21.smarthomesystem.model.sensor_data.CurrentData
import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData

data class FirebaseData(
    val deviceHistory: Map<String, DeviceHistory> = emptyMap(),
    val dataHistory: Map<String, SensorData> = emptyMap(),
    val currentDevice: DeviceStatus = DeviceStatus(),
    val currentData: CurrentData = CurrentData()
)