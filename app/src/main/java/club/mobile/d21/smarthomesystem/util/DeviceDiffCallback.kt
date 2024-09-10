package club.mobile.d21.smarthomesystem.util

import androidx.recyclerview.widget.DiffUtil
import club.mobile.d21.smarthomesystem.model.device.Device

class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}