package club.mobile.d21.smarthomesystem.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
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
        if (chart.label == "Wind and air") {
            bindAllChart(chart)
        } else {
            bindSingleChart(chart)
        }
    }

    private fun bindSingleChart(chart: Chart) {
        val entries = createEntries(chart)
        val colors = createColors(chart, entries)

        val dataSet = LineDataSet(entries, chart.label).apply {
            this.colors = colors
            valueTextSize = 12f
            setDrawValues(false)
            setDrawCircles(true)
            setDrawCircleHole(true)
            circleRadius = 3f
            lineWidth = 2f
        }

        binding.chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            setDrawGridBackground(false)

            setupChartAxes()
            invalidate()
        }

        binding.label.text = chart.label
    }

    private fun bindAllChart(chart: Chart) {
        val windEntries = createAllEntries(chart, "Wind")
        val airEntries = createAllEntries(chart, "Air")
        val windDataSet = LineDataSet(windEntries, "Wind").apply {
            color = ContextCompat.getColor(itemView.context, R.color.red)
            setDrawCircles(true)
            setDrawCircleHole(true)
            circleRadius = 3f
            lineWidth = 2f
        }
        val airDataSet = LineDataSet(airEntries, "Air").apply {
            color = ContextCompat.getColor(itemView.context, R.color.green)
            setDrawCircles(true)
            setDrawCircleHole(true)
            circleRadius = 3f
            lineWidth = 2f
        }
        val lineData = LineData(windDataSet,airDataSet)

        binding.chart.apply {
            data = lineData
            description.isEnabled = false
            setDrawGridBackground(false)

            setupChartAxes()
            invalidate()
        }

        binding.label.text = "All"
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

    private fun createAllEntries(chart: Chart, type: String): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        for (i in chart.dataHistory.indices) {
            val value = when (type) {
                "Temperature" -> chart.dataHistory[i].second.temperature
                "Humidity" -> chart.dataHistory[i].second.humidity
                "Light" -> chart.dataHistory[i].second.light/100
                "Wind" -> chart.dataHistory[i].second.wind.toFloat()
                "Air" -> chart.dataHistory[i].second.air.toFloat()
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
                "Wind" -> getColorBasedOnTemperatureValue(entry.y)
                "Air" -> getColorBasedOnTemperatureValue(entry.y)
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
