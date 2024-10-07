package club.mobile.d21.smarthomesystem.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.ItemDeviceBinding
import club.mobile.d21.smarthomesystem.data.model.device.Device

interface DeviceClickListener {
    fun onDeviceClicked(device: Device)
}
class DeviceViewHolder(private val binding: ItemDeviceBinding,
                       private val listener: DeviceClickListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(device: Device) {
        binding.deviceText.text = device.name
        binding.deviceImage.setImageResource(device.image)
        updateStatusImage(device.status)
        binding.deviceStatus.setOnClickListener {
            device.status = !device.status
            listener.onDeviceClicked(device)
        }
    }

    private fun updateStatusImage(status: Boolean) {
        val imageRes = if (status) R.drawable.ic_on else R.drawable.ic_off
        binding.deviceStatus.setImageResource(imageRes)
    }

    companion object {
        fun from(parent: ViewGroup, listener: DeviceClickListener): DeviceViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDeviceBinding.inflate(layoutInflater, parent, false)
            return DeviceViewHolder(binding, listener)
        }
    }
}
