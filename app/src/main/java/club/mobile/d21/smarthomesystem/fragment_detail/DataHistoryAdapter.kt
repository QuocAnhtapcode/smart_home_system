package club.mobile.d21.smarthomesystem.fragment_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import club.mobile.d21.smarthomesystem.databinding.ItemDataHistoryBinding
import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData
import club.mobile.d21.smarthomesystem.util.DataHistoryDiffCallback
import club.mobile.d21.smarthomesystem.viewholder.DataHistoryViewHolder

class DataHistoryAdapter
    :ListAdapter<Pair<Long, SensorData>, DataHistoryViewHolder>(DataHistoryDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHistoryViewHolder {
        val binding = ItemDataHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DataHistoryViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DataHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}