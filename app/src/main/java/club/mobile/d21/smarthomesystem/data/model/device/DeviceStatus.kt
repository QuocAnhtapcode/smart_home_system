package club.mobile.d21.smarthomesystem.data.model.device

data class DeviceStatus(
    val ac: Boolean = false,
    val light: Boolean = false,
    val tv: Boolean = false,
    val warning: Boolean = false
)