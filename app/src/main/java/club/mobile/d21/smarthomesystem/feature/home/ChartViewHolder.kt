package club.mobile.d21.smarthomesystem.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.core.util.Util.getColorBasedOnHumidityValue
import club.mobile.d21.smarthomesystem.core.util.Util.getColorBasedOnLightValue
import club.mobile.d21.smarthomesystem.core.util.Util.getColorBasedOnTemperatureValue
import club.mobile.d21.smarthomesystem.data.model.chart.Chart
import club.mobile.d21.smarthomesystem.databinding.ItemChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartViewHolder(private val binding: ItemChartBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(chart: Chart) {
        val entries = createEntries(chart)
        val colors = createColors(chart, entries)

        val dataSet = LineDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 12f
            setDrawValues(false)
            setDrawCircles(true)  // Vẽ chấm tròn tại mỗi điểm dữ liệu
            setDrawCircleHole(true)  // Vẽ lỗ tròn tại mỗi điểm dữ liệu
            circleRadius = 3f  // Kích thước của các chấm
            lineWidth = 2f  // Độ dày của đường nối giữa các chấm
        }

        binding.chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            setDrawGridBackground(false)

            setupChartAxes()
            invalidate()  // Refresh biểu đồ để hiển thị cập nhật
        }

        binding.label.text = chart.label
    }

    private fun createEntries(chart: Chart): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        for (i in chart.dataHistory.indices) {
            val value = when (chart.label) {
                "Temperature (°C)" -> chart.dataHistory[i].second.temperature
                "Humidity (%)" -> chart.dataHistory[i].second.humidity
                "Light (lux)" -> chart.dataHistory[i].second.light
                else -> 0f
            }
            entries.add(Entry((i + 1).toFloat(), value))
        }
        return entries
    }

    private fun createColors(chart: Chart, entries: List<Entry>): ArrayList<Int> {
        val colors = ArrayList<Int>()
        for (entry in entries) {
            val color = when (chart.label) {
                "Temperature (°C)" -> getColorBasedOnTemperatureValue(entry.y)
                "Humidity (%)" -> getColorBasedOnHumidityValue(entry.y)
                "Light (lux)" -> getColorBasedOnLightValue(entry.y)
                else -> android.R.color.transparent
            }
            colors.add(ContextCompat.getColor(itemView.context, color))
        }
        return colors
    }

    private fun setupChartAxes() {
        binding.chart.xAxis.apply {
            textSize = 12f
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
        }
        binding.chart.axisLeft.textSize = 12f
        binding.chart.axisRight.textSize = 12f
    }


    companion object {
        fun from(parent: ViewGroup): ChartViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemChartBinding.inflate(layoutInflater, parent, false)
            return ChartViewHolder(binding)
        }
    }
}
