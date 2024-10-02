package club.mobile.d21.smarthomesystem.feature.device_history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import club.mobile.d21.smarthomesystem.databinding.FragmentFilteredDeviceHistoryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilteredDeviceHistoryDialogFragment : DialogFragment() {

    private var _binding: FragmentFilteredDeviceHistoryBinding? = null
    private val binding get() = _binding!!
    private val deviceHistoryViewModel: DeviceHistoryViewModel by activityViewModels()

    private var selectedDeviceType: String = "All"
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilteredDeviceHistoryBinding.inflate(inflater, container, false)

        setupDeviceTypeSpinner()
        setupDatePicker()
        setupFilterButton()

        return binding.root
    }

    private fun setupDeviceTypeSpinner() {
        val deviceTypeName = listOf("All", "Light", "AC", "TV")
        val deviceTypes = listOf("All", "light", "ac", "tv")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, deviceTypeName)
        binding.deviceTypeSpinner.adapter = adapter

        binding.deviceTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedDeviceType = deviceTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = format.format(calendar.time)
            binding.dateText.text = selectedDate
        }

        binding.selectDateButton.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            deviceHistoryViewModel.fetchDeviceHistoryByFilters(selectedDate,selectedDeviceType)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
