package club.mobile.d21.smarthomesystem.data

import java.time.LocalDateTime

data class History(
    val device: String,
    val status: Boolean,
    val time: LocalDateTime
)