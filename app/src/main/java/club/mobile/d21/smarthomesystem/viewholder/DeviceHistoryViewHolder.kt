package club.mobile.d21.smarthomesystem.viewholder

import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.ItemDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.model.device.DeviceHistory

class DeviceHistoryViewHolder(private val binding: ItemDeviceHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(deviceHistory: Pair<Long,DeviceHistory>) {
        binding.name.text = deviceHistory.second.name
        when(deviceHistory.second.name){
            "light"-> binding.image.setImageResource(R.drawable.ic_big_light)
            "ac"-> binding.image.setImageResource(R.drawable.ic_big_ac)
            "tv"-> binding.image.setImageResource(R.drawable.ic_big_tv)
        }
        if (deviceHistory.second.status) {
            binding.status.setImageResource(R.drawable.ic_on)
        } else {
            binding.status.setImageResource(R.drawable.ic_off)
        }
        binding.time.text = formatTimestamp(deviceHistory.first)
    }
    private fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return format.format(date)
    }
}