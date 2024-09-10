package club.mobile.d21.smarthomesystem.util

import androidx.recyclerview.widget.DiffUtil
import club.mobile.d21.smarthomesystem.model.device.DeviceHistory

class DeviceHistoryDiffCallback : DiffUtil.ItemCallback<DeviceHistory>() {
    override fun areContentsTheSame(oldItem: DeviceHistory, newItem: DeviceHistory): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: DeviceHistory, newItem: DeviceHistory): Boolean {
        return oldItem === newItem
    }
}