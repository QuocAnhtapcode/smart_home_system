package club.mobile.d21.smarthomesystem.viewholder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.ItemDeviceBinding
import club.mobile.d21.smarthomesystem.model.TestData
import club.mobile.d21.smarthomesystem.model.device.Device
import club.mobile.d21.smarthomesystem.model.device.DeviceHistory
import java.time.LocalDateTime

class DeviceViewHolder(private val binding: ItemDeviceBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(device: Device) {
        binding.deviceText.text = device.name
        binding.deviceImage.setImageResource(device.image)
        updateStatusImage(device.status)

        binding.deviceStatus.setOnClickListener {
            device.status = !device.status
            TestData.deviceHistoryList.add(DeviceHistory(device.copy(), LocalDateTime.now()))
            updateStatusImage(device.status)
        }
    }

    private fun updateStatusImage(status: Boolean) {
        if (status) {
            binding.deviceStatus.setImageResource(R.drawable.ic_on)
        } else {
            binding.deviceStatus.setImageResource(R.drawable.ic_off)
        }
    }

    companion object {
        fun from(parent: ViewGroup): DeviceViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDeviceBinding.inflate(layoutInflater, parent, false)
            return DeviceViewHolder(binding)
        }
    }
}
