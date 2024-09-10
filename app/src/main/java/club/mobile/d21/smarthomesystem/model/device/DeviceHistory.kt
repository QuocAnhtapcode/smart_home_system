package club.mobile.d21.smarthomesystem.model.device

import java.time.LocalDateTime

data class DeviceHistory(
    val device: Device,
    val time: LocalDateTime
)