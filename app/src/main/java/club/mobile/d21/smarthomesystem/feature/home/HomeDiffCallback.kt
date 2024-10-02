package club.mobile.d21.smarthomesystem.feature.home

import androidx.recyclerview.widget.DiffUtil
import club.mobile.d21.smarthomesystem.data.model.chart.Chart
import club.mobile.d21.smarthomesystem.data.model.device.Device

class HomeDiffCallback : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is Device && newItem is Device -> oldItem == newItem
            oldItem is Chart && newItem is Chart -> oldItem == newItem
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is Device && newItem is Device -> oldItem.status == newItem.status
            oldItem is Chart && newItem is Chart -> oldItem.label == newItem.label
            else -> false
        }
    }
}