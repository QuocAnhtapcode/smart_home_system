package club.mobile.d21.smarthomesystem.feature.device_history

import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.ItemDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.data.model.device.DeviceHistory
import club.mobile.d21.smarthomesystem.core.util.Util.formatTimestamp

class DeviceHistoryViewHolder(private val binding: ItemDeviceHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(deviceHistory: Pair<Long, DeviceHistory>) {

        when (deviceHistory.second.name) {
            "light" -> {
                binding.name.text = "Light"
                binding.image.setImageResource(R.drawable.ic_small_light)
            }

            "ac" -> {
                binding.name.text = "Air Conditioner"
                binding.image.setImageResource(R.drawable.ic_small_ac)
            }

            "tv" -> {
                binding.name.text = "Television"
                binding.image.setImageResource(R.drawable.ic_small_tv)
            }
        }
        if (deviceHistory.second.status) {
            binding.status.setImageResource(R.drawable.ic_on)
        } else {
            binding.status.setImageResource(R.drawable.ic_off)
        }
        binding.time.text = formatTimestamp(deviceHistory.first)
    }

}