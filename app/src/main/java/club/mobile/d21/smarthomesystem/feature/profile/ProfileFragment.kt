package club.mobile.d21.smarthomesystem.feature.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import club.mobile.d21.smarthomesystem.LoginActivity
import club.mobile.d21.smarthomesystem.databinding.FragmentProfileBinding
import android.text.style.ClickableSpan
import androidx.core.content.ContextCompat
import club.mobile.d21.smarthomesystem.R
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
        val spannableGithub = SpannableString("QuocAnhtapcode")
        val clickableGithub = object : ClickableSpan() {
            override fun onClick(widget: View){
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/QuocAnhtapcode/smart_home_system.git"))
                startActivity(browserIntent)
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.main_color)
                ds.isUnderlineText = true
            }
        }
        spannableGithub.setSpan(clickableGithub, 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.githubLink.text = spannableGithub
        binding.githubLink.movementMethod = LinkMovementMethod.getInstance()

        val spannableApiDocs = SpannableString("API Docs")
        val clickableApiDocs = object : ClickableSpan() {
            override fun onClick(widget: View){
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://documenter.getpostman.com/view/33082190/2sAXxJiF2f"))
                startActivity(browserIntent)
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.main_color)
                ds.isUnderlineText = true
            }
        }
        spannableApiDocs.setSpan(clickableApiDocs,0,8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.apiLink.text = spannableApiDocs
        binding.apiLink.movementMethod = LinkMovementMethod.getInstance()

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