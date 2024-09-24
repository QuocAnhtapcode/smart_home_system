package club.mobile.d21.smarthomesystem.fragment_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.FragmentHomeBinding
import club.mobile.d21.smarthomesystem.model.chart.Chart
import club.mobile.d21.smarthomesystem.model.device.Device
import club.mobile.d21.smarthomesystem.viewholder.DeviceClickListener
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel

class HomeFragment : Fragment(), DeviceClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ItemHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ItemHomeAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        mainViewModel.firebaseData.observe(viewLifecycleOwner) { firebaseData ->
            binding.lightText.text =
                getString(R.string.light_value, firebaseData.currentData.light.toInt())
            binding.humidityText.text =
                getString(R.string.humidity_value, firebaseData.currentData.humidity)
            binding.temparatureText.text =
                getString(R.string.temperature_value, firebaseData.currentData.temperature)

            val dataHistoryList = firebaseData.dataHistory.mapNotNull { (key, value) ->
                val timestamp = key.toLongOrNull()
                if (timestamp != null) Pair(timestamp, value) else null
            }.sortedByDescending { it.first }
            val subListSize = if (dataHistoryList.size > 10) 10 else dataHistoryList.size
            val subList = dataHistoryList.subList(0, subListSize)
            adapter.submitList(
                listOf(
                    Device("Light", R.drawable.ic_big_light, firebaseData.currentDevice.light),
                    Device("Air Conditioner", R.drawable.ic_big_ac, firebaseData.currentDevice.ac),
                    Device("Television", R.drawable.ic_big_tv, firebaseData.currentDevice.tv),
                    Chart("Temperature (°C)", subList),
                    Chart("Humidity (%)", subList),
                    Chart("Light (lux)", subList)
                )
            )
        }
        mainViewModel.startUpdatingData()
        return binding.root
    }

    override fun onDeviceClicked(device: Device) {
        when (device.name) {
            "Light" -> mainViewModel.logLedStatus("light", device.status)
            "Air Conditioner" -> mainViewModel.logLedStatus("ac", device.status)
            "Television" -> mainViewModel.logLedStatus("tv", device.status)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
