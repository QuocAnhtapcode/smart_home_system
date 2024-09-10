package club.mobile.d21.smarthomesystem.fragment_device_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import club.mobile.d21.smarthomesystem.databinding.FragmentDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.model.TestData

class DeviceHistoryFragment: Fragment() {
    private var _binding: FragmentDeviceHistoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceHistoryBinding.inflate(inflater,container,false)
        val recyclerView = binding.historyList
        val adapter = DeviceHistoryAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter.submitList(TestData.deviceHistoryList)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}