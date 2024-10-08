package club.mobile.d21.smarthomesystem.feature.device_history

import androidx.recyclerview.widget.DiffUtil
import club.mobile.d21.smarthomesystem.data.model.device.DeviceHistory

class DeviceHistoryDiffCallback : DiffUtil.ItemCallback<Pair<Long, DeviceHistory>>() {
    override fun areContentsTheSame(oldItem: Pair<Long, DeviceHistory>, newItem: Pair<Long, DeviceHistory>): Boolean {
        return oldItem.second == newItem.second
    }

    override fun areItemsTheSame(oldItem: Pair<Long, DeviceHistory>, newItem: Pair<Long, DeviceHistory>): Boolean {
        return oldItem.first == newItem.first
    }
}