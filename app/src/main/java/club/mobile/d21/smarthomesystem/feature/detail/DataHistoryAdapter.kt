package club.mobile.d21.smarthomesystem.feature.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import club.mobile.d21.smarthomesystem.databinding.ItemDataHistoryBinding
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData

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