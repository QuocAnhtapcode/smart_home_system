package club.mobile.d21.smarthomesystem.fragment_device_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import club.mobile.d21.smarthomesystem.databinding.FragmentDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.util.Util.currentPage
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel

class DeviceHistoryFragment : Fragment() {
    private var _binding: FragmentDeviceHistoryBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val limit = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceHistoryBinding.inflate(inflater, container, false)
        val recyclerView = binding.historyList
        val adapter = DeviceHistoryAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        mainViewModel.deviceHistory.observe(viewLifecycleOwner) { deviceHistory ->
            val deviceHistoryList = deviceHistory.map { (key, value) -> Pair(key, value) }
                .sortedByDescending { it.first }
            adapter.submitList(deviceHistoryList)
        }

        binding.nextButton.setOnClickListener {
            mainViewModel.fetchDeviceHistoryData(limit, "next")
            if (mainViewModel.deviceHistory.value?.size == limit) {
                currentPage++
                binding.page.text = "Page $currentPage"
            }
        }

        binding.previousButton.setOnClickListener {
            if (currentPage > 1) {
                mainViewModel.fetchDeviceHistoryData(limit, "previous")
                currentPage--
                binding.page.text = "Page $currentPage"
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
