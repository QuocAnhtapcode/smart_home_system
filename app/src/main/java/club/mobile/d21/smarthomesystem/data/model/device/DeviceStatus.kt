package club.mobile.d21.smarthomesystem.data.model.device

data class DeviceStatus(
    var ac: Boolean = false,
    var light: Boolean = false,
    var tv: Boolean = false,
    var warning: Boolean = false
)