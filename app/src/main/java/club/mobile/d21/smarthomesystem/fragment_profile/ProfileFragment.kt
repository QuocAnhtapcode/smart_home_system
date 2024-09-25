package club.mobile.d21.smarthomesystem.fragment_profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import club.mobile.d21.smarthomesystem.LoginActivity
import club.mobile.d21.smarthomesystem.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.logoutButton.setOnClickListener {
            logOut()
        }
        return binding.root
    }
    private fun logOut() {
        val sharedPref = requireContext().getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("USER_ID")
            apply()
        }
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}