package club.mobile.d21.smarthomesystem.feature.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.core.util.Util
import club.mobile.d21.smarthomesystem.data.model.chart.Chart
import club.mobile.d21.smarthomesystem.data.model.device.Device
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData
import club.mobile.d21.smarthomesystem.databinding.FragmentHomeBinding
import club.mobile.d21.smarthomesystem.feature.device_history.DeviceHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(), DeviceClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val deviceHistoryViewModel: DeviceHistoryViewModel by activityViewModels()
    private lateinit var adapter: ItemHomeAdapter

    private val chartDataList = mutableListOf<Pair<Long, SensorData>>()
    private val maxChartSize = 20

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = ItemHomeAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        handler.post(timeUpdater)
        homeViewModel.combinedLiveData.observe(viewLifecycleOwner) { (dataHistory, currentDevice) ->
            dataHistory?.let { dataHistoryMap ->
                val dataHistoryList = dataHistoryMap.mapNotNull { (key, value) ->
                    Pair(key, value)
                }.sortedByDescending { it.first }

                binding.lightText.text = getString(R.string.light_value, dataHistoryList[0].second.light.toInt())
                binding.lightText.setTextColor(ContextCompat.getColor(requireContext(),
                    Util.getColorBasedOnLightValue(dataHistoryList[0].second.light)))

                binding.humidityText.text = getString(R.string.humidity_value, dataHistoryList[0].second.humidity)
                binding.humidityText.setTextColor(ContextCompat.getColor(requireContext(),
                    Util.getColorBasedOnHumidityValue(dataHistoryList[0].second.humidity)))

                binding.temparatureText.text = getString(R.string.temperature_value, dataHistoryList[0].second.temperature)
                binding.temparatureText.setTextColor(ContextCompat.getColor(requireContext(),
                    Util.getColorBasedOnTemperatureValue(dataHistoryList[0].second.temperature)))

                updateChartWithNewData(dataHistoryList)

                currentDevice?.let { device ->
                    adapter.submitList(
                        listOf(
                            Device("Light", R.drawable.ic_big_light, device.light),
                            Device("Air Conditioner", R.drawable.ic_big_ac, device.ac),
                            Device("Television", R.drawable.ic_big_tv, device.tv),
                            Chart("Temperature (°C)", chartDataList),
                            Chart("Humidity (%)", chartDataList),
                            Chart("Light (lux)", chartDataList)
                        )
                    )
                }
            }
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateChartWithNewData(dataHistoryList: List<Pair<Long, SensorData>>) {
        chartDataList.add(dataHistoryList[0])
        if (chartDataList.size > maxChartSize) {
            chartDataList.removeAt(0)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timeUpdater)
        _binding = null
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L // Cập nhật mỗi giây

    private val timeUpdater = object : Runnable {
        override fun run() {
            if (_binding != null) {
                updateCurrentTime()
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    private fun updateCurrentTime() {
        val currentTime = System.currentTimeMillis()
        val formatter = SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val formattedTime = formatter.format(Date(currentTime))
        binding.dateText.text = formattedTime
    }

    override fun onDeviceClicked(device: Device) {
        when (device.name) {
            "Light" -> homeViewModel.logLedStatus("light", device.status)
            "Air Conditioner" -> homeViewModel.logLedStatus("ac", device.status)
            "Television" -> homeViewModel.logLedStatus("tv", device.status)
        }
        deviceHistoryViewModel.fetchDeviceHistory(10, "")
    }
}
