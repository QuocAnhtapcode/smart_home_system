package club.mobile.d21.smarthomesystem.data

data class Chart(
    val label: String,
    val dataList: MutableList<Float> = mutableListOf()
)
