package club.mobile.d21.smarthomesystem

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import club.mobile.d21.smarthomesystem.databinding.ActivityMainBinding
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            mainViewModel.setUserId(uid)
            mainViewModel.startUpdatingData()
        } else {
            // Xử lý trường hợp người dùng chưa đăng nhập
        }
        val navView = binding.bottomNavView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }
    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.stopUpdatingData()
    }
}