package club.mobile.d21.smarthomesystem.feature.detail

import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.databinding.ItemDataHistoryBinding
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData
import club.mobile.d21.smarthomesystem.core.util.Util.formatTimestamp

class DataHistoryViewHolder(private val binding: ItemDataHistoryBinding)
    :RecyclerView.ViewHolder(binding.root){
        fun bind(dataHistory: Pair<Long, SensorData>){
            binding.dateTimeText.text = formatTimestamp(dataHistory.first)
            binding.temperatureText.text = dataHistory.second.temperature.toString()
            binding.humidityText.text = dataHistory.second.humidity.toString()
            binding.lightText.text = dataHistory.second.light.toString()
        }
}