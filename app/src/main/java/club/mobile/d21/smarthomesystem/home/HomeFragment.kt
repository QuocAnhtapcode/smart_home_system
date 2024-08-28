package club.mobile.d21.smarthomesystem.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import club.mobile.d21.smarthomesystem.R
import club.mobile.d21.smarthomesystem.data.Data
import club.mobile.d21.smarthomesystem.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        if(Data.isLightOn){
            binding.lightSwitch.setBackgroundResource(R.drawable.ic_light_on)
        }else{
            binding.lightSwitch.setBackgroundResource(R.drawable.ic_light_off)
        }
        binding.lightSwitch.setOnClickListener {
            if(Data.isLightOn){
                Data.isLightOn = false
                binding.lightSwitch.setBackgroundResource(R.drawable.ic_light_off)
            }else{
                Data.isLightOn = true
                binding.lightSwitch.setBackgroundResource(R.drawable.ic_light_on)
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}