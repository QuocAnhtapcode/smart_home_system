package club.mobile.d21.smarthomesystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import club.mobile.d21.smarthomesystem.databinding.ActivityMainBinding
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUserLogin()
        setupNavigation()
        setupViewModel()
    }
    private fun checkUserLogin() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            navigateToLogin()
        }
    }
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
    }
    private fun setupViewModel() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            mainViewModel.setUserId(user.uid)
            mainViewModel.fetchDeviceHistoryData(10, "next")
            mainViewModel.startUpdatingData()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.stopUpdatingData()
    }
}
