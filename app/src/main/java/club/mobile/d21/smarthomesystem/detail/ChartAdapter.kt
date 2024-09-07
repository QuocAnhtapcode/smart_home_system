package club.mobile.d21.smarthomesystem.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.data.Chart
import club.mobile.d21.smarthomesystem.data.Data
import club.mobile.d21.smarthomesystem.databinding.ItemChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class ChartAdapter : ListAdapter<Chart, ChartAdapter.ViewHolder>(ChartDiffCallBack()) {
    inner class ViewHolder(private val binding: ItemChartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chart: Chart) {
            val barChart = binding.chart

            val entries = ArrayList<BarEntry>()
            for (i in 1 until chart.dataList.size) {
                entries.add(BarEntry(i.toFloat(), chart.dataList[i]));
            }
            val dataSet = BarDataSet(entries, "")
            dataSet.valueTextSize = 12f
            val barData = BarData(dataSet)
            barChart.data = barData
            barChart.description.isEnabled = false

            val colors = ArrayList<Int>()
            when (chart.label) {
                "Temperature (°C)" -> {
                    for (entry in entries) {
                        colors.add(ContextCompat.getColor(itemView.context,
                            getColorBasedOnTemperatureValue(entry.y)
                        ))
                    }
                    dataSet.colors = colors
                }
                "Humidity (%)" -> {
                    for (entry in entries) {
                        colors.add(ContextCompat.getColor(itemView.context,
                            getColorBasedOnHumidityValue(entry.y)
                        ))
                    }
                    dataSet.colors = colors
                }
            }
            val xAxis = barChart.xAxis
            xAxis.textSize = 16f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textSize = 16f
            xAxis.granularity = 1f
            xAxis.labelCount = Data.temperatureChart.dataList.size

            val leftAxis = barChart.axisLeft
            leftAxis.textSize = 16f
            val rightAxis = barChart.axisRight
            rightAxis.textSize = 16f

            barChart.isHighlightPerTapEnabled = false
            barChart.isHighlightPerDragEnabled = false
            barChart.invalidate()

            binding.label.text = chart.label
        }
    }

    private fun getColorBasedOnTemperatureValue(value: Float): Int {
        return when {
            value < 25 -> R.color.green
            value < 30 -> R.color.yellow
            value < 35 -> R.color.orange
            value < 40 -> R.color.red
            value < 45 -> R.color.purple
            else -> R.color.dark_purple
        }
    }

    private fun getColorBasedOnHumidityValue(value: Float): Int {
        return when {
            value < 35 -> R.color.blue_1
            value < 50 -> R.color.blue_2
            value < 65 -> R.color.blue_3
            value < 85 -> R.color.blue_4
            else -> R.color.blue_5
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChartBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class ChartDiffCallBack : DiffUtil.ItemCallback<Chart>() {
        override fun areContentsTheSame(oldItem: Chart, newItem: Chart): Boolean {
            return oldItem.label == newItem.label
        }
        override fun areItemsTheSame(oldItem: Chart, newItem: Chart): Boolean {
            return oldItem === newItem
        }
    }
}