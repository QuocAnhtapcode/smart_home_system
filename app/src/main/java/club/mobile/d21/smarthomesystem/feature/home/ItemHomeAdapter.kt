package club.mobile.d21.smarthomesystem.feature.home

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import club.mobile.d21.smarthomesystem.data.model.chart.Chart
import club.mobile.d21.smarthomesystem.data.model.device.Device

class ItemHomeAdapter(
    private val deviceClickListener: DeviceClickListener
) : ListAdapter<Any, RecyclerView.ViewHolder>(HomeDiffCallback()) {

    companion object {
        const val DEVICE = 1
        const val CHART = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Device -> DEVICE
            is Chart -> CHART
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DEVICE -> DeviceViewHolder.from(parent, deviceClickListener)
            CHART -> ChartViewHolder.from(parent)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DeviceViewHolder -> holder.bind(getItem(position) as Device)
            is ChartViewHolder -> holder.bind(getItem(position) as Chart)
        }
    }
}
