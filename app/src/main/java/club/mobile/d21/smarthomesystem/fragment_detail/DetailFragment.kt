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
        }
        mainViewModel.startUpdatingData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
