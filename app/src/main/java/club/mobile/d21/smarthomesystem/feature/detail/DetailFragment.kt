package club.mobile.d21.smarthomesystem.feature.detail

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

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val dataHistoryViewModel: DataHistoryViewModel by activityViewModels()
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

        dataHistoryViewModel.updateSortedDataHistory(1)

        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                dataHistoryViewModel.updateSortedDataHistory(position)
                binding.recyclerView.scrollToPosition(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                dataHistoryViewModel.updateSortedDataHistory(1)
            }
        }

        dataHistoryViewModel.dataHistory.observe(viewLifecycleOwner) { dataList ->
            adapter.submitList(dataList)
            binding.recyclerView.scrollToPosition(0)
        }

        binding.searchButton.setOnClickListener {
            val searchOption = binding.searchSpinner.selectedItem.toString()
            val searchValue = binding.searchInput.text.toString().trim()
            dataHistoryViewModel.performSearch(searchOption, searchValue)
        }

        dataHistoryViewModel.startUpdatingData()

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
