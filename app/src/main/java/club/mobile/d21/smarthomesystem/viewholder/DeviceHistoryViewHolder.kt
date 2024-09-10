package club.mobile.d21.smarthomesystem.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.ItemDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.model.device.DeviceHistory
import java.time.format.DateTimeFormatter

class DeviceHistoryViewHolder(private val binding: ItemDeviceHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(deviceHistory: DeviceHistory) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formattedDateTime = deviceHistory.time.format(formatter)
        binding.name.text = deviceHistory.device.name
        binding.image.setImageResource(deviceHistory.device.image)
        if (deviceHistory.device.status) {
            binding.status.setImageResource(R.drawable.ic_on)
        } else {
            binding.status.setImageResource(R.drawable.ic_off)
        }
        binding.time.text = formattedDateTime
    }

    companion object {
        fun from(parent: ViewGroup): DeviceHistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDeviceHistoryBinding.inflate(layoutInflater, parent, false)
            return DeviceHistoryViewHolder(binding)
        }
    }
}