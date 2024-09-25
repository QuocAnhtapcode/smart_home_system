package club.mobile.d21.smarthomesystem.util

import androidx.recyclerview.widget.DiffUtil
import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData

class DataHistoryDiffCallback : DiffUtil.ItemCallback<Pair<Long, SensorData>>() {
    override fun areContentsTheSame(
        oldItem: Pair<Long, SensorData>,
        newItem: Pair<Long, SensorData>
    ): Boolean {
        return oldItem.second == newItem.second
    }

    override fun areItemsTheSame(
        oldItem: Pair<Long, SensorData>,
        newItem: Pair<Long, SensorData>
    ): Boolean {
        return oldItem.first == newItem.first
    }
}