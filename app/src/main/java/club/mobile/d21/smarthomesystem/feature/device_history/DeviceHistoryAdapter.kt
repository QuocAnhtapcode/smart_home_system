package club.mobile.d21.smarthomesystem.feature.device_history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import club.mobile.d21.smarthomesystem.databinding.ItemDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.data.model.device.DeviceHistory

class DeviceHistoryAdapter
    : ListAdapter<Pair<Long, DeviceHistory>, DeviceHistoryViewHolder>(DeviceHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHistoryViewHolder {
        val binding =
            ItemDeviceHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}