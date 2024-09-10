package club.mobile.d21.smarthomesystem.model.chart

data class Chart(
    val label: String,
    val dataList: MutableList<Float> = mutableListOf()
)
