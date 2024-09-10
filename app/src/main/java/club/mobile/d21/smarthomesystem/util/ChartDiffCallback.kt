package club.mobile.d21.smarthomesystem.util

import androidx.recyclerview.widget.DiffUtil
import club.mobile.d21.smarthomesystem.model.chart.Chart

class ChartDiffCallback: DiffUtil.ItemCallback<Chart>(){
    override fun areItemsTheSame(oldItem: Chart, newItem: Chart): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Chart, newItem: Chart): Boolean {
        return oldItem == newItem
    }
}