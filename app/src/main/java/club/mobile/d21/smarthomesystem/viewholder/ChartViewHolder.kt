package club.mobile.d21.smarthomesystem.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.databinding.ItemChartBinding
import club.mobile.d21.smarthomesystem.model.chart.Chart
import club.mobile.d21.smarthomesystem.util.Util.getColorBasedOnHumidityValue
import club.mobile.d21.smarthomesystem.util.Util.getColorBasedOnLightValue
import club.mobile.d21.smarthomesystem.util.Util.getColorBasedOnTemperatureValue
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.highlight.Highlight

class ChartViewHolder(private val binding: ItemChartBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(chart: Chart) {
        val entries = createEntries(chart)
        val colors = createColors(chart, entries)

        val dataSet = BarDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 12f
            setDrawValues(false)
        }

        binding.chart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            setDrawValueAboveBar(true)

            setupChartAxes()
            setupChartListeners(dataSet)
            invalidate()
        }

        binding.label.text = chart.label
    }

    private fun createEntries(chart: Chart): ArrayList<BarEntry> {
        val entries = ArrayList<BarEntry>()
        for (i in chart.dataHistory.indices) {
            val value = when (chart.label) {
                "Temperature (°C)" -> chart.dataHistory[i].second.temperature
                "Humidity (%)" -> chart.dataHistory[i].second.humidity
                "Light (lux)" -> chart.dataHistory[i].second.light
                else -> 0f
            }
            entries.add(BarEntry((i + 1).toFloat(), value))
        }
        return entries
    }

    private fun createColors(chart: Chart, entries: List<BarEntry>): ArrayList<Int> {
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

    private fun setupChartListeners(dataSet: BarDataSet) {
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                dataSet.setDrawValues(true)
                binding.chart.invalidate()
            }

            override fun onNothingSelected() {
                dataSet.setDrawValues(false)
                binding.chart.invalidate()
            }
        })
    }

    companion object {
        fun from(parent: ViewGroup): ChartViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemChartBinding.inflate(layoutInflater, parent, false)
            return ChartViewHolder(binding)
        }
    }
}
