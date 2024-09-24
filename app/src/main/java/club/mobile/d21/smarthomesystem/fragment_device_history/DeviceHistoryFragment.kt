package club.mobile.d21.smarthomesystem.fragment_device_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import club.mobile.d21.smarthomesystem.databinding.FragmentDeviceHistoryBinding
import club.mobile.d21.smarthomesystem.util.Util
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel

class DeviceHistoryFragment : Fragment() {
    private var _binding: FragmentDeviceHistoryBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
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
        binding.page.text = "Page " + Util.currentPage
        var startIndex = (Util.currentPage-1)*10
        var endIndex = Util.currentPage*10
        mainViewModel.firebaseData.observe(viewLifecycleOwner) { firebaseData ->
            val deviceHistoryList = firebaseData.deviceHistory.mapNotNull { (key, value) ->
                val timestamp = key.toLongOrNull()
                if (timestamp != null) Pair(timestamp, value) else null
            }.sortedByDescending { it.first }
            adapter.submitList(deviceHistoryList.subList(startIndex,endIndex))

            binding.nextButton.setOnClickListener {
                if((Util.currentPage+1)*10<=deviceHistoryList.size){
                    Util.currentPage++
                    binding.page.text = "Page " + Util.currentPage
                    startIndex =  (Util.currentPage-1)*10
                    endIndex = Util.currentPage*10
                    adapter.submitList(deviceHistoryList.subList(startIndex,endIndex))
                }else{
                    if(Util.currentPage*10<deviceHistoryList.size){
                        Util.currentPage++
                        binding.page.text = "Page " + Util.currentPage
                        startIndex =  (Util.currentPage-1)*10
                        endIndex = deviceHistoryList.size
                        adapter.submitList(deviceHistoryList.subList(startIndex,endIndex))
                    } else{
                        return@setOnClickListener
                    }
                }
            }
            binding.previousButton.setOnClickListener {
                if(Util.currentPage>1){
                    Util.currentPage--
                    binding.page.text = "Page " + Util.currentPage
                    startIndex =  (Util.currentPage-1)*10
                    endIndex = Util.currentPage*10
                    adapter.submitList(deviceHistoryList.subList(startIndex,endIndex))
                }else{
                    return@setOnClickListener
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}