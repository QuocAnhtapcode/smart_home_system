package club.mobile.d21.smarthomesystem.fragment_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.databinding.FragmentDetailBinding
import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData
import club.mobile.d21.smarthomesystem.util.Util
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: DataHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        adapter = DataHistoryAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val sortOptions = resources.getStringArray(R.array.sort_options)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortSpinner.adapter = spinnerAdapter
        mainViewModel.updateSortedDataHistory(1)
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                mainViewModel.updateSortedDataHistory(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                mainViewModel.updateSortedDataHistory(1)
            }
        }
        mainViewModel.sortedDataHistory.observe(viewLifecycleOwner) { sortedList ->
            adapter.submitList(sortedList)
            binding.recyclerView.scrollToPosition(0)
        }
        binding.searchButton.setOnClickListener {
            val searchOption = binding.searchSpinner.selectedItem.toString()
            val searchValue = binding.searchInput.text.toString().trim()
            performSearch(searchOption, searchValue)
        }
        mainViewModel.startUpdatingData()
        return binding.root
    }

    private fun performSearch(option: String, value: String) {
        mainViewModel.firebaseData.value!!.dataHistory.let { dataHistory ->
            val filteredList = when (option) {
                "All" -> dataHistory.filter {
                    matchesAllCriteria(Util.formatTimestamp(it.key),it.value, value)
                }
                "Time" -> dataHistory.filter {
                    matchesTime(Util.formatTimestamp(it.key), value)
                }
                "Temperature" -> dataHistory.filter {
                    matchesTemperature(it.value.temperature, value)
                }
                "Humidity" -> dataHistory.filter {
                    matchesHumidity(it.value.humidity, value)
                }
                "Light" -> dataHistory.filter {
                    matchesLight(it.value.light, value)
                }
                else -> dataHistory
            }.map { (key, value) -> Pair(key * 1000, value) }.sortedByDescending { it.first }
            adapter.submitList(filteredList)
        }
    }

    private fun matchesAllCriteria(time: String,sensorData: SensorData, value: String): Boolean {
        return matchesTime(time, value) ||
                matchesTemperature(sensorData.temperature, value) ||
                matchesHumidity(sensorData.humidity, value) ||
                matchesLight(sensorData.light, value)
    }

    private fun matchesTime(time: String, value: String): Boolean {
        return time.contains(value)
    }

    private fun matchesTemperature(temperature: Float, value: String): Boolean {
        return temperature.toString().contains(value)
    }

    private fun matchesHumidity(humidity: Float, value: String): Boolean {
        return humidity.toString().contains(value)
    }

    private fun matchesLight(light: Float, value: String): Boolean {
        return light.toString().contains(value)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
